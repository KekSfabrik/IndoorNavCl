/*
 * Copyright (C) 2015 Jan "KekS" M.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package de.hsmainz.gi.indoornavcl.positioning;

import android.util.Log;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import org.ejml.simple.SimpleMatrix;

import java.util.*;

/**
 * Wrapper to parse the observations gathered by the input device and filter them for the parameter estimation.
 *
 * @author Saufaus, 31.01.2015.
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 04.02.15.
 */
public class LocatorImplOne
        implements Locator {

    private static final String     TAG = LocatorImplOne.class.getSimpleName();

    private int                 SRID;
    private TinyCoordinate      initialPosition;
    private ParameterEstimation paramEst;


    public LocatorImplOne(){
        this.SRID = 4326;
        this.initialPosition = new TinyCoordinate(0,0,0);
        this.paramEst = new ParameterEstimation();
    }

    /**
     * Basic function to filter the input map. Will produce a matrix one row per found beacon containing: X-,Y-,Z-coordinates and the the median of the observed pseudoranges:
     *
     * Created by Saufaus on 31.01.2015.
     */
    private SimpleMatrix locatorToObservations(Map<WkbLocation, Measurement> locations) {
        int u = 4;
        SimpleMatrix output = new SimpleMatrix(locations.size(), u); // Set size
        int i = 0;
        Map<Double, WkbLocation> mapByDistance = new TreeMap<>();
        for (Map.Entry<WkbLocation, Measurement> loc: locations.entrySet()) {
            if (loc.getValue() != null) {
                Point point = loc.getKey().getCoord().getPoint();
                if (this.SRID == 0) {
                    SRID = point.getSRID();
                } else if (this.SRID != point.getSRID()) {
                    Log.d(TAG, "Warning, varying SRIDs found (" + this.SRID + " ≠ " + point.getSRID() + ")");
                }
                double dist = DistanceCalculator.calculateDistance(loc.getValue().getTxPower(), loc.getValue().getRssi());
                output.set(i, 0, point.getX());     // X-coordinate
                output.set(i, 1, point.getY());     // Y-coordinate
                output.set(i, 2, 2.5d);             // Z-coordinate
                output.set(i, 3, dist);             // calculated distance
                i++;
                mapByDistance.put(dist, loc.getKey());
            }
        }
        if (mapByDistance.size() >= 3) {
            i = 0;
            TinyCoordinate   a,      b,      c;
            double  dA,     dB,     dC,
                    cA=0.2, cB=0.2, cC=0.2;
            Iterator<Map.Entry<Double, WkbLocation>> it = mapByDistance.entrySet().iterator();
            Map.Entry<Double, WkbLocation> e = it.next();
            Point p = e.getValue().getCoord().getPoint();
            if (e == null || e.getValue() == null) {
                Log.d(TAG, "no location candidate for a");
            } else {
                a = new TinyCoordinate(p);
                dA = e.getKey();
                e = it.next();
                p = e.getValue().getCoord().getPoint();
                if (e == null || e.getValue() == null) {
                    Log.d(TAG, "no location candidate for b -> initialposition = a");
                    initialPosition = new TinyCoordinate(p);
                } else {
                    b = new TinyCoordinate(p);
                    dB = e.getKey();
                    e = it.next();
                    p = e.getValue().getCoord().getPoint();
                    if (e == null || e.getValue() == null) {
                        Log.d(TAG, "no location candidate for c -> initialposition between a and b");
                        initialPosition = new TinyCoordinate();
                        double ratio = (dA + dB) / 2;
                        initialPosition.x = (dA * a.x + dB * b.x) / ratio;
                        initialPosition.y = (dA * a.y + dB * b.y) / ratio;
                        initialPosition.z = (dA * a.z + dB * b.z) / ratio;
                    } else {
                        c = new TinyCoordinate(p);
                        dC = e.getKey();
                        initialPosition = position(a, b, c, dA, dB, dC, cA, cB, cC);
                    }
                }
            }
        }
        return output;
    }

    /**
     *
     * based on http://stackoverflow.com/questions/20332856/triangulate-example-for-ibeacons
     * @param a     Coordinate a
     * @param b     Coordinate b
     * @param c     Coordinate c
     * @param dA    Distance to a
     * @param dB    Distance to b
     * @param dC    Distance to c
     * @param cA    Confidence of a (0 < cA < 1)
     * @param cB    Confidence of b (0 < cB < 1)
     * @param cC    Confidence of c (0 < cC < 1)
     * @return      the position based on the 3 strongest signals
     */
    private TinyCoordinate position(
        TinyCoordinate a, TinyCoordinate b, TinyCoordinate c,
        double dA, double dB, double dC,
        double cA, double cB, double cC
    ) {
        double sum = (cA*dA) + (cB*dB) + (cC*dC);
        dA = cA*dA/sum;
        dB = cB*dB/sum;
        dC = cC*dC/sum;

        TinyCoordinate p = new TinyCoordinate();
        p.x = (a.x * dA) + (b.x * dB) + (c.x * dC);
        p.y = (a.y * dA) + (b.x * dB) + (c.x * dC);
        p.z = (a.z * dA) + (b.x * dB) + (c.x * dC);
        return p;
    }

    /**
     * Function that converts the output from an estimator to a JTS point.
     *
     * Created by Saufaus on 31.01.2015.
     */
    private Point asPoint(SimpleMatrix input){
        Point output = new GeometryFactory()
                .createPoint(
                    new Coordinate(
                        input.get(0, 0), input.get(1, 0), input.get(2, 0)
                    )
                );
        output.setSRID(this.SRID);
        System.out.println("Point created: " + output.getCoordinate().toString());
        return output;
    }

    /**
     * Calculate the {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation} of the Client
     * based on the visible {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon}s and their
     * {@link de.hsmainz.gi.indoornavcl.positioning.Measurement}s.
     *
     * @param locations a Map of Locations with a List of their Measurements
     * @return the WkbLocation of the Client
     */
    @Override
    public Point getLocation(Map<WkbLocation, Measurement> locations) {
        SimpleMatrix sm = this.locatorToObservations(locations);
        paramEst = new ParameterEstimation();
        paramEst.setLastPosition(new double[]{initialPosition.x, initialPosition.y, initialPosition.z});
        Log.v(TAG, "Attempting to estimate for initialPosition " + initialPosition.toString());
        SimpleMatrix res = paramEst.estimate(sm);
        return this.asPoint(res);
    }

}