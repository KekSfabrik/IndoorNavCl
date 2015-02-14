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

package de.hsmainz.gi.indoornavcl.comm;

import android.util.Log;
import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;

/**
 * Class to rewrap in and outputs to calls to the
 * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}.
 *
 *
 *
 * @author  KekS (mailto:keks@keksfabrik.eu),  27.01.15.
 */
public class SoapPositionerRequests {

    private static final String     TAG     = SoapPositionerRequests.class.getSimpleName();

    /**
     * Wrapper to put a {@link de.hsmainz.gi.indoornavcl.comm.types.Site} into the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}.
     * @param   site    the Site to put into the System
     * @return  whether or not it was successful
     */
    public static boolean addSite(Site site) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().addSite(site);
        } catch (Exception ex) {
            Log.w(TAG, "Could not addSite("+site+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to put a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} into the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}.
     * @param   beacon    the Beacon to put into the System
     * @return  whether or not it was successful
     */
    public static boolean addBeacon(Beacon beacon) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().addBeacon(beacon);
        } catch (Exception ex) {
            Log.w(TAG, "Could not addBeacon("+beacon+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to put a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} into the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService} based on the beacons uuid, major & minor.
     * @param   uuid    the Beacons manufacturer UUID
     * @param   major   the Beacons MAJOR field
     * @param   minor   the Beacons MINOR field
     * @return  whether or not it was successful
     */
    public static boolean addBeaconFromUuidMajorMinor(String uuid, int major, int minor) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().addBeaconFromUuidMajorMinor(uuid, major, minor);
        } catch (Exception ex) {
            Log.w(TAG, "Could not addBeaconFromUuidMajorMinor("+ uuid +", "+ major +", "+ minor +")", ex);
        }
        return res;
    }

    /**
     * Wrapper to put a {@link de.hsmainz.gi.indoornavcl.comm.types.Site} into the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService} based on its name.
     * @param   name    the name of the Site to put into the System
     * @return  whether or not it was successful
     */
    public static boolean addSiteFromName(String name) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().addSiteFromName(name);
        } catch (Exception ex) {
            Log.w(TAG, "Could not addSiteFromName("+name+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to delete a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Beacon has to have its
     * ID field set and therefor {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon#isVerified} has to be
     * {@link java.lang.Boolean#TRUE}.
     * @param   beacon    the Beacon to delete from the System
     * @return  whether or not it was successful
     */
    public static boolean deleteBeacon(Beacon beacon) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().deleteBeacon(beacon);
        } catch (Exception ex) {
            Log.w(TAG, "Could not deleteBeacon("+beacon+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to remove a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from a
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The inputs have to have their
     * ID fields set and therefor {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be
     * {@link java.lang.Boolean#TRUE}.
     * @param   beacon  the Beacon to remove from the Site
     * @param   site    the Site to remove the Beacon from
     * @return  whether or not it was successful
     */
    public static boolean removeBeaconFromSite(Beacon beacon, Site site) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().removeBeaconFromSite(beacon, site);
        } catch (Exception ex) {
            Log.w(TAG, "Could not removeBeaconFromSite("+beacon+", "+site+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to place a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} at a Coordinate indicated by the given
     * {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation} managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Beacon and Sites wrapped in
     * the WkbLocation have to have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * @param   wkbLocation     the location that should be added to the system
     * @return  whether or not it was successful
     */
    public static boolean placeBeaconAtLocation(WkbLocation wkbLocation) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().placeBeaconAtLocation(wkbLocation);
        } catch (Exception ex) {
            Log.w(TAG, "Could not placeBeaconAtLocation("+ wkbLocation +")", ex);
        }
        return res;
    }

    /**
     * Wrapper to delete a {@link de.hsmainz.gi.indoornavcl.comm.types.Site} from the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Site has to have its
     * ID field set and therefor {@link de.hsmainz.gi.indoornavcl.comm.types.Site#isVerified} has to be
     * {@link java.lang.Boolean#TRUE}.
     * @param   site    the Site to delete from the System
     * @return  whether or not it was successful
     */
    public static boolean deleteSite(Site site) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().deleteSite(site);
        } catch (Exception ex) {
            Log.w(TAG, "Could not deleteSite("+site+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to remove a {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation} from the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Beacon and Sites wrapped in
     * the WkbLocation have to have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   wkbLocation    the location to remove from the System
     * @return  whether or not it was successful
     */
    public static boolean removeLocation(WkbLocation wkbLocation) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().removeLocation(wkbLocation);
        } catch (Exception ex) {
            Log.w(TAG, "Could not removeLocation("+ wkbLocation +")", ex);
        }
        return res;
    }

    /**
     * Wrapper to place a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} on a specific
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} and at a given {@link com.vividsolutions.jts.geom.Point}
     * Coordinate in the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Beacon and Site have to
     * have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   site        the Site the Beacon should be placed on
     * @param   beacon      the Beacon to place on that Site
     * @param   coordinate  the Coordinate of the Beacon on the Site
     * @return  whether or not it was successful
     */
    public static boolean placeBeacon(Site site, Beacon beacon, Point coordinate) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().placeBeacon(site, beacon, new WkbPoint(coordinate));
        } catch (Exception ex) {
            Log.w(TAG, "Could not placeBeacon("+site+", "+beacon+", "+coordinate+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to replace a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} on a specific
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} in the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Beacons and Site have to
     * have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   site        the Site the Beacon should be replaced at
     * @param   oldBeacon   the Beacon to be replaced
     * @param   newBeacon   the Beacon to place at the Coordinate of the old Beacon
     * @return  whether or not it was successful
     */
    public static boolean replaceBeacon(Site site, Beacon oldBeacon, Beacon newBeacon) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().replaceBeacon(site, oldBeacon, newBeacon);
        } catch (Exception ex) {
            Log.w(TAG, "Could not replaceBeacon("+site+", "+oldBeacon+", "+newBeacon+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to relocate a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from one
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} to another in the System managed through the
     * {@link de.hsmainz.gi.indoornavcl.comm.positioner.IBeaconPositionerService}. The input Beacon and Sites have to
     * have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   fromSite        the Site the Beacon was located at
     * @param   toSite          the Site the Beacon is moved to
     * @param   beacon          the Beacon that is relocated
     * @param   toCoordinate    the Coordinate of the Beacon on the new Site
     * @return  whether or not it was successful
     */
    public static boolean relocateBeacon(Site fromSite, Site toSite, Beacon beacon, Point toCoordinate) {
        boolean res = false;
        try {
            res = new IBeaconPositionerService().relocateBeacon(fromSite, toSite, beacon, new WkbPoint(toCoordinate));
        } catch (Exception ex) {
            Log.w(TAG, "Could not relocateBeacon("+fromSite+", "+toSite+", "+beacon+", "+toCoordinate+")", ex);
        }
        return res;
    }
//    private static final String     TAG             = SoapPositionerRequests.class.getSimpleName();
//
//    private static final boolean    DEBUG           = true;
//    private static final String     REQUEST_URL     = "http://143.93.114.129:8080/service/BeaconPositionerService?wsdl";
//    private static final String     NAMESPACE       = "http://services.indoornavcl.comm.gi.hsmainz.de/";
//    private static final String     SOAP_ACTION     = "http://143.93.114.129:8080/service/BeaconPositionerService";
//    private static String           SESSION_ID;
//
//    private static final String     USERNAME        = "indoornav-admin";
//    private static final String     PASSWORD        = "73s962yV97C2Ei10zP9m4w13D9gh169S";
//
//
//
//    /**
//     * Add a new {@link de.hsmainz.gi.indoornavcl.comm.types.Site} to the System.
//     * @param   site    the Site to add
//     * @return  whether site was successfully added
//     */
//    public boolean addSite(Site site) {
//        String methodName = "addSite";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "addSiteResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Wrapped call for {@link #addSite(de.hsmainz.gi.indoornavcl.comm.types.Site) } with
//     * the implication of a new {@link de.hsmainz.gi.indoornavcl.comm.types.Site}
//     * being constructed before adding it to the System.
//     * @param   name    the new Site's name
//     * @return  whether the site was successfully added
//     */
//    public boolean addSite(String name) {
//        String methodName = "addSiteFromName";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.name = "name";
//        properties.type = String.class;
//        properties.setValue(name);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "addSiteFromNameResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//
//    }
//
//
//    /**
//     * Unregister a {@link #addSite(de.hsmainz.gi.indoornavcl.comm.types.Site) } from the
//     * System and delete it from the Database.
//     * @param   site    the Site to remove
//     * @return  whether the site was successfully deleted from the database
//     */
//    public boolean deleteSite(Site site) {
//        String methodName = "deleteSite";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "deleteSiteResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Register a new {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} to the System.
//     * @param   beacon  the Beacon to register to the System
//     * @return  whether the Beacon was successully registered
//     */
//    public boolean addBeacon(Beacon beacon) {
//        String methodName = "addBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "addBeaconResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//
//    }
//
//    /**
//     * wrapped call to {@link #addBeacon(de.hsmainz.gi.indoornavcl.comm.types.Beacon) }
//     * with a new new {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} being constructed
//     * before registering with the System.
//     * @param   uuid    the Beacons manufacturer uuid
//     * @param   major   the Beacons major version
//     * @param   minor   the Beacons minor version
//     * @return  whether the Beacon was successully registered
//     */
//    public boolean addBeacon(String uuid, int major, int minor) {
//        String methodName = "addBeaconFromUuidMajorMinor";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "uuid";
//        properties.type = String.class;
//        properties.setValue(uuid);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "major";
//        properties.type = Integer.class;
//        properties.setValue(major);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "minor";
//        properties.type = Integer.class;
//        properties.setValue(minor);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "addBeaconFromUuidMajorMinorResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Unregister a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from a specific
//     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site}
//     * @param   beacon  the Beacon to unregister from the Site
//     * @param   site    the Site to remove the Beacon from
//     * @return  whether successful
//     */
//    public boolean removeBeaconFromSite(Beacon beacon, Site site) {
//        String methodName = "removeBeaconFromSite";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "removeBeaconFromSiteResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Unregister a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from a specific
//     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} by its
//     * {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation}.
//     * @param   location    the location to remove
//     * @return  whether successful
//     */
//    public boolean removeBeaconFromSite(WkbLocation location) {
//        String methodName = "removeLocation";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "location";
//        properties.type = WkbLocation.class;
//        properties.setValue(location);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "removeLocationResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Unregister a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from the System
//     * and remove it from the Database.
//     * @param   beacon  the Beacon to unregister from the System
//     * @return  whether successful
//     */
//    public boolean deleteBeacon(Beacon beacon) {
//        String methodName = "deleteBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "deleteBeaconResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Place a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} on a
//     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} with a
//     * {@link com.vividsolutions.jts.geom.Point}
//     * @param   site        the Site to place the Beacon on
//     * @param   beacon      the Beacon that is being placed
//     * @param   coordinate  the Beacons Coordinate on the given Site
//     * @return  whether successful
//     */
//    public boolean placeBeacon(Site site, Beacon beacon, Point coordinate) {
//        String methodName = "placeBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "coordinate";
//        properties.type = Point.class;
//        properties.setValue(coordinate);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "placeBeaconResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//
//    /**
//     * Place a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} on a
//     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} with a
//     * {@link com.vividsolutions.jts.geom.Point}
//     * @param   location    the WkbLocation (Site, Beacon, Point)
//     * @return  whether successful
//     */
//    public boolean placeBeacon(WkbLocation location) {
//        String methodName = "placeBeaconAtLocation";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "location";
//        properties.type = WkbLocation.class;
//        properties.setValue(location);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "placeBeaconAtLocationResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Replace an existing {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} with a
//     * new one at the same {@link com.vividsolutions.jts.geom.Point}.
//     * @param   site        the Site on which to replace the Beacon
//     * @param   oldBeacon   the old Beacon
//     * @param   newBeacon   the new Beacon
//     * @return  whether or not the oldBeacon could be replaced by newBeacon
//     */
//    public boolean replaceBeacon(Site site, Beacon oldBeacon, Beacon newBeacon) {
//        String methodName = "replaceBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "oldBeacon";
//        properties.type = Beacon.class;
//        properties.setValue(oldBeacon);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "newBeacon";
//        properties.type = Beacon.class;
//        properties.setValue(newBeacon);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "replaceBeaconResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//    }
//
//    /**
//     * Relocate an existing {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} to a
//     * different {@link com.vividsolutions.jts.geom.Point} on another
//     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site}.
//     * @param   fromSite        from which Site the Beacon is moved
//     * @param   toSite          to which Site the Beacon is moved to
//     * @param   beacon          the Beacon that is being moved
//     * @param   toCoordinate    the Beacons Coordinate on the new Site
//     * @return  whether successful
//     */
//    public boolean relocateBeacon(Site fromSite, Site toSite, Beacon beacon, Point toCoordinate) {
//        String methodName = "relocateBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "fromSite";
//        properties.type = Site.class;
//        properties.setValue(fromSite);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "toSite";
//        properties.type = Site.class;
//        properties.setValue(toSite);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.namespace = NAMESPACE;
//        properties.name = "toCoordinate";
//        properties.type = Point.class;
//        properties.setValue(toCoordinate);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "relocateBeaconResponse", Boolean.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (Boolean) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return false;
//
//    }
//
//
//
//
//    // ----------- private helpers -----------
//
//    private static final String getSessionId(HttpTransportSE ht) throws IOException {
//        List<HeaderProperty> COOKIE_HEADER = (List<HeaderProperty>) ht.getServiceConnection().getResponseProperties();
//        for (int i = 0; i < COOKIE_HEADER.size(); i++) {
//            String key = COOKIE_HEADER.get(i).getKey();
//            String value = COOKIE_HEADER.get(i).getValue();
//
//            if (key != null && key.equalsIgnoreCase("set-cookie")) {
//                SESSION_ID = value.trim();
//                Log.v(TAG, "Cookie: " + SESSION_ID);
//                break;
//            }
//        }
//        return SESSION_ID;
//    }
//
//    private static final void testResponse(HttpTransportSE ht) {
//        ht.debug = DEBUG;
//        if (DEBUG) {
//            Log.v(TAG, "Request XML:\n" + ht.requestDump);
//            Log.v(TAG, "\n\n\nResponse XML:\n" + ht.responseDump);
//        }
//    }
//
//    private static final SoapSerializationEnvelope getEnvelope(SoapObject request) {
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//
//        Element[] header = new Element[1];
//        header[0] = new Element().createElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security");
//        header[0].setAttribute(null, "mustUnderstand","1");
//
//        Element usernameTokenElement = new Element().createElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken");
//        usernameTokenElement.setAttribute(null, "Id", "UsernameToken-");
//        header[0].addChild(Node.ELEMENT, usernameTokenElement);
//
//        Element userElem = new Element().createElement(null, "n0:Username");
//        userElem.addChild(Node.IGNORABLE_WHITESPACE, USERNAME);
//        usernameTokenElement.addChild(Node.ELEMENT, userElem);
//
//        Element passElem = new Element().createElement(null,"n0:Password");
//        passElem.setAttribute(null, "Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
//        passElem.addChild(Node.TEXT, PASSWORD);
//
//        usernameTokenElement.addChild(Node.ELEMENT, passElem);
//
//
//        // add header to envelope
//        envelope.headerOut = header;
//
//
//        Log.i(TAG, "Header: " + envelope.headerOut.toString());
//
//
//        envelope.dotNet = false;
//        envelope.bodyOut = request;
//        envelope.setOutputSoapObject(request);
//        Log.i(TAG, "Body: " + envelope.bodyOut.toString());
//        //--------------------
//        envelope.dotNet = false;
//        envelope.implicitTypes = true;
//        envelope.setAddAdornments(false);
//        envelope.setOutputSoapObject(request);
//        return envelope;
//    }
//
//    private static final HttpTransportSE getTransport() {
//        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, REQUEST_URL, 10000);
//        ht.debug = true;
//        ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
//        return ht;
//    }
//
//    private static final List<HeaderProperty> getHeader() {
//        List<HeaderProperty> header = new ArrayList<>();
//        HeaderProperty headerPropertyObj = new HeaderProperty("cookie", SoapPositionerRequests.SESSION_ID);
//        header.add(headerPropertyObj);
//        return header;
//    }
}

