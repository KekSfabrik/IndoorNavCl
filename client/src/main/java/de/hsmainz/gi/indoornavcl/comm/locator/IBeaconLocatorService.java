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

package de.hsmainz.gi.indoornavcl.comm.locator;

import android.util.Log;
import de.hsmainz.gi.indoornavcl.comm.Configuration;
import de.hsmainz.gi.indoornavcl.comm.CustomSoapSerializationEnvelope;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.Proxy;
import java.util.*;

/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public final class IBeaconLocatorService {

    private static final String TAG     = IBeaconLocatorService.class.getSimpleName();

    private String              SESSION_ID;


    public Site getSite(String name) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getSite");
        Site ret = new Site();
        request.addProperty("name", name);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getSite)", ex);
            return ret;
        }
        testResponse(ht);
        SoapObject so = (SoapObject) envelope.getResponse();
        int len = so.getPropertyCount();
        for (int i = 0; i < len; i++) {
            ret.setProperty(i, so.getProperty(i));
        }
        return ret;
    }


    public Beacon getBeaconFromUuidMajorMinor(String uuid, int major, int minor) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getBeaconFromUuidMajorMinor");
        Beacon ret = new Beacon();
        request.addProperty("uuid", uuid);
        request.addProperty("major", major + "");
        request.addProperty("minor", minor + "");
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return ret;
        }
        testResponse(ht);
        SoapObject so = (SoapObject) envelope.getResponse();
        int len = so.getPropertyCount();
        for (int i = 0; i < len; i++) {
            ret.setProperty(i, so.getProperty(i));
        }
        return ret;
    }

    /**
     * FINE!
     * @param name
     * @return
     * @throws Exception
     */
    public Set<Site> getSiteByApproximateName(String name) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getSiteByApproximateName");
        Set<Site> ret = new HashSet<>();
        request.addProperty("name", name);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getSiteByApproximateName)", ex);
            return ret;
        }
        testResponse(ht);
        Object obj = envelope.getResponse();
        if (obj != null) {
            try {
                KvmSerializable ks = (KvmSerializable) obj;
                if (ks.getPropertyCount() == 1) {
                    ret.add(parseType((SoapObject) ks, Site.class));
                }
            } catch (ClassCastException ex) {
                Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
                for (SoapObject so: sos) {
                    ret.add(parseType(so, Site.class));
                }
            }
        }
        return ret;
    }


    public WkbPoint getCoordinate(Site site, Beacon beacon) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getCoordinate");
        WkbPoint ret = new WkbPoint();
        request.addProperty("site", site);
        request.addProperty("beacon", beacon);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        ht.call(null, envelope);
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getCoordinate)", ex);
            return ret;
        }
        SoapObject so = (SoapObject) envelope.getResponse();
        int len = so.getPropertyCount();
        for (int i = 0; i < len; i++) {
            ret.setProperty(i, so.getProperty(i));
        }
        return ret;
    }


    public Set<Site> getSitesFromBeaconList(Beacon[] beacons) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getSitesFromBeaconList");
        Set<Site> ret = new HashSet<>();
        PropertyInfo properties;
        for (Beacon b: beacons) {
            properties = new PropertyInfo();
            properties.name = "beacons";
            properties.type = Beacon.class;
            properties.setValue(b);
            request.addProperty(properties);
        }
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getSitesFromBeaconList)", ex);
            return ret;
        }
        testResponse(ht);
        Object obj = envelope.getResponse();
        if (obj != null) {
            try {
                KvmSerializable ks = (KvmSerializable) obj;
                if (ks.getPropertyCount() == 1) {
                    ret.add(parseType((SoapObject) ks, Site.class));
                }
            } catch (ClassCastException ex) {
                Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
                for (SoapObject so: sos) {
                    ret.add(parseType(so, Site.class));
                }
            }
        }
        return ret;
    }

    /**
     * FINE!
     * @param site
     * @return
     * @throws Exception
     */
    public Set<WkbLocation> getBeaconLocationsFromSite(Site site) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getBeaconLocationsFromSite");
        Set<WkbLocation> ret = new HashSet<>();
        request.addProperty("site", site);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconLocationsFromSite)", ex);
            return ret;
        }
        testResponse(ht);
        Object obj = envelope.getResponse();
        if (obj != null) {
            try {
                KvmSerializable ks = (KvmSerializable) obj;
                if (ks.getPropertyCount() == 1) {
                    ret.add(parseType((SoapObject) ks, WkbLocation.class));
                }
            } catch (ClassCastException ex) {
                Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
                for (SoapObject so: sos) {
                    ret.add(parseType(so, WkbLocation.class));
                }
            }
        }
        return ret;
