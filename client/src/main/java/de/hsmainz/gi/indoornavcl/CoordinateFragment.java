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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.positioning.TinyCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 10.02.15.
 */
public class    CoordinateFragment
    extends     Fragment {

    private static final String     TAG = CoordinateFragment.class.getSimpleName();
    private TextView                x,
                                    y,
                                    z;
    private static final Gson       gson = new Gson();
    private List<TinyCoordinate>    coords = new ArrayList<>();

    public void setPoint(WkbPoint point) {
        TinyCoordinate coord = point.getCoordinate();
        coord.round(5);
        x.setText(coord.x + "");
        y.setText(coord.y + "");
        z.setText(coord.z + "");
        ((MainActivity)getActivity()).showNotification("Found 'POINT(" + coord.x + " " + coord.y + " " + coord.z + ")'");
        coords.add(coord);
    }

    public String getCoords() {
        String str = gson.toJson(coords);
        coords.clear();
        return str;
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
        View view = inflater.inflate(R.layout.coordinate_fragment, container, false);
        x = (TextView) view.findViewById(R.id.txtX);
        y = (TextView) view.findViewById(R.id.txtY);
        z = (TextView) view.findViewById(R.id.txtZ);
        // Inflate the layout for this fragment
        return view;
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
