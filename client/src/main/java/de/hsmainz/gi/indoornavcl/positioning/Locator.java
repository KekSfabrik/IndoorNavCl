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
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;

import java.util.Map;


/**
 * Interface to be shared by all Implementations that determine the Position based on a {@link java.util.Map} of
 * {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation}s and the matching
 * {@link de.hsmainz.gi.indoornavcl.positioning.Measurement}s.
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public interface Locator {

    /**
     * Calculate the {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation} of the Client
     * based on the visible {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon}s and their
     * {@link de.hsmainz.gi.indoornavcl.positioning.Measurement}s.
     *
     * @param   locations   a Map of Locations with a List of their Measurements
     * @return  the WkbLocation of the Client
     */
    public Point getLocation(Map<WkbLocation, Measurement> locations);

}
