
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

import de.hsmainz.gi.indoornavcl.util.StringUtils;
import de.hsmainz.gi.indoornavcl.util.Utilities;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Objects;



/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class Beacon
        implements  KvmSerializable,
                    Serializable,
                    Comparable,
                    IndoorNavEntity {

    protected int       id;
    protected int       major;
    protected int       minor;
    protected String    uuid;

    /**
     * Empty Constructor - used by kSoap2
     */
    public Beacon() {
    }

    /**
     * Construct a {@link Beacon} from it\'s Fields.
     * @param   id      the Database ID
     * @param   major   the Major Version (~site)
     * @param   minor   the Minor Version (individual id)
     * @param   uuid    the Manufacturer UUID
     */
    public Beacon(int id, int major, int minor, String uuid) {
        this.id = id;
        this.major = major;
        this.minor = minor;
        this.uuid = uuid.replace("-", "");
    }

    /**
     * Construct a {@link Beacon} from a
     * {@link org.altbeacon.beacon.Beacon}.
     * @param   beacon  the altbeacon.Beacon
     */
    public Beacon(org.altbeacon.beacon.Beacon beacon) {
        this.major = Integer.parseInt(beacon.getId2().toString());
        this.minor = Integer.parseInt(beacon.getId3().toString());
        this.uuid = beacon.getId1().toString().replace("-", "");
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }


    /**
     * Ruft den Wert der major-Eigenschaft ab.
     * 
     */
    public int getMajor() {
        return major;
    }

    /**
     * Legt den Wert der major-Eigenschaft fest.
     * 
     */
    public void setMajor(int value) {
        this.major = value;
    }

    /**
     * Ruft den Wert der minor-Eigenschaft ab.
     * 
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Legt den Wert der minor-Eigenschaft fest.
     * 
     */
    public void setMinor(int value) {
        this.minor = value;
    }

    /**
     * Ruft den Wert der uuid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Legt den Wert der uuid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value.replace("-","");
    }

    /**
     * Get the property at the given index
     *
     * @param index
     */
    @Override
    public Object getProperty(int index) {
        switch(index) {
            case 0: return id;
            case 1: return major;
            case 2: return minor;
            case 3: return uuid;
            default: return null;
        }
    }

    /**
     * @return the number of serializable properties
     */
    @Override
    public int getPropertyCount() {
        return 4;
    }

    /**
     * Sets the property with the given index to the given value.
     *
     * @param index the index to be set
     * @param value the value of the property
     */
    @Override
    public void setProperty(int index, Object value) {
        switch(index) {
            case 0:
                this.id = Utilities.tryParseInt(value).intValue();
                break;
            case 1:
                this.major = Utilities.tryParseInt(value).intValue();
                break;
            case 2:
                this.minor = Utilities.tryParseInt(value).intValue();
                break;
            case 3:
                this.uuid = value.toString().replace("-","");
                break;
        }
    }

    /**
     * Fills the given property info record.
     *
     * @param index      the index to be queried
     * @param properties information about the (de)serializer.  Not frequently used.
     * @param info       The return parameter, to be filled with information about the
     */
    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch(index) {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "id";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "major";
                break;
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "minor";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "uuid";
                break;
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
    @Override
    public int compareTo(Object o) {
        int out = 0;
        out += this.uuid.compareTo(((Beacon) o).getUuid());
        out += 42 * this.major - ((Beacon) o).getMajor();
        out += 79 * this.major - ((Beacon) o).getMajor();
        return out;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Beacon)) {
            return false;
        }
        Beacon castOther = (Beacon) other;
        boolean res = false;
        try {
             res = this.uuid.equals(castOther.getUuid())
                    && this.major == castOther.getMajor()
                    && this.major == castOther.getMajor();
        } catch (NullPointerException npe) {
            android.util.Log.v("Beacon", "NPE " + StringUtils.toString(this) + " | " + StringUtils.toString(castOther), npe);
            res = false;
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.uuid);
        hash = 37 * hash + this.major;
        hash = 37 * hash + this.minor;
        return hash;
    }
}
