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
import de.hsmainz.gi.indoornavcl.util.StringUtils;
import org.ejml.simple.SimpleMatrix;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 18.02.15.
 */
public class    LocatorComboImpl
    implements  Locator {

    private static final String TAG = LocatorComboImpl.class.getSimpleName();

    private int                 SRID;
    private TinyCoordinate      initialPosition;
    private ParameterEstimation paramEst;
    private int                 measurementCount;


    public LocatorComboImpl(){
        this.SRID = 4326;
        this.initialPosition = new TinyCoordinate(0,0,0);
        this.paramEst = new ParameterEstimation();
        this.measurementCount = 0;
    }

    /**
     * Basic function to filter the input map. Will produce a matrix one row per found beacon containing: X-,Y-,Z-coordinates and the the median of the observed pseudoranges:
     *
     * Created by Saufaus on 31.01.2015.
     */
    private SimpleMatrix locatorToObservations(Map<WkbLocation, Measurement> locations) {
        int u = 4;
        SimpleMatrix output = new SimpleMatrix(locations.size(), u); // Set size
        this.measurementCount = 0;
        Map<Double, WkbLocation> mapByDistance = new TreeMap<>();
        double x = 0, y = 0, rssiByTxPowerSum = 0;
        for (Map.Entry<WkbLocation, Measurement> loc: locations.entrySet()) {
            if (loc.getValue() != null) {
                double dist = DistanceCalculator.calculateDistancePoly3(loc.getValue().getTxPower(), loc.getValue().getRssi());
                if (dist <= 11) {
                    Point point = loc.getKey().getCoord().getPoint();
                    if (this.SRID == 0) {
                        SRID = point.getSRID();
                    } else if (this.SRID != point.getSRID()) {
                        Log.d(TAG, "Warning, varying SRIDs found (" + this.SRID + " ≠ " + point.getSRID() + ")");
                    }
                    Measurement msm = loc.getValue();
                    double rssiByTxPower = msm.getTxPower() - msm.getRssi();
                    if (rssiByTxPower <= 0) {
                        rssiByTxPower = 1;
                    }
                    rssiByTxPower = 1.0 / rssiByTxPower;
                    x += point.getX() * rssiByTxPower;
                    y += point.getY() * rssiByTxPower;
                    rssiByTxPowerSum += rssiByTxPower;
                    output.set(this.measurementCount, 0, point.getX());     // X-coordinate
                    output.set(this.measurementCount, 1, point.getY());     // Y-coordinate
                    output.set(this.measurementCount, 2, 2.5d);             // Z-coordinate
                    output.set(this.measurementCount, 3, dist);             // calculated distance
                    Log.v(TAG, "d2p: " + dist + "\t" + StringUtils.toString(loc.getKey().getId()) + " " + StringUtils.toString(loc.getKey().getCoord()));
                    this.measurementCount++;
                    mapByDistance.put(dist, loc.getKey());
                }
            }
        }
        output.reshape(this.measurementCount, u);
        x /= rssiByTxPowerSum;
        y /= rssiByTxPowerSum;
        initialPosition = new TinyCoordinate(x, y, 1.5);

        return output;
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
        switch (this.measurementCount) {
            case 0: return null;
            case 1: return initialPosition.asPoint(this.SRID);
            case 2: return initialPosition.asPoint(this.SRID);
            case 3: return initialPosition.asPoint(this.SRID);
            default: {
                paramEst = new ParameterEstimation();
                paramEst.setLastPosition(new double[]{initialPosition.x, initialPosition.y, initialPosition.z});
                Log.v(TAG, "Attempting to estimate for initialPosition " + initialPosition.toString());
                try {
                    SimpleMatrix res = paramEst.estimate(sm);
                    return this.asPoint(res);
                } catch (Exception ex) {
                    Log.w(TAG, "Could not determine position", ex);
                    return null;
                }
            }
        }
    }

}