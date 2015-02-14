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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.positioning.TinyCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that holds 3 {@link android.widget.TextView}s representing x, y and z Coordinates of a position.
 *
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

    /**
     * Set the x, y and z Coordinate {@link android.widget.TextView}s with the Coordinates by the input point.
     * @param   point   the point to show in the TextView Fields
     */
    public void setPoint(WkbPoint point) {
        TinyCoordinate coord = point.getCoordinate();
        coord.round(5);
        x.setText(coord.x + "");
        y.setText(coord.y + "");
        z.setText(coord.z + "");
        //((MainActivity)getActivity()).showNotification("Found 'POINT(" + coord.x + " " + coord.y + " " + coord.z + ")'");
        coords.add(coord);
    }

    /**
     * Getter for {@link #coords} deserialized by {@link com.google.gson.Gson}. Clears the list of
     * {@link de.hsmainz.gi.indoornavcl.positioning.TinyCoordinate} after deserialization.
     * @return  a String representation of all the logged coordinates
     */
    public String getCoords() {
        String str = gson.toJson(coords);
        coords.clear();
        return str;
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
