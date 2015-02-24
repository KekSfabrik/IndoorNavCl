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
import de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService;
import de.hsmainz.gi.types.Beacon;
import de.hsmainz.gi.types.Site;
import de.hsmainz.gi.types.WkbLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to rewrap in and outputs to calls to the
 * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService}.
 *
 *
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class SoapLocatorRequests {

    private static final String TAG = SoapLocatorRequests.class.getSimpleName();

    /**
     * Wrapper to get a {@link de.hsmainz.gi.types.Site} object from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * its exact name.
     * @param   name    the name of the Site
     * @return  the Site object from the LocatorService
     */
    public static Site getSite(String name) {
        Site res = new Site();
        try {
            res = new IBeaconLocatorService().getSite(name);
        } catch (Exception ex) {
            Log.w(TAG, "Could not getSite("+name+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get the {@link de.hsmainz.gi.types.Beacon} object from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * its uuid, major and minor fields.
     * @param   uuid    the Beacons UUID field
     * @param   major   the Beacons MAJOR field
     * @param   minor   the Beacons MINOR field
     * @return  the Beacon matching the description
     */
    public static Beacon getBeaconFromUuidMajorMinor(String uuid, int major, int minor) {
        Beacon res = new Beacon();
        try {
            res = new IBeaconLocatorService().getBeaconFromUuidMajorMinor(uuid, major, minor);
        } catch (Exception ex) {
            Log.w(TAG, "Could not getBeaconFromUuidMajorMinor("+uuid+", "+major+", "+minor+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a Set of {@link de.hsmainz.gi.types.Site} objects from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * the approximate name of the Site.
     * @param   name    the approximate name of the Site
     * @return  a Set of Sites with names matching the name
     */
    public static Set<Site> getSiteByApproximateName(String name) {
        Set<Site> res = new HashSet<>();
        try {
            res = new IBeaconLocatorService().getSiteByApproximateName(name);
        } catch (Exception ex) {
            Log.w(TAG, "Could not getSiteByApproximateName("+name+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a {@link com.vividsolutions.jts.geom.Point} object from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * the {@link de.hsmainz.gi.types.Beacon} and the {@link de.hsmainz.gi.types.Site}.
     * The resulting Point is the Coordinate of the Beacon on that Site.
     * @param   site    the Site the beacon is located at
     * @param   beacon  the Beacon for which the coordinate should be found
     * @return  the Coordinate of the Beacon on that Site
     */
    public static Point getCoordinate(Site site, Beacon beacon) {
        Point res = null;
        try {
            res = new IBeaconLocatorService().getCoordinate(site, beacon).getPoint();
        } catch (Exception ex) {
            Log.w(TAG, "Could not getCoordinate("+site+", "+beacon+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a Set of {@link de.hsmainz.gi.types.Site} objects from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * a Set of {@link de.hsmainz.gi.types.Beacon}s.
     * @param   beacons the Beacons for which the Sites are wanted
     * @return  a Set of all Sites the given Beacons are located at
     */
    public static Set<Site> getSitesFromBeaconList(Set<Beacon> beacons){
        Set<Site> res = new HashSet<>();
        try {
            if (beacons != null) {
                res = new IBeaconLocatorService().getSitesFromBeaconList(beacons.toArray(new Beacon[0]));
            }
        } catch (Exception ex) {
            Log.w(TAG, "Could not getSitesFromBeaconList("+(beacons != null ? Arrays.toString(beacons.toArray()) : beacons)+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a Set of {@link de.hsmainz.gi.types.WkbLocation} objects from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * the {@link de.hsmainz.gi.types.Site} they are located at.
     * @param   site    the Site at which all Beaconpositions should be found
     * @return  the Locations of all Beacons on the given Site
     */
    public static Set<WkbLocation> getBeaconLocationsFromSite(Site site) {
        Set<WkbLocation> res = new HashSet<>();
        try {
            res = new IBeaconLocatorService().getBeaconLocationsFromSite(site);
        } catch (Exception ex) {
            Log.w(TAG, "Could not getBeaconLocationsFromSite("+site+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a {@link de.hsmainz.gi.types.Beacon} object with its appropriate ID from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * a Beacon without ID.
     * @param   beacon  the Beacon which should get its Field ID set
     * @return  the beacon - if it is part of the system - with its ID field
     */
    public static Beacon getBeacon(Beacon beacon) {
        Beacon res = new Beacon();
        try {
            res = new IBeaconLocatorService().getBeacon(beacon);
        } catch (Exception ex) {
            Log.w(TAG, "Could not getBeacon("+beacon+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a Set of {@link de.hsmainz.gi.types.Site} objects from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * a {@link de.hsmainz.gi.types.Beacon}.
     * @param   beacon  the Beacon for which its Sites should be found
     * @return  the Set of Sites the given Beacon can be located at
     */
    public static Set<Site> getSitesFromBeacon(Beacon beacon) {
        Set<Site> res = new HashSet<>();
        try {
            res = new IBeaconLocatorService().getSitesFromBeacon(beacon);
        } catch (Exception ex) {
            Log.w(TAG, "Could not getSitesFromBeacon("+beacon+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a Set of {@link de.hsmainz.gi.types.WkbLocation} objects from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * a Set of {@link de.hsmainz.gi.types.Beacon}s.
     * @param   beacons the Set of Beacons for which Locations (Site & Position) should be found
     * @return  all Locations of the given beacons
     */
    public static Set<WkbLocation> getBeaconLocationsFromBeaconList(Set<Beacon> beacons) {
        Set<WkbLocation> res = new HashSet<>();
        try {
            if (beacons != null) {
                res = new IBeaconLocatorService().getBeaconLocationsFromBeaconList(beacons.toArray(new Beacon[0]));
            }
        } catch (Exception ex) {
            Log.w(TAG, "Could not getBeaconLocationsFromBeaconList("+(beacons != null ? Arrays.toString(beacons.toArray()) : beacons)+")", ex);
        }
        return res;
    }

    /**
     * Wrapper to get a Set of {@link de.hsmainz.gi.types.Beacon} objects with ID fields from the
     * {@link de.hsmainz.gi.indoornavcl.comm.locator.IBeaconLocatorService} based on
     * a Set of Beacons without IDs.
     * @param   beacons the Set of Beacons for which the IDs should be set
     * @return  the given Beacons - if they are part of the system - with their ID fields filled
     */
    public static Set<Beacon> getBeacons(Set<Beacon> beacons) {
        Set<Beacon> res = new HashSet<>();
        try {
            if (beacons != null) {
                res = new IBeaconLocatorService().getBeacons(beacons.toArray(new Beacon[0]));
            }
        } catch (Exception ex) {
            Log.w(TAG, "Could not getBeacons("+(beacons != null ? Arrays.toString(beacons.toArray()) : beacons)+")", ex);
        }
        return res;
    }
//    private static final String     TAG             = SoapLocatorRequests.class.getSimpleName();
//
//    private static final boolean    DEBUG           = true;
//    private static final String     REQUEST_URL     = "http://143.93.114.129:8080/service/BeaconLocatorService?wsdl";
//    private static final String     NAMESPACE       = "http://services.indoornavsrv.gi.hsmainz.de/";
//    private static final String     SOAP_ACTION     = "";//"http://143.93.114.129:8080/service/BeaconLocatorService";
//    private static String           SESSION_ID;
//
//
//    private SoapLocatorRequests() { }
//
//    /**
//     * Get a the {@link de.hsmainz.gi.types.Site}s by it\'s name.
//     * @param   name  the name of the Site to search for
//     * @return  a Site Object
//     */
//    public static Site getSite(String name) {
//        String methodName = "getSite";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.namespace = NAMESPACE;
//        properties.name = "name";
//        properties.type = String.class;
//        properties.setValue(name);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getSiteResponse", Site.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (Site) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Get a List of {@link de.hsmainz.gi.types.Site}s by by a wildcarded
//     * name (SQL LIKE "%name%").
//     * @param   name  the approximate name of the Site to search for
//     * @return  a List of Site Object
//     */
//    public static List<Site> getSites(String name) {
//        String methodName = "getSiteByApproximateName";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.name = "name";
//        properties.type = String.class;
//        properties.setValue(name);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getSiteByApproximateNameResponse", KvmArrayList.class);
//        envelope.addMapping(NAMESPACE, "Site", Site.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (List<Site>) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Get a List of {@link de.hsmainz.gi.types.Site}s where the provided
//     * {@link de.hsmainz.gi.types.Beacon} is registered to.
//     * @param   beacon  the Beacon to search for
//     * @return  a List of Sites where this Beacon is registered
//     */
//    public static List<Site> getSites(Beacon beacon) {
//        String methodName = "getSitesFromBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        PropertyInfo properties = new PropertyInfo();
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getSiteFromBeaconResponse", KvmArrayList.class);
//        envelope.addMapping(NAMESPACE, "Site", Site.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (List<Site>) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Get a List of {@link de.hsmainz.gi.types.Site}s where the provided
//     * {@link de.hsmainz.gi.types.Beacon}s are registered to.
//     * @param   beacons   a list of Beacons to search for
//     * @return  a List of Sites where all provided Beacons are registered
//     */
//    public static List<Site> getSites(List<Beacon> beacons) {
//        String methodName = "getSitesFromBeaconList";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.name = "beacons";
//        properties.type = KvmArrayList.class;
//        properties.setValue(beacons);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getSiteFromBeaconListResponse", KvmArrayList.class);
//        envelope.addMapping(NAMESPACE, "Site", Site.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (List<Site>) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Get the {@link de.hsmainz.gi.types.Beacon} Object by uuid, major
//     * and minor.
//     * @param   uuid    the beacons manufacturer uuid
//     * @param   major   the beacons major id
//     * @param   minor   the beacons minor id
//     * @return  the Beacon object if it exists
//     */
//    public static Beacon getBeacon(String uuid, int major, int minor) {
//        String methodName = "getBeaconFromUuidMajorMinor";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.name = "uuid";
//        properties.type = String.class;
//        properties.setValue(uuid);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.name = "major";
//        properties.type = Integer.class;
//        properties.setValue(major);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.name = "minor";
//        properties.type = Integer.class;
//        properties.setValue(minor);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getBeaconFromUuidMajorMinorResponse", Beacon.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (Beacon) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Get the ID of a {@link de.hsmainz.gi.types.Beacon}.
//     * @param   beacon     a List of Beacons to get IDs for
//     * @return  a List of Beacons that exist
//     */
//    public static Beacon getBeacon(Beacon beacon) {
//        String methodName = "getBeacon";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getBeaconResponse", getBeaconResponse.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            SoapObject so = (SoapObject) envelope.getResponse();
//            Log.v(TAG, "so: "+so);
//            Log.v(TAG, "so.name: " + so.getName());
//            Log.v(TAG, "so.ns: " + so.getNamespace());
//            Log.v(TAG, "so.atts (" + so.getAttributeCount() + "): ");
//            for (int i = 0; i < so.getAttributeCount(); i++) {
//                Log.v(TAG, "\t" + so.getAttribute(i));
//            }
//            return new Beacon(0, 100, 12, "00000000000000000000000000000000");//(Beacon) so;
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "Caught other exception:", e);
//        }
//        return null;
//
//    }
//
//    /**
//     * Get the IDs for a bunch of {@link de.hsmainz.gi.types.Beacon}s.
//     * and minor.
//     * @param   beacons     a List of Beacons to get IDs for
//     * @return  a List of Beacons that exist
//     */
//    public static List<Beacon> getBeacons(List<Beacon> beacons) {
//        String methodName = "getBeacons";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        CustomSoapSerializationEnvelope envelope = getEnvelope(request);
//        envelope.useHeader = false;
//        PropertyInfo properties;
//        for (Beacon b: beacons) {
//            properties = new PropertyInfo();
//            properties.name = "beacons";
//            properties.type = Beacon.class;
//            properties.setValue(b);
//            request.addProperty(properties);
//        }
//        envelope.addMapping(NAMESPACE, "getBeaconsResponse", KvmArrayList.class);
//        envelope.addMapping(NAMESPACE, "Beacon", Beacon.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(null, envelope);
//            testResponse(ht);
//            return (List<Beacon>) (SoapPrimitive) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "Other Exception", e);
//        }
//        return null;
//
//    }
//
//    /**
//     * Get the {@link com.vividsolutions.jts.geom.Point} at the provided
//     * {@link de.hsmainz.gi.types.Site} where the provided
//     * {@link de.hsmainz.gi.types.Beacon} is positioned.
//     * @param   site    the Site of the Beacon
//     * @param   beacon  the Beacon
//     * @return  the Coordinate of the Beacon on the Site
//     */
//    public static Point getCoordinate(Site site, Beacon beacon) {
//        String methodName = "getCoordinate";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        properties = new PropertyInfo();
//        properties.name = "beacon";
//        properties.type = Beacon.class;
//        properties.setValue(beacon);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getCoordinateResponse", Point.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (Point) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Get all {@link de.hsmainz.gi.types.WkbLocation}s of
//     * {@link de.hsmainz.gi.types.Beacon}s at the specified
//     * {@link de.hsmainz.gi.types.Site}.
//     * @param   site    the Site of the Locations
//     * @return  all Locations of Beacons at the Site
//     */
//    public static List<WkbLocation> getBeaconLocations(Site site) {
//        String methodName = "getBeaconLocationsFromSite";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.name = "site";
//        properties.type = Site.class;
//        properties.setValue(site);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getBeaconLocationsFromSiteResponse", KvmArrayList.class);
//        envelope.addMapping(NAMESPACE, "WkbLocation", WkbLocation.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (List<WkbLocation>) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//    /**
//     * Attempt to find all {@link de.hsmainz.gi.types.WkbLocation}s for
//     * the provided List of {@link de.hsmainz.gi.types.Beacon}s.
//     * @param   beacons  the Beacons for which a Site should be found
//     * @return  all Locations of Beacons on the Site determined by the input
//     */
//    public static List<WkbLocation> getBeaconLocations(List<Beacon> beacons) {
//        String methodName = "getBeaconLocationsFromBeaconList";
//        SoapObject request = new SoapObject(NAMESPACE, methodName);
//        SoapSerializationEnvelope envelope = getEnvelope(request);
//        PropertyInfo properties = new PropertyInfo();
//        properties.name = "beacons";
//        properties.type = KvmArrayList.class;
//        properties.setValue(beacons);
//        request.addProperty(properties);
//        envelope.addMapping(NAMESPACE, "getBeaconLocationsFromBeaconListResponse", KvmArrayList.class);
//        envelope.addMapping(NAMESPACE, "WkbLocation", WkbLocation.class);
//        HttpTransportSE ht = getTransport();
//        try {
//            ht.call(SOAP_ACTION, envelope);
//            testResponse(ht);
//            return (List<WkbLocation>) envelope.getResponse();
//        } catch (SoapFault e) {
//            Log.w(TAG, "Soap Problem", e);
//        } catch (SocketTimeoutException e) {
//            Log.w(TAG, "Socket timed out", e);
//        } catch (IOException e) {
//            Log.w(TAG, "IO Problem", e);
//        } catch (Exception e) {
//            Log.w(TAG, "", e);
//        }
//        return null;
//    }
//
//
//
//// ----------- private helpers -----------
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
//    private static final CustomSoapSerializationEnvelope getEnvelope(SoapObject request) {
//        CustomSoapSerializationEnvelope envelope = new CustomSoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.dotNet = false;
//        envelope.implicitTypes = true;
//        //envelope.avoidExceptionForUnknownProperty = true;
//        envelope.setAddAdornments(false);
//        envelope.setOutputSoapObject(request);
//        return envelope;
//    }
//
//    private static final HttpTransportSE getTransport() {
//        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, REQUEST_URL, 10000);
//        ht.debug = true;
//        //ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
//        return ht;
//    }
//
//    private static final List<HeaderProperty> getHeader() {
//        List<HeaderProperty> header = new ArrayList<>();
//        HeaderProperty headerPropertyObj = new HeaderProperty("cookie", SoapLocatorRequests.SESSION_ID);
//        header.add(headerPropertyObj);
//        return header;
//    }
}
