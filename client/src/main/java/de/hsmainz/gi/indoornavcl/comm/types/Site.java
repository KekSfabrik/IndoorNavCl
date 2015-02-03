
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
public class Site
        implements  KvmSerializable,
                    Serializable,
                    Comparable,
                    IndoorNavEntity {

    protected String name;
    protected int site;

    public Site() { }

    public Site(String name) {
        this(0, name);
    }

    public Site(int id, String name) {
        this.site = id;
        this.name = name;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
            case 0: return name;
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
                this.name = value.toString();
                break;
            case 1:
                this.site = Utilities.tryParseInt(value);
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
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "name";
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
    public int compareTo(Object o) {
        int out = 0;
        out += this.name.compareTo(((Site) o).getName());
        out += 42 * this.site - ((Site) o).getSite();
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
        if (!(other instanceof Site)) {
            return false;
        }
        Site castOther = (Site) other;
        boolean res;
        try {
            res = this.name.equals(castOther.getName());
        } catch (NullPointerException npe) {
            android.util.Log.v("Site", "NPE " + StringUtils.toString(this) + " | " + StringUtils.toString(castOther), npe);
            res = false;
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.site;
        hash = 11 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
