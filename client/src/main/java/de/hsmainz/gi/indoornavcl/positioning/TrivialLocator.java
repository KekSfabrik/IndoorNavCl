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

import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.util.Utilities;

import java.util.Map;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 12.02.15.
 */
public class    TrivialLocator
    implements  Locator {


    private TinyCoordinate getWeightedMean(Map<WkbLocation, Measurement> locations) {
        double x = 0, y = 0, rssiByTxPowerSum = 0;
        for (Map.Entry<WkbLocation, Measurement> loc: locations.entrySet()) {
            if (loc.getValue() != null) {
                Point point = loc.getKey().getCoord().getPoint();
                Measurement msm = loc.getValue();
                double rssiByTxPower = msm.getTxPower() - msm.getRssi();
                if (rssiByTxPower <= 0) {
                    rssiByTxPower = 1;
                }
                rssiByTxPower = 1.0 / rssiByTxPower;
                x += point.getX() * rssiByTxPower;
                y += point.getY() * rssiByTxPower;
                rssiByTxPowerSum += rssiByTxPower;
            }
        }
        x /= rssiByTxPowerSum;
        y /= rssiByTxPowerSum;
        double randomHeight = 0.7d + 0.1 * Utilities.R.nextInt(10);
        return new TinyCoordinate(x, y, randomHeight);
    }


    /**
     * Calculate the {@link de.hsmainz.gi.types.WkbLocation} of the Client
     * based on the visible {@link de.hsmainz.gi.types.Beacon}s and their
     * {@link de.hsmainz.gi.indoornavcl.positioning.Measurement}s.
     *
     * @param locations a Map of Locations with a List of their Measurements
     * @return the WkbLocation of the Client
     */
    @Override
    public Point getLocation(Map<WkbLocation, Measurement> locations) {
        return getWeightedMean(locations).asPoint(4326);
    }
}
