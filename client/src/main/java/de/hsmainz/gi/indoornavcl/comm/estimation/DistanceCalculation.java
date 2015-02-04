package de.hsmainz.gi.indoornavcl.comm.estimation;

/**
 * Created by Saufaus on 29.01.2015.
 */



public class DistanceCalculation {

    /**
     * Basic calculation of distance from RSSI and txPower.
     * Source: http://stackoverflow.com/a/20434019
     *
     * @param   rssi            received signal strength indicator
     * @param   txPower         transmit power
     */

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double distance =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return distance;
        }
    }



}
