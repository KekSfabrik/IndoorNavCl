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

/**
 * Created by Saufaus on 29.01.2015.
 */
public class DistanceCalculator {

    /**
     * Basic calculation of distance from RSSI and txPower.
     * Source: http://stackoverflow.com/a/20434019
     *
     * @param   rssi            received signal strength indicator
     * @param   txPower         transmit power
     */
    public static double calculateDistance(double txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        }
        else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    /**
     * Calibrated calculation of distance from RSSI and txPower.
     * Should not be used by 3rd parties because it only fits the beacons, mobile phone and environment of our project.
     * This calculation is baes on a 3rd grade polynom approximation.
     * Source: http://stackoverflow.com/a/20434019
     *
     * @param   rssi            received signal strength indicator
     * @param   txPower         transmit power
     * Created by Saufaus on 10.02.2015.
     */

    public static double calculateDistancePoly3(double txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        }
        else {
            // distance = a * ratio^3 + b * ratio^2 + c * ratio + d
            return (-0.000327) * Math.pow(ratio, 3) + (-0.066887) * Math.pow(ratio,2) + (-4.518441) * ratio + (-97.864651);
        }
    }

}