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

package de.hsmainz.gi.indoornavcl.util;

import de.hsmainz.gi.indoornavcl.comm.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *  Class with String Utilities since none of the {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity} implementors
 *  could override {@link Object#toString} because of XML-marshalling/unmarshalling.
 *
 * @author Jan "KekS" M. <a href="mailto:keks@keksfabrik.eu">mail</a>, 23.01.2015
 */
public abstract class StringUtils {

    public static <T extends IndoorNavEntity> String listAll(Collection<T> collection) {
        List<String> strings = new ArrayList<>();
        if (collection != null && !collection.isEmpty()) {
            for (T entity : collection) {
                strings.add(toString(entity));
            }
            return Arrays.toString(strings.toArray(new String[0]));
            //return list.parallelStream().map(i -> toString(i)).collect(Collectors.toList()).toArray(new String[0]);
        }
        else {
            return "";
        }
    }

//    public static String toString(Class clazz, IndoorNavEntity entity) {
//        if (entity.getClass().equals(clazz))
//            return toString((clazz.getName()) entity);
//    }

    public static String toString(Beacon b) {
        if (b == null)
            return "NPE (Beacon)";
        if (b.getUuid() == null)
            return "NPE Beacon:\t" + b.getId() + ", 'null', " + b.getMajor() + ", " + b.getMinor();
        return "Beacon:\t" + b.getId() + ", '" + b.getUuid() + "', " + b.getMajor() + ", " + b.getMinor();
    }

    public static String toString(WkbLocation l) {
        if (l == null || l.getBeacon().getUuid() == null || l.getSite().getName() == null)
            return "NPE (WkbLocation)";
        Beacon b = l.getBeacon();
        Site s = l.getSite();
        WkbPoint p = l.getCoord();
        return "WkbLocation:\t" + toString(l.getId())
                + " (" + (b != null ? (b.getUuid() != null ? b.getUuid() : "NULL")+ ", " + b.getMajor() + ", " + b.getMinor() : "NULL")
                + ") at " + (s != null ? s.getName() : "NULL")
                + " @ " + (p != null ? p.toText() : "NULL");
    }


    public static String toString(WkbPoint p) {
        if (p == null || p.getWkb() == null)
            return "NPE (WkbPoint)";
        return "WkbPoint:\t" + p.getWkb() + " = " + new com.vividsolutions.jts.io.WKTWriter(3).write(p.getPoint());
    }

    public static String toString(LocationId lid) {
        if (lid == null)
            return "NPE (LocationId)";
        return "PK: " + lid.getSite() + ", " + lid.getBeaconId();
    }

    public static String toString(Site s) {
        if (s == null || s.getName() == null)
            return "NPE (Site)";
        return "Site:\t\t" + s.getSite() + ", '" + s.getName() + "'";
    }


    public static String toString(IndoorNavEntity e) {
        if (e instanceof Beacon)
            return toString((Beacon) e);
        if (e instanceof WkbPoint)
            return toString((WkbPoint) e);
        if (e instanceof LocationId)
            return toString((LocationId) e);
        if (e instanceof WkbLocation)
            return toString((WkbLocation) e);
        if (e instanceof Site)
            return toString((Site) e);
        return toString(e);
    }

    public static String toString(Object o) {
        return o != null ? o.toString() : "";
    }
}
