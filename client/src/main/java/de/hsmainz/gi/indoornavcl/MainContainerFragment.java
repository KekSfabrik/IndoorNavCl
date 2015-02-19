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
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 19.02.15.
 */
public class    MainContainerFragment
    extends     Fragment {

    public static final String  TAG = MainContainerFragment.class.getSimpleName();

    private FragmentManager     fm;
    private CoordinateFragment  coordFragment;
    private MapFragment         mapFragment;
    private StartButtonFragment sbFragment;

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
        View rootView = inflater.inflate(R.layout.main_container_fragment, container, false);
        fm = getChildFragmentManager();
        coordFragment = (CoordinateFragment) fm.findFragmentById(R.id.coordinate_fragment_layout);
        if (coordFragment == null) {
            coordFragment = new CoordinateFragment();
            fm
            .beginTransaction()
            .add(R.id.coordinate_fragment_holder, coordFragment, CoordinateFragment.TAG)
            .addToBackStack(null)
            .commit();
        }
        mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment_layout);
        if (mapFragment == null) {
            mapFragment = new MapFragment();
            fm
            .beginTransaction()
            .add(R.id.map_fragment_holder, mapFragment, MapFragment.TAG)
            .addToBackStack(null)
            .commit();
        }
        sbFragment = (StartButtonFragment) fm.findFragmentById(R.id.startbutton_fragment_layout);
        if (sbFragment == null) {
            sbFragment = new StartButtonFragment();
            fm
            .beginTransaction()
            .add(R.id.startbutton_fragment_holder, sbFragment, StartButtonFragment.TAG)
            .addToBackStack(null)
            .commit();
        }
        return rootView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    public void setPoint(WkbPoint wkbPoint) {
        if (coordFragment != null && mapFragment != null) {
            if (wkbPoint != null && wkbPoint.isVerified()) {
                coordFragment.setPoint(wkbPoint);
                mapFragment.setPoint(wkbPoint);
            }
        } else {
            coordFragment = (CoordinateFragment) fm.findFragmentById(R.id.coordinate_fragment_layout);
            mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment_layout);
        }
    }

    public void changeSite(Site site) {
        if (mapFragment != null) {
            mapFragment.changeSite(site);
        } else {
            mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment_layout);
            if (mapFragment != null) {
                mapFragment.changeSite(site);
            }
        }
    }

    public void changeSite(String siteName) {
        if (mapFragment != null) {
            mapFragment.changeSite(siteName);
        } else {
            mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment_layout);
            if (mapFragment != null) {
                mapFragment.changeSite(siteName);
            }
        }
    }

    public String getCoords() {
        return coordFragment.getCoords();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}