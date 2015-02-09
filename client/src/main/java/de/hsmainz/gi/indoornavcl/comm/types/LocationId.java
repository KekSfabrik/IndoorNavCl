
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

import android.os.Parcel;
import android.os.Parcelable;
import de.hsmainz.gi.indoornavcl.util.Utilities;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;



/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class LocationId
        implements  KvmSerializable,
                    Serializable,
                    Comparable,
                    IndoorNavEntity,
                    Parcelable {

    protected int beaconId;
    protected int site;

    /**
     * Ruft den Wert der beaconId-Eigenschaft ab.
     * 
     */
    public int getBeaconId() {
        return beaconId;
    }

    public LocationId() {

    }

    public LocationId(Beacon beacon, Site site) {
        this.beaconId = beacon.getId();
        this.site = site.getSite();
    }

    public LocationId(int beaconId, int site) {
        this.beaconId = beaconId;
        this.site = site;
    }

    public LocationId(Parcel in) {
        this.beaconId = in.readInt();
        this.site = in.readInt();
    }

    /**
     * Legt den Wert der beaconId-Eigenschaft fest.
     * 
     */
    public void setBeaconId(int value) {
        this.beaconId = value;
    }

    /**
     * Ruft den Wert der site-Eigenschaft ab.
     * 
     */
    public int getSite() {
        return site;
    }

    /**
     * Legt den Wert der site-Eigenschaft fest.
     * 
     */
    public void setSite(int value) {
        this.site = value;
    }

    /**
     * Get the property at the given index
     *
     * @param index
     */
    @Override
    public Object getProperty(int index) {
        switch(index) {
            case 0: return beaconId;
            case 1: return site;
            default: return null;
        }
    }

    /**
     * @return the number of serializable properties
     */
    @Override
    public int getPropertyCount() {
        return 2;
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
                this.beaconId = Utilities.tryParseInt(value).intValue();
                break;
            case 1:
                this.site = Utilities.tryParseInt(value).intValue();
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
                info.name = "beaconId";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "site";
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof LocationId)) {
            return false;
        }
        LocationId castOther = (LocationId) other;
        return (this.getSite() == castOther.getSite())
                && (this.getBeaconId() == castOther.getBeaconId());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.site;
        result = 37 * result + this.beaconId;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        int out = 0;
        out += 37 * (this.beaconId - ((LocationId) o).getBeaconId());
        out += 42 * (this.site - ((LocationId) o).getSite());
        return out;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.beaconId);
        dest.writeInt(this.site);
    }


    public static final Parcelable.Creator<LocationId> CREATOR = new Parcelable.Creator<LocationId>() {

        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link android.os.Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public LocationId createFromParcel(Parcel source) {
            return new LocationId(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public LocationId[] newArray(int size) {
            return new LocationId[size];
        }
    };

    public boolean isVerified() {
        return  this.beaconId != 0
                && this.site != 0;
    }
}
