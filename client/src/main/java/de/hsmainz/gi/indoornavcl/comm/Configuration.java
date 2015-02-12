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
     * Prohibits instantiation.
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
     * URL.
     */ /**
     *
     * @return The address which the web service is deployed.
     */
    public static String getLocatorWsUrl() {
        return wsUrl + locatorWs;
    }

    /**
     * URL.
     */ /**
     *
     * @return The address which the web service is deployed.
     */
    public static String getPositionerWsUrl() {
        return wsUrl + positionerWs;
    }

    public static String getUsername() {
        return username;
    }


    public static String getPassword() {
        return password;
    }


    public static int getTimeout() {
        return timeout;
    }


    public static boolean isDebug() {
        return debug;
    }

    public static String getNamespace() {
        return namespace;
    }

    public static String getTileServer() {
        return tileServer;
    }
}
