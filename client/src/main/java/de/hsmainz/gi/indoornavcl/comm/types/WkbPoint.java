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

package de.hsmainz.gi.indoornavcl.comm.types;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import de.hsmainz.gi.indoornavcl.util.StringUtils;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Objects;

/**
 *
 * @author Jan "KekS" M. <a href="mailto:keks@keksfabrik.eu">mail</a>, 29.01.2015
 */
public class WkbPoint
        implements KvmSerializable,
                    Serializable,
                    Comparable,
                    IndoorNavEntity {

    private static final WKBReader  reader = new WKBReader();
    private static final WKBWriter  writer = new WKBWriter(3, true);

    private String                  wkb;

    public WkbPoint() { }

    public WkbPoint(String wkb) {
        this.wkb = wkb;
    }

    public WkbPoint(com.vividsolutions.jts.geom.Point point) {
        this.wkb = WKBWriter.toHex(writer.write(point));
    }

// <editor-fold desc="old implementation" defaultstate="collapsed">
//    private int     SRID;
//    private double  x,
//                    y,
//                    z;
//
//    public WkbPoint(com.vividsolutions.jts.geom.WkbPoint point) {
//        this.SRID = point.getSRID();
//        this.x = point.getCoordinate().x;
//        this.y = point.getCoordinate().y;
//        this.z = point.getCoordinate().z;
//    }
//
//    public WkbPoint(int SRID, double x, double y, double z) {
//        this.SRID = SRID;
//        this.x = x;
//        this.y = y;
//        this.z = z;
//    }
//
//    /**
//     * @return the SRID
//     */
//    public int getSRID() {
//        return SRID;
//    }
//
//    /**
//     * @param SRID the SRID to set
//     */
//    public void setSRID(int SRID) {
//        this.SRID = SRID;
//    }
//
//    /**
//     * @return the x
//     */
//    public double getX() {
//        return x;
//    }
//
//    /**
//     * @param x the x to set
//     */
//    public void setX(double x) {
//        this.x = x;
//    }
//
//    /**
//     * @return the y
//     */
//    public double getY() {
//        return y;
//    }
//
//    /**
//     * @param y the y to set
//     */
//    public void setY(double y) {
//        this.y = y;
//    }
//
//    /**
//     * @return the z
//     */
//    public double getZ() {
//        return z;
//    }
//
//    /**
//     * @param z the z to set
//     */
//    public void setZ(double z) {
//        this.z = z;
//    }
//
//    public com.vividsolutions.jts.geom.WkbPoint getPoint() {
//        return new GeometryFactory(
//                new PrecisionModel(
//                    de.hsmainz.gi.indoornavsrv.util.StringUtils.COORDINATE_PRECISION
//                ),
//                SRID
//            )
//            .createPoint(
//                new Coordinate(
//                    x,
//                    y,
//                    z
//                )
//            );
//    }
//
//    @Override
//    public int compareTo(Object other) {
//        int out = 42;
//        out += 17 * Integer.compare(SRID, ((WkbPoint) other).getSRID());
//        out += 17 * Double.compare(x, ((WkbPoint) other).getX());
//        out += 17 * Double.compare(y, ((WkbPoint) other).getY());
//        out += 17 * Double.compare(z, ((WkbPoint) other).getZ());
//        return out;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 17 * hash + this.SRID;
//        hash = 17 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
//        hash = 17 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
//        hash = 17 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (this == other) {
//            return true;
//        }
//        if (other == null) {
//            return false;
//        }
//        if (!(other instanceof WkbPoint)) {
//            return false;
//        }
//        WkbPoint castOther = (WkbPoint) other;
//        return  this.SRID == castOther.getSRID()
//                && x - castOther.getX() <= de.hsmainz.gi.indoornavsrv.util.StringUtils.COORDINATE_PRECISION
//                && y - castOther.getY() <= de.hsmainz.gi.indoornavsrv.util.StringUtils.COORDINATE_PRECISION
//                && z - castOther.getZ() <= de.hsmainz.gi.indoornavsrv.util.StringUtils.COORDINATE_PRECISION;
//    }
// </editor-fold>

    /**
     * @return the wkb
     */
    public String getWkb() {
        return wkb;
    }


    public Point getPoint() {
        Point point;
        try {
            point = (Point) reader.read(WKBReader.hexToBytes(getWkb()));
        } catch (ParseException ex) {
            android.util.Log.d("WkbPoint", "Couldn't parse WKB", ex);
            point = new GeometryFactory()
                .createPoint(
                        new Coordinate(
                                0, 0, 0
                        )
                );
            point.setSRID(4326);
        }
        return point;
    }

    /**
     * @param wkb the wkb to set
     */
    public void setWkb(String wkb) {
        this.wkb = wkb;
    }

    public void setWkb(com.vividsolutions.jts.geom.Point point) {
        this.wkb = WKBWriter.toHex(writer.write(point));
    }

    public String toText() {
        return getPoint().toText();
    }

    @Override
    public int compareTo(Object other) {
        return this.wkb.compareTo(((WkbPoint) other).getWkb());
    }

    @Override
    public int hashCode() {
        return 37 * Objects.hashCode(this.wkb);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof WkbPoint)) {
            return false;
        }
        WkbPoint castOther = (WkbPoint) other;
        boolean res;
        try {
            res = Objects.equals(this.wkb, castOther.getWkb());
        } catch (NullPointerException npe) {
            android.util.Log.v("WkbPoint", "NPE " + StringUtils.toString(this) + " | " + StringUtils.toString(castOther), npe);
            res = false;
        }
        return res;
    }

    public int getPropertyCount() {
        return 1;
    }

    public Object getProperty(int index) {
        switch(index)  {
            case 0: return wkb;
        }
        return null;
    }

    @Override
    public void setProperty(int index, Object obj) {
        switch(index)  {
            case 0: wkb = obj.toString(); break;//wkb = (String) obj; break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable table, PropertyInfo info) {
        switch(index)  {
            case 0:
                info.name = "wkb";
                info.type = String.class; break;
        }
    }

    /**
     * Gets the inner text of xml tags
     */
    @Override
    public String getInnerText() {
        return null;
    }

    /**
     * @param s String to be set as inner text for an outgoing soap object
     */
    @Override
    public void setInnerText(String s) {

    }
}
