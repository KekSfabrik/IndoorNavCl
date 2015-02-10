package de.hsmainz.gi.indoornavcl.comm.estimation;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.positioning.Measurement;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Wrapper to parse the observations gathered by the input device and filter them for the parameter estimation.
 *
 * Created by Saufaus on 31.01.2015.
 */
public class Wrapper {

    /**
     * Basic function to filter the input map. Will produce a matrix one row per found beacon containing: X-,Y-,Z-coordinates and the the median of the observed pseudoranges:
     *
     * Created by Saufaus on 31.01.2015.
     */
    public static SimpleMatrix locatorToObservations (Map<WkbLocation, Measurement> locations){

        int u = 4;
        int k = 0;                                                                                                      // Iterator for the output matrix

        double thresholdDistance = 6.0;                                                                                 // Distance up to which an observation can be used
        double mediandistance = -1;

        ArrayList<Integer> sortedObservations = new ArrayList<>();
        SimpleMatrix output = new SimpleMatrix(locations.keySet().size(), u);                                           // Set size
        ArrayList<Measurement> rawObservations = new ArrayList<>();

        // Scan map for individual beacons amd create an array from them.
        ArrayList<WkbLocation> foundLocations = new ArrayList<>(locations.keySet());


        for (int i=0;i<foundLocations.size();i++) {

            rawObservations.addAll(Arrays.asList(locations.get(foundLocations.get(i))));

            // if more then one observation is found, filter for median.
            if(rawObservations.size() > 1){

                // Sort raw observations
                for (int l=0; l< rawObservations.size();l++) {
                    sortedObservations.add( (int)rawObservations.get(i).getRssi());
                }
                Collections.sort(sortedObservations);                                                                   //
                System.out.println("Number of sorted observations before filtering for median: " + sortedObservations.size());

                // if list has even size, form median.
                if (rawObservations.size() % 2 == 0) {
                    mediandistance = (sortedObservations.get(sortedObservations.size() / 2) + sortedObservations.get(( sortedObservations.size() / 2) + 1)) / 2;
                } else {
                    mediandistance = sortedObservations.get((sortedObservations.size() + 1) / 2);
                }
            } else if (rawObservations.size() == 1){
                mediandistance = sortedObservations.get(0);
            }

            System.out.println("Number of sorted observations after filtering for median: " + sortedObservations.size());
            for (int l=0;l<sortedObservations.size();l++){
                System.out.println(sortedObservations.get(l).toString());
            }

            // Check if found median observation is suitable
            if (DistanceCalculation.calculateDistance( (int) rawObservations.get(0).getTxPower(), mediandistance) <= thresholdDistance) {
                // Add the position of the beacon and the calculated distance from the observed pseudoranges to output matrix
                output.set(k, 0, foundLocations.get(i).getCoord().getPoint().getX());                                          // X-coordinate
                output.set(k, 1, foundLocations.get(i).getCoord().getPoint().getY());                                          // Y-coordinate
                output.set(k, 2, 2.5);                                                                                         // Z-coordinate
                output.set(k, 3, DistanceCalculation.calculateDistance( (int) rawObservations.get(0).getTxPower(), mediandistance));  // calculated distance
                k++;
            }

            // Null rawObservations for the next pass.
            rawObservations.clear();
        }
        return output;
    }

    /**
     * Function that converts the output from an estimator to a JTS point. Automatically sets SRID to 4326.
     *
     * Created by Saufaus on 31.01.2015.
     */
    public static Point observationsToJTSPoint (SimpleMatrix input){
        GeometryFactory gf = new GeometryFactory();
        Point output = gf.createPoint(new Coordinate(input.get(0,0),input.get(1,0),input.get(2,0)));
        System.out.println("Point created: " + output.getCoordinate().toString());
        output.setSRID(4326);
        return output;
    }
}
