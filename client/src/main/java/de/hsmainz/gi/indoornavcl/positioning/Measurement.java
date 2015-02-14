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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Minimalistic Wrapper Class for rssi and txPower combined into a Measurement.
 *
 *
 *
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 04.02.15.
 */
public class Measurement
            implements Parcelable {
    private double rssi;
    private double txPower;

    public Measurement() {

    }

    public Measurement(double rssi, double txPower) {
        this.rssi = rssi;
        this.txPower = txPower;
    }

    public Measurement(Parcel in) {
        this.rssi = in.readDouble();
        this.txPower = in.readDouble();
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public double getTxPower() {
        return txPower;
    }

    public void setTxPower(double txPower) {
        this.txPower = txPower;
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
        dest.writeDouble(this.rssi);
        dest.writeDouble(this.txPower);
    }

    public String toString() {
        return "Measurement : " + rssi + " / " + txPower;
    }
}