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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import de.hsmainz.gi.indoornavcl.comm.Configuration;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.positioning.TinyCoordinate;
import de.hsmainz.gi.indoornavcl.util.WebAppInterface;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 12.02.15.
 */
public class    MapFragment
    extends     Fragment {


    private static final String TAG = MapFragment.class.getSimpleName();

    private WebView     leaflet;

    private enum SITES {
        ug, eg, og1, og2, og3;
    }

    /**
     * App startup
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * called after {@link #onCreate} when the fragment is added
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
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


    public void setPoint(WkbPoint point) {
        TinyCoordinate coord = point.getCoordinate();
        coord.round(5);
        leaflet.loadUrl("javascript:setPosition("+coord.y+", "+coord.x+", "+ coord.z +")");
    }


    public void changeSite(Site site) {
        String s = "";
        if (site.getName().startsWith("UG")) {
            s = "ug";
        } else if (site.getName().startsWith("EG")) {
            s = "eg";
        } else if (site.getName().startsWith("1OG")) {
            s = "og1";
        } else if (site.getName().startsWith("2OG")) {
            s = "og2";
        } else if (site.getName().startsWith("3OG")) {
            s = "og3";
        }
        leaflet.loadUrl("javascript:changeSite(\""+s+"\")");
    }

    /**
     * called before {@link #onDestroy} when the fragment is removed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * app shutdown
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
