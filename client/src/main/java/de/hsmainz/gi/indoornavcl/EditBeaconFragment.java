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
import android.widget.TextView;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.util.StringUtils;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 19.02.15.
 */
public class EditBeaconFragment
        extends Fragment {

    public static final String  TAG = EditBeaconFragment.class.getSimpleName();

    private WkbLocation         location;
    private TextView            b_uuid,
                                b_major,
                                b_minor;
    private CoordinateFragment  cFrag;
    private boolean             isReady = false;

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
        View rootView = inflater.inflate(R.layout.edit_beacon_admin_fragment, container, false);
        b_uuid = (TextView) rootView.findViewById(R.id.beacon_uuid_text);
        b_major = (TextView) rootView.findViewById(R.id.beacon_major_text);
        b_minor = (TextView) rootView.findViewById(R.id.beacon_minor_text);

        CoordinateFragment cFrag = (CoordinateFragment) getChildFragmentManager().findFragmentById(R.id.coordinate);
        if (cFrag == null) {
            cFrag = new CoordinateFragment();
        }
        if (location != null) {
            setLocation();
        }
        isReady = true;
        return rootView;
    }

    public void setLocation(WkbLocation location) {
        Log.v(TAG, "setLocation called: " + StringUtils.toString(location));
        this.location = location;
        if (isReady)
            setLocation();
    }

    private void setLocation() {
        Beacon b = location.getBeacon();
        b_uuid.setText(b.getUuid());
        b_major.setText(Integer.toString(b.getMajor()));
        b_minor.setText(Integer.toString(b.getMinor()));
//        cFrag.setPoint(location.getCoord());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView() {
        isReady = false;

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