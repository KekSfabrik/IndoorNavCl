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

package de.hsmainz.gi.indoornavcl.comm.positioner;

import android.util.Log;
import de.hsmainz.gi.indoornavcl.comm.Configuration;
import de.hsmainz.gi.indoornavcl.comm.CustomSoapSerializationEnvelope;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public final class IBeaconPositionerService {

    private static final String     TAG     = IBeaconPositionerService.class.getSimpleName();

    private static String           SESSION_ID;


    public boolean addSite(Site site) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "addSite");
        request.addProperty("site", site);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean addBeacon(Beacon beacon) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "addBeacon");
        request.addProperty("beacon", beacon);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean addBeaconFromUuidMajorMinor(java.lang.String uuid, int major, int minor) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "addBeaconFromUuidMajorMinor");
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
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean addSiteFromName(java.lang.String name) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "addSiteFromName");
        request.addProperty("name", name);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean deleteBeacon(Beacon beacon) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "deleteBeacon");
        request.addProperty("beacon", beacon);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean removeBeaconFromSite(Beacon beacon, Site site) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "removeBeaconFromSite");
        request.addProperty("beacon", beacon);
        request.addProperty("site", site);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean placeBeaconAtLocation(WkbLocation wkbLocation) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "placeBeaconAtLocation");
        request.addProperty("wkbLocation", wkbLocation);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean deleteSite(Site site) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "deleteSite");
        request.addProperty("site", site);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean removeLocation(WkbLocation wkbLocation) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "removeLocation");
        request.addProperty("wkbLocation", wkbLocation);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean placeBeacon(Site site, Beacon beacon, WkbPoint coordinate) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "placeBeacon");
        request.addProperty("site", site);
        request.addProperty("beacon", beacon);
        request.addProperty("coordinate", coordinate);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean replaceBeacon(Site site, Beacon oldBeacon, Beacon newBeacon) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "replaceBeacon");
        request.addProperty("site", site);
        request.addProperty("oldBeacon", oldBeacon);
        request.addProperty("newBeacon", newBeacon);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }


    public boolean relocateBeacon(Site fromSite, Site toSite, Beacon beacon, WkbPoint toCoordinate) throws Exception {
        SoapObject request = new SoapObject(Configuration.getNamespace(), "relocateBeacon");
        request.addProperty("fromSite", fromSite);
        request.addProperty("toSite", toSite);
        request.addProperty("beacon", beacon);
        request.addProperty("toCoordinate", toCoordinate);
        SoapSerializationEnvelope envelope = getEnvelope(request);
        envelope.bodyOut = request;
        HttpTransportSE ht = getTransport();
        try {
            ht.call(null, envelope);
        } catch (Exception ex) {
            Log.w(TAG, "Problem communicating with the Server (getBeaconFromUuidMajorMinor)", ex);
            return false;
        }
        testResponse(ht);
        return envelope.getResponse().toString().equals("true");
    }
    // ----------- private helpers -----------

    private static final String getSessionId(HttpTransportSE ht) throws IOException {
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

    private static final void testResponse(HttpTransportSE ht) {
        ht.debug = Configuration.isDebug();
        if (Configuration.isDebug()) {
            Log.v(TAG, "Request XML:\n" + ht.requestDump);
            Log.v(TAG, "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }

    private static final SoapSerializationEnvelope getEnvelope(SoapObject request) {
        CustomSoapSerializationEnvelope envelope = new CustomSoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.useHeader = true;

        Element[] header = new Element[1];
        header[0] = new Element().createElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security");
        header[0].setAttribute(null, "mustUnderstand","1");

        Element usernameTokenElement = new Element().createElement("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken");
        usernameTokenElement.setAttribute(null, "Id", "UsernameToken-");
        header[0].addChild(Node.ELEMENT, usernameTokenElement);

        Element userElem = new Element().createElement(null, "n0:Username");
        userElem.addChild(Node.IGNORABLE_WHITESPACE, Configuration.getUsername());
        usernameTokenElement.addChild(Node.ELEMENT, userElem);

        Element passElem = new Element().createElement(null,"n0:Password");
        passElem.setAttribute(null, "Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
        passElem.addChild(Node.TEXT, Configuration.getPassword());

        usernameTokenElement.addChild(Node.ELEMENT, passElem);


        // add header to envelope
        envelope.headerOut = header;


        Log.i(TAG, "Header: " + envelope.headerOut.toString());


        envelope.dotNet = false;
        envelope.bodyOut = request;
        envelope.setOutputSoapObject(request);
        Log.i(TAG, "Body: " + envelope.bodyOut.toString());
        //--------------------
        envelope.dotNet = false;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private static final HttpTransportSE getTransport() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, Configuration.getPositionerWsUrl(), Configuration.getTimeout());
        ht.debug = Configuration.isDebug();
        //ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
        return ht;
    }

    private static final List<HeaderProperty> getHeader() {
        List<HeaderProperty> header = new ArrayList<>();
        HeaderProperty headerPropertyObj = new HeaderProperty("cookie", IBeaconPositionerService.SESSION_ID);
        header.add(headerPropertyObj);
        return header;
    }

}