//        Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
//        if (sos == null || sos.isEmpty())
//            return ret;
//        for (SoapObject s: sos) {
//            int len = s.getPropertyCount();
//            WkbLocation loc = new WkbLocation();
//            for (int i = 0; i < len; i++) {
//                loc.setProperty(i, s.getProperty(i));
//            }
//            ret.add(loc);
//        }
//        return ret;
    }


    /**
     * FINE!
     * @param beacon
     * @return
     * @throws Exception
     */
    public Beacon getBeacon(Beacon beacon) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getBeacon");
        Beacon ret = new Beacon();
        request.addProperty("beacon", beacon);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeacon)", ex);
            return ret;
        }
        testResponse(ht);
        SoapObject so = (SoapObject) envelope.getResponse();
        int len = so.getPropertyCount();
        for (int i = 0; i < len; i++) {
            ret.setProperty(i, so.getProperty(i));
        }
        return ret;
    }


    public Set<Site> getSitesFromBeacon(Beacon beacon) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getSitesFromBeacon");
        Set<Site> ret = new HashSet<>();
        request.addProperty("beacon", beacon);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getSitesFromBeacon)", ex);
            return ret;
        }
        testResponse(ht);
        Object obj = envelope.getResponse();
        if (obj != null) {
            KvmSerializable ks = (KvmSerializable) obj;
            if (ks.getPropertyCount() == 0) {
            } else if (ks.getPropertyCount() == 1) {
                ret.add(parseType((SoapObject) ks, Site.class));
            } else {
                Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
                for (SoapObject so: sos) {
                    ret.add(parseType(so, Site.class));
                }
            }
        }
        return ret;
    }


    public Set<WkbLocation> getBeaconLocationsFromBeaconList(Beacon[] beacons) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getBeaconLocationsFromBeaconList");
        Set<WkbLocation> ret = new HashSet<>();
        PropertyInfo properties;
        for (Beacon b: beacons) {
            properties = new PropertyInfo();
            properties.name = "beacons";
            properties.type = Beacon.class;
            properties.setValue(b);
            request.addProperty(properties);
        }
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconLocationsFromBeaconList)", ex);
            return ret;
        }
        testResponse(ht);
        Object obj = envelope.getResponse();
        if (obj != null) {
            try {
                KvmSerializable ks = (KvmSerializable) obj;
                if (ks.getPropertyCount() == 1) {
                    ret.add(parseType((SoapObject) ks, WkbLocation.class));
                }
            } catch (ClassCastException ex) {
                Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
                for (SoapObject so: sos) {
                    ret.add(parseType(so, WkbLocation.class));
                }
            }
        }
        return ret;
    }


    /**
     * FINE!
     * @param beacons
     * @return
     * @throws Exception
     */
    public Set<Beacon> getBeacons(Beacon[] beacons) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "getBeacons");
        Set<Beacon> ret = new HashSet<>();
        PropertyInfo properties;
        for (Beacon b: beacons) {
            properties = new PropertyInfo();
            properties.name = "beacons";
            properties.type = Beacon.class;
            properties.setValue(b);
            request.addProperty(properties);
        }
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconLocationsFromBeaconList)", ex);
            return ret;
        }
        testResponse(ht);
        Object obj = envelope.getResponse();
        if (obj != null) {
            try {
                KvmSerializable ks = (KvmSerializable) obj;
                if (ks.getPropertyCount() == 1) {
                    ret.add(parseType((SoapObject) ks, Beacon.class));
                }
            } catch (ClassCastException ex) {
                Vector<SoapObject> sos = (Vector<SoapObject>) envelope.getResponse();
                for (SoapObject so: sos) {
                    ret.add(parseType(so, Beacon.class));
                }
            }
        }
        return ret;
    }

// ----------- private helpers -----------

    private final String getSessionId(HttpTransportSE ht) throws IOException {
        List<HeaderProperty> COOKIE_HEADER = (List<HeaderProperty>) ht.getServiceConnection().getResponseProperties();
        for (int i = 0; i < COOKIE_HEADER.size(); i++) {
            String key = COOKIE_HEADER.get(i).getKey();
            String value = COOKIE_HEADER.get(i).getValue();

            if (key != null && key.equalsIgnoreCase("set-cookie")) {
                SESSION_ID = value.trim();
                Log.v(TAG, "Cookie: " + SESSION_ID);
                break;
            }
        }
        return SESSION_ID;
    }

    private final List<HeaderProperty> getHeader() {
        List<HeaderProperty> header = new ArrayList<>();
        HeaderProperty headerPropertyObj = new HeaderProperty("cookie", this.SESSION_ID);
        header.add(headerPropertyObj);
        return header;
    }

    private static final void testResponse(HttpTransportSE ht) {
        ht.debug = Configuration.isDebug();
        if (Configuration.isDebug()) {
            Log.v(TAG, "Request XML:\n" + ht.requestDump);
            Log.v(TAG, "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }

    private static final CustomSoapSerializationEnvelope getEnvelope(SoapObject request) {
        CustomSoapSerializationEnvelope envelope = new CustomSoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.implicitTypes = true;
        //envelope.avoidExceptionForUnknownProperty = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private static final HttpTransportSE getTransport() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, Configuration.getLocatorWsUrl(), Configuration.getTimeout());
        ht.debug = Configuration.isDebug();
        //ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
        return ht;
    }

    private static <T extends KvmSerializable> T parseType(SoapObject so, Class<T> type) throws IllegalAccessException, InstantiationException {
        T ret = (T) type.newInstance();
        for (int i = 0, len = so.getPropertyCount(); i < len; i++) {
            ret.setProperty(i, so.getProperty(i));
        }
        return ret;
    }
}
