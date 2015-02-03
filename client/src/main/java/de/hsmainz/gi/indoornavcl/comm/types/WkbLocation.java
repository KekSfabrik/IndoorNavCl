
package de.hsmainz.gi.indoornavcl.comm.types;

import de.hsmainz.gi.indoornavcl.util.StringUtils;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Objects;



/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class WkbLocation
        implements  KvmSerializable,
                    Serializable,
                    Comparable,
                    IndoorNavEntity {

    protected Beacon beacon;
    protected WkbPoint coord;
    protected LocationId id;
    protected Site site;

    /**
     * Ruft den Wert der beacon-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Beacon }
     *     
     */
    public Beacon getBeacon() {
        return beacon;
    }

    /**
     * Legt den Wert der beacon-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Beacon }
     *     
     */
    public void setBeacon(Beacon value) {
        this.beacon = value;
    }

    /**
     * Ruft den Wert der coord-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link WkbPoint }
     *     
     */
    public WkbPoint getCoord() {
        return coord;
    }

    /**
     * Legt den Wert der coord-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link WkbPoint }
     *     
     */
    public void setCoord(WkbPoint value) {
        this.coord = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LocationId }
     *     
     */
    public LocationId getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationId }
     *     
     */
    public void setId(LocationId value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der site-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Site }
     *     
     */
    public Site getSite() {
        return site;
    }

    /**
     * Legt den Wert der site-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Site }
     *     
     */
    public void setSite(Site value) {
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
            case 0: return beacon;
            case 1: return coord;
            case 2: return id;
            case 3: return site;
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
        SoapObject so = (SoapObject) value;
        android.util.Log.d("WkbLocation", ".setProperty(" + index + ", " + value.toString() + ")");
        switch(index) {
            case 0: {
                    Beacon ret = new Beacon();
                    for (int i = 0, len = so.getPropertyCount(); i < len; i++) {
                        ret.setProperty(i, so.getProperty(i));
                    }
                    this.beacon = ret;
                    android.util.Log.d("WkbLocation", StringUtils.toString(ret));
                }
                break;
            case 1: {
                    WkbPoint ret = new WkbPoint();
                    for (int i = 0, len = so.getPropertyCount(); i < len; i++) {
                        ret.setProperty(i, so.getProperty(i));
                    }
                    this.coord = ret;
                    android.util.Log.d("WkbLocation", StringUtils.toString(ret));
                }
                break;
            case 2: {
                    LocationId ret = new LocationId();
                    for (int i = 0, len = so.getPropertyCount(); i < len; i++) {
                        ret.setProperty(i, so.getProperty(i));
                    }
                    this.id = ret;
                    android.util.Log.d("WkbLocation", StringUtils.toString(ret));
                }
                break;
            case 3: {
                    Site ret = new Site();
                    for (int i = 0, len = so.getPropertyCount(); i < len; i++) {
                        ret.setProperty(i, so.getProperty(i));
                    }
                    this.site = ret;
                    android.util.Log.d("WkbLocation", StringUtils.toString(ret));
                }
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
                info.type = Beacon.class;
                info.name = "beacon";
                break;
            case 1:
                info.type = WkbPoint.class;
                info.name = "coord";
                break;
            case 2:
                info.type = LocationId.class;
                info.name = "id";
                break;
            case 3:
                info.type = Site.class;
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
    public int compareTo(Object o) {
        int out = 0;
        out += this.id.compareTo(((WkbLocation) o).getId());
        out += this.beacon.compareTo(((WkbLocation) o).getBeacon());
        out += this.site.compareTo(((WkbLocation) o).getSite());
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
        if (!(other instanceof WkbLocation)) {
            return false;
        }
        WkbLocation castOther = (WkbLocation) other;
        boolean res;
        try {
            res = (this.site.equals(castOther.getSite()))
                    && (this.beacon.equals(castOther.getBeacon()));
        } catch (NullPointerException npe) {
            android.util.Log.v("WkbLocation", "NPE " + StringUtils.toString(this) + " | " + StringUtils.toString(castOther), npe);
            res = false;
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.beacon);
        hash = 79 * hash + Objects.hashCode(this.site);
        hash = 79 * hash + Objects.hashCode(this.coord);
        return hash;
    }
}
