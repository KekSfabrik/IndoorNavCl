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
import org.xmlpull.v1.XmlPullParser;

/**
 *
 * Config Class
 * @version 03.02.2015
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 03.02.2015
 */
public final class Configuration {

    private static final int        timeout      = 10 * 1000;
    private static final boolean    debug        = true;

    private static String     wsUrl;
    private static String     locatorWs;
    private static String     positionerWs;
    private static String     username;
    private static String     password;
    private static String     namespace;
    private static String     tileServer;
    private static boolean    isInitialized = false;


    /**
     * Load Configuration based on the <code>../res/xml/config.xml</code> File and sets its variables accordingly.
     * @param   parser  the parser to be used to read from the configuration file
     */
    public Configuration(XmlPullParser parser) {
        if (!isInitialized) {
            try {
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    String setting = null;
                    if ((name != null) && name.equals("setting")) {
                        for(int i = 0, size = parser.getAttributeCount(); i < size; i++) {
                            String attrName = parser.getAttributeName(i);
                            if ((attrName != null)) {
                                switch (attrName) {
                                    case "wsUrl":
                                        this.wsUrl = parser.getAttributeValue(i);
                                        break;
                                    case "locatorWs":
                                        this.locatorWs = parser.getAttributeValue(i);
                                        break;
                                    case "positionerWs":
                                        this.positionerWs = parser.getAttributeValue(i);
                                        break;
                                    case "username":
                                        this.username = parser.getAttributeValue(i);
                                        break;
                                    case "password":
                                        this.password = parser.getAttributeValue(i);
                                        break;
                                    case "namespace":
                                        this.namespace = parser.getAttributeValue(i);
                                        break;
                                    case "tileServer":
                                        this.tileServer = parser.getAttributeValue(i);
                                        break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e("Configuration", "problem loading resources", ex);
            }
            isInitialized = true;
        }
    }

    /**
     *
     * @return The address at which the LOCATOR WebService is deployed.
     */
    public static String getLocatorWsUrl() {
        return wsUrl + locatorWs;
    }


    /**
     * Getter for {@link #}
     * @return The address which the POSITIONER WebService is deployed.
     */
    public static String getPositionerWsUrl() {
        return wsUrl + positionerWs;
    }

    /**
     * Getter for {@link #username}
     * @return  the Username to access the POSITIONER WebService
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Getter for {@link #password}
     * @return  the Password to access the POSITIONER WebService
     */
    public static String getPassword() {
        return password;
    }


    /**
     * Getter for {@link #timeout}
     * @return  the time in milliseconds a request to the WebService should take at most
     */
    public static int getTimeout() {
        return timeout;
    }

    /**
     * Getter for {@link #debug}
     * @return  whether or not the app is running in debug mode
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * Getter for {@link #namespace}
     * @return  the Namespace used by the WebService
     */
    public static String getNamespace() {
        return namespace;
    }

    /**
     * Getter for {@link #password}
     * @return  the URL of the (WMS-) TileServer to be used by the {@link de.hsmainz.gi.indoornavcl.MapFragment}
     */
    public static String getTileServer() {
        return tileServer;
    }
}
