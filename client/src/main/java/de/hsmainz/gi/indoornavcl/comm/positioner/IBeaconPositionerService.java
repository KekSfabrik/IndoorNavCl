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
 * Class that communicates with the backing WebService to POSITION for users with administrative privileges.
 * The Username and Password allowing the user access to the "positioning" WebService can be set in the
 * {@link de.hsmainz.gi.indoornavcl.comm.Configuration} class.
 *
 *
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public final class IBeaconPositionerService {

    private static final String     TAG = IBeaconPositionerService.class.getSimpleName();
    private static String           SESSION_ID;

    /**
     * Put a {@link de.hsmainz.gi.indoornavcl.comm.types.Site} into the.
     * @param   site    the Site to put into the System
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Put a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} into the System.
     * @param   beacon    the Beacon to put into the System
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Put a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} into the System based on the beacons uuid, major & minor.
     * @param   uuid    the Beacons manufacturer UUID
     * @param   major   the Beacons MAJOR field
     * @param   minor   the Beacons MINOR field
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Wrapper to put a {@link de.hsmainz.gi.indoornavcl.comm.types.Site} into the System based on its name.
     * @param   name    the name of the Site to put into the System
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Deletes a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from the System. The input Beacon has to have its
     * ID field set and therefor {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon#isVerified} has to be
     * {@link java.lang.Boolean#TRUE}.
     * @param   beacon    the Beacon to delete from the System
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Removes a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from a
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} managed through the. The inputs have to have their
     * ID fields set and therefor {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be
     * {@link java.lang.Boolean#TRUE}.
     * @param   beacon  the Beacon to remove from the Site
     * @param   site    the Site to remove the Beacon from
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Places a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} at a Coordinate indicated by the given
     * {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation}. The input Beacon and Sites wrapped in
     * the WkbLocation have to have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * @param   wkbLocation     the location that should be added to the system
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Deletes a {@link de.hsmainz.gi.indoornavcl.comm.types.Site} from the System. The input Site has to have its
     * ID field set and therefor {@link de.hsmainz.gi.indoornavcl.comm.types.Site#isVerified} has to be
     * {@link java.lang.Boolean#TRUE}.
     * @param   site    the Site to delete from the System
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Removes a {@link de.hsmainz.gi.indoornavcl.comm.types.WkbLocation} from the System. The input Beacon and Sites
     * wrapped in the WkbLocation have to have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   wkbLocation    the location to remove from the System
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Placed a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} on a specific
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} and at a given {@link com.vividsolutions.jts.geom.Point}
     * Coordinate in the System. The input Beacon and Site have to have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   site        the Site the Beacon should be placed on
     * @param   beacon      the Beacon to place on that Site
     * @param   coordinate  the Coordinate of the Beacon on the Site
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Replaces a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} on a specific
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} in the System. The input Beacons and Site have to
     * have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   site        the Site the Beacon should be replaced at
     * @param   oldBeacon   the Beacon to be replaced
     * @param   newBeacon   the Beacon to place at the Coordinate of the old Beacon
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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

    /**
     * Relocates a {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} from one
     * {@link de.hsmainz.gi.indoornavcl.comm.types.Site} to another in the System. The input Beacon and Sites have to
     * have their ID fields set and therefor
     * {@link de.hsmainz.gi.indoornavcl.comm.types.IndoorNavEntity#isVerified} has to be {@link java.lang.Boolean#TRUE}.
     * {@link java.lang.Boolean#TRUE}.
     * @param   fromSite        the Site the Beacon was located at
     * @param   toSite          the Site the Beacon is moved to
     * @param   beacon          the Beacon that is relocated
     * @param   toCoordinate    the Coordinate of the Beacon on the new Site
     * @return  whether or not it was successful
     * @throws  Exception   if something goes wrong
     */
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
    /**
     * Getter for the Session-ID to allow for longer lasting Sessions instead of 1-Session-per-Request
     * @param   ht  the HttpTransportSe to get the SessionID from
     * @return  the SessionID
     * @throws  IOException if it fails to get the properties of the ht
     */
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

    /**
     * Show debug output with the input request and the resulting response (both XML)
     * @param   ht  the Transport to show the request and response for
     */
    private static final void testResponse(HttpTransportSE ht) {
        ht.debug = Configuration.isDebug();
        if (Configuration.isDebug()) {
            Log.v(TAG, "Request XML:\n" + ht.requestDump);
            Log.v(TAG, "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }

    /**
     * Configures the {@link org.ksoap2.SoapEnvelope} with the neccessary Headers to allow for username and password-
     * based communication to the WebService.
     * @param   request the request to wrap in the configured envelope
     * @return  the configured SoapEnvelope
     */
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

    /**
     * Getter for a new {@link org.ksoap2.transport.HttpTransportSE}
     * @return  a configured HttpTransportSe
     */
    private static final HttpTransportSE getTransport() {
        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, Configuration.getPositionerWsUrl(), Configuration.getTimeout());
        ht.debug = Configuration.isDebug();
        //ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
        return ht;
    }

    /**
     * Configures the header to be used on the envelope
     * @return  the header
     */
    private static final List<HeaderProperty> getHeader() {
        List<HeaderProperty> header = new ArrayList<>();
        HeaderProperty headerPropertyObj = new HeaderProperty("cookie", IBeaconPositionerService.SESSION_ID);
        header.add(headerPropertyObj);
        return header;
    }

}
