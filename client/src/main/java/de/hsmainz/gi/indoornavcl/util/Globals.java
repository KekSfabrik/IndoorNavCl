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

package de.hsmainz.gi.indoornavcl.util;

/**
 * Class with global Callback Parameters to allow Callback-based communication between Service and Activities and
 * callbacks from Threadrunners.
 *
 *
 *
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 04.02.15.
 */
public final class Globals {

    public static final String  UPDATE_POSITION_HANDLER = "updateposition";
    public static final String  CURRENT_POSITION        = "currentposition";
    public static final String  DISPLAY_TOAST           = "displaytoast";
    public static final String  SITES_AVAILABLE         = "sitesavailable";
    public static final String  SITE_CHANGED            = "sitechanged";

    public static final int     ZERO                                = 0;
    public static final int     UPDATE_POSITION_MSG                 = 1;
    public static final int     DISPLAY_TOAST_MSG                   = 2;
    public static final int     DETERMINE_SITE_CALLBACK_ARRIVED     = 3;
    public static final int     GET_LOCATIONS_CALLBACK_ARRIVED      = 4;
    public static final int     CALC_POSITION_CALLBACK_ARRIVED      = 5;
    public static final int     CURRENT_SITE_LOCATIONS_CALLBACK_ARRIVED  = 6;
    public static final int     SITES_AVAILABLE_CALLBACK_ARRIVED    = 7;
    public static final int     SITE_CHANGED_MSG                    = 8;




    /** no instances*/
    private Globals() { }

}
