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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 04.02.15.
 */
public class TinyCoordinate {
    public double x, y, z;

    public TinyCoordinate() {
    }

    public TinyCoordinate(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public TinyCoordinate(Point p) {
        Coordinate c = p.getCoordinate();
        this.x = c.x;
        this.y = c.y;
        this.z = c.z;
    }


    public Point asPoint(int SRID){
        Point output = new GeometryFactory()
            .createPoint(
                    new Coordinate(
                            this.x, this.y, this.z
                    )
            );
        output.setSRID(SRID);
        return output;
    }

    public String toString() {
        return "TinyCoordinate (" + x + " " + y + " " + z + ")";
    }

    public void round(int pow) {
        double fac = Math.pow(10, pow);
        this.x = Math.round(this.x*fac) / fac;
        this.y = Math.round(this.y*fac) / fac;
        this.z = Math.round(this.z*fac) / fac;
    }
}
