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

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.util.Globals;
import de.hsmainz.gi.indoornavcl.util.StringUtils;
import de.hsmainz.gi.indoornavcl.util.TaskFragment;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class    MainActivity
    extends     ActionBarActivity
    implements  ActionBar.OnNavigationListener,
                TaskFragment.TaskCallbacks {

    private static final String         TAG = MainActivity.class.getSimpleName();
    private static final String         TAG_TASK_FRAGMENT = "main_activity_task_fragment";

    private TaskFragment                mTaskFragment;
    private Button                      buttonStart;
    private BeaconScanService           bs;
    private boolean                     isBound = false;
    private List<String>                availableSites = new ArrayList<>();
    private Site                        currentSite;
    private CoordinateFragment          coordFragment;
    private ArrayAdapter<String>        adapter;
    private ActionBar                   actionBar;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection           connection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName className, IBinder service) {
                        // We've bound to LocalService, cast the IBinder and get LocalService instance
                        BeaconScanService.LocalBinder binder = (BeaconScanService.LocalBinder) service;
                        bs = binder.getService();
                        isBound = true;
                    }
                    @Override
                    public void onServiceDisconnected(ComponentName arg0) {
                        bs = null;
                        isBound = false;
                    }};
    private Handler                      handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            switch (msg.what) {
                                case Globals.UPDATE_POSITION_MSG: {
                                    if (coordFragment != null) {
                                        Bundle b = msg.getData();
                                        WkbPoint wkbPoint = b.getParcelable(Globals.CURRENT_POSITION);
                                        if (wkbPoint != null && wkbPoint.isVerified()) {
                                            coordFragment.setPoint(wkbPoint);
                                        }
                                    } else {
                                        coordFragment = (CoordinateFragment) getFragmentManager().findFragmentById(R.id.coordinate_fragment);
                                    }
                                    // TODO display the new point in the map
                                }
                                    break;
                                case Globals.DISPLAY_TOAST_MSG: {
                                    showNotification(msg.getData().getString(Globals.DISPLAY_TOAST));
                                }
                                    break;
                                case Globals.SITE_CHANGED_MSG: {
                                    availableSites.clear();
                                    for (Site s: bs.getAllAvailableSites()) {
                                        availableSites.add(s.getName());
                                    }
                                    Bundle b = msg.getData();
                                    currentSite = b.getParcelable(Globals.SITE_CHANGED);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int position = adapter.getPosition(currentSite.getName());
                                            Log.v(TAG, "current site position: " + position + " ("+ StringUtils.toString(currentSite)+")");
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    // TODO update actionbar spinner to select the correct site
                                }
                                    break;
                            }
                            return false;
                        }
                    });
    /**
     * App startup
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "started");

        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        Intent intent = new Intent(this, BeaconScanService.class);
        intent.putExtra(Globals.UPDATE_POSITION_HANDLER, new Messenger(this.handler));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        buttonStart = (Button) getFragmentManager().findFragmentById(R.id.startbutton_fragment).getView().findViewById(R.id.btnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "START logging");
                // start logging for R.id.inIntervalLength seconds
                if (!bs.isScanning()) {
                    bs.startScanning();
                    Toast.makeText(buttonStart.getContext(), "Started", Toast.LENGTH_SHORT).show();
                    buttonStart.setText(R.string.stop);
                } else {
                    bs.stopScanning();
                    Toast.makeText(buttonStart.getContext(), "Stopped", Toast.LENGTH_SHORT).show();
                    buttonStart.setText(R.string.start);
                    bs.writeToFile(coordFragment.getCoords());
                }
            }
        });
        coordFragment = (CoordinateFragment) getFragmentManager().findFragmentById(R.id.coordinate_fragment);

        // Set up the action bar to show a dropdown list.
        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, android.R.id.text1, availableSites);
        actionBar.setListNavigationCallbacks(adapter, this);
    }

    public void showNotification(String str) {
        Toast.makeText(buttonStart.getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        if (bs.isScanning()) {
            bs.writeToFile(coordFragment.getCoords());
        }
    }
    /**
     * app shutdown
     */
    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    /**
     * This method is called whenever a navigation item in your action bar
     * is selected.
     *
     * @param itemPosition Position of the item clicked.
     * @param itemId       ID of the item clicked.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.v(TAG, "CALLBACK onNavigationItemSelected("+itemPosition+","+itemId+") = " + availableSites.get(itemPosition));
        return false;
    }


    // The four methods below are called by the TaskFragment when new
    // progress updates or results are available. The MainActivity
    // should respond by updating its UI to indicate the change.

    @Override
    public void onPreExecute() {  }

    @Override
    public void onProgressUpdate(int percent) {  }

    @Override
    public void onCancelled() {  }

    @Override
    public void onPostExecute() {  }
}
