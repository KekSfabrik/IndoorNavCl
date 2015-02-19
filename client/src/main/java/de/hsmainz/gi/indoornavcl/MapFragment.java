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

package de.hsmainz.gi.indoornavcl;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import de.hsmainz.gi.indoornavcl.comm.Configuration;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.positioning.TinyCoordinate;
import de.hsmainz.gi.indoornavcl.util.WebAppInterface;

/**
 * Fragment that holds a Leaflet based Map in a WebView.
 *
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 12.02.15.
 */
public class    MapFragment
    extends     Fragment {


    public static final String  TAG = MapFragment.class.getSimpleName();

    private WebView             leaflet;

    private enum SITE {
        ug, eg, og1, og2, og3, test
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * {@inheritDoc}
     * <p>Sets up the Leaflet based {@link android.webkit.WebView} and sets up TileServer etc
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        leaflet = (WebView) view.findViewById(R.id.leaflet);
        leaflet.clearCache(true);
        leaflet.addJavascriptInterface(new WebAppInterface(this.getActivity()), "Android");
        leaflet.getSettings().setJavaScriptEnabled(true);
        leaflet.loadUrl("file:///android_asset/index.html");
        String ts = Configuration.getTileServer();
        Log.v(TAG, "setting tileserver to " + ts);
        //leaflet.loadUrl("javascript:setup(\""+ ts +"\")");
        return view;
    }

    /**
     * Puts the Marker on the position on the Map given by the argument.
     * @param   point   the position to put the marker to.
     */
    public void setPoint(WkbPoint point) {
        TinyCoordinate coord = point.getCoordinate();
        leaflet.loadUrl("javascript:setPosition("+coord.y+", "+coord.x+", "+ coord.z +")");
    }

    public void changeSite(String siteName) {
        SITE s;
        if (siteName != null && siteName.length() >= 3) {
            if (siteName.startsWith("UG")) {
                s = SITE.ug;
            } else if (siteName.startsWith("EG")) {
                s = SITE.eg;
            } else if (siteName.startsWith("1OG")) {
                s = SITE.og1;
            } else if (siteName.startsWith("2OG")) {
                s = SITE.og2;
            } else if (siteName.startsWith("3OG")) {
                s = SITE.og3;
            } else {
                s = SITE.test;
            }
        } else {
            s = SITE.test;
        }
        Log.d(TAG, "Calling changeSite(\"" + s + "\") in JS");
        leaflet.loadUrl("javascript:changeSite(\"" + s + "\")");
    }

    public void changeSite(Site site) {
        changeSite(site.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
