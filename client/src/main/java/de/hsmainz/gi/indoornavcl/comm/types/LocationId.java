
package de.hsmainz.gi.indoornavcl.comm.types;

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
                    IndoorNavEntity {

    protected int beaconId;
    protected int site;

    /**
     * Ruft den Wert der beaconId-Eigenschaft ab.
     * 
     */
    public int getBeaconId() {
        return beaconId;
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
        result = 37 * result + this.getSite();
        result = 37 * result + this.getBeaconId();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        int out = 0;
        out += 42 * this.site - ((LocationId) o).getSite();
        out += 79 * this.beaconId - ((LocationId) o).getBeaconId();
        return out;
    }
}
