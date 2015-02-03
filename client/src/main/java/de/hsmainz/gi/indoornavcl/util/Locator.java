package de.hsmainz.gi.indoornavcl.util;

import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;

import java.util.Map;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public interface Locator {

    /**
     * Calculate the {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation} of the Client
     * based on the visible {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon}s and their
     * {@link Measurement}s.
     *
     * @param   locations   a Map of Locations with a List of their Measurements
     * @return  the WkbLocation of the Client
     */
    public Point getLocation(Map<WkbLocation, Measurement> locations);

}
