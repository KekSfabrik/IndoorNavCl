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

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.util.BeaconLocationRowAdapter;
import de.hsmainz.gi.indoornavcl.util.BeaconRowAdapter;
import de.hsmainz.gi.indoornavcl.util.Globals;
import de.hsmainz.gi.indoornavcl.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class    MainActivity
    extends     Activity
    implements  ActionBar.OnNavigationListener,
                DialogInterface.OnClickListener,
                AdapterView.OnItemClickListener,
                AdapterView.OnItemLongClickListener {

    private static final String         TAG = MainActivity.class.getSimpleName();


    enum CURRENTMENU {
        USER, ADMIN_OVERVIEW, BEACON_ADMIN, SITE_ADMIN, LOCATION_ADMIN, EDIT_BEACON, EDIT_SITE, EDIT_LOCATION
    }
    private BeaconScanService           bs;
    private boolean                     isBound = false;
    private List<String>                availableSites = new ArrayList<>();
    private Site                        currentSite;
    private FragmentManager             fm;
    private MainContainerFragment       mcFragment;
    private AdminFragment               adminFragment;
    private BeaconAdminFragment         beaconAdminFragment;
    private SiteAdminFragment           siteAdminFragment;
    private LocationAdminFragment       locationAdminFragment;
    private EditBeaconFragment          editBeaconFragment;
    private ArrayAdapter<String>        adapter;
    private ActionBar                   actionBar;
    private MenuItem                    spinnerItem;
    private CURRENTMENU                 currentMenu;

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

    /**
     * The Callback {@link android.os.Handler} ({@link android.os.Handler.Callback}) which evaluates the callbacks from
     * the Service {@link de.hsmainz.gi.indoornavcl.BeaconScanService}.
     */
    private Handler                      handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            switch (msg.what) {
                                case Globals.UPDATE_POSITION_MSG: {
                                    Bundle b = msg.getData();
                                    WkbPoint wkbPoint = b.getParcelable(Globals.CURRENT_POSITION);
                                    mcFragment.setPoint(wkbPoint);
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
                                    Collections.sort(availableSites);
                                    Bundle b = msg.getData();
                                    currentSite = b.getParcelable(Globals.SITE_CHANGED);
                                    mcFragment.changeSite(currentSite);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int position = adapter.getPosition(currentSite.getName());
                                            Log.v(TAG, "current site position: " + position + " ("+ StringUtils.toString(currentSite)+")");
                                            ((Spinner) spinnerItem.getActionView()).setSelection(position);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                                    break;
                            }
                            return false;
                        }
                    });
    /**
     * {@inheritDoc}
     * <p>Configures GUI elements and binds to the {@link de.hsmainz.gi.indoornavcl.BeaconScanService}.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "started");
        Intent intent = new Intent(this, BeaconScanService.class);
        intent.putExtra(Globals.UPDATE_POSITION_HANDLER, new Messenger(this.handler));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            /**
             * Called whenever the contents of the back stack change.
             */
            @Override
            public void onBackStackChanged() {
                Log.v(TAG, "OnBackStackChanged called");
                if (fm.getBackStackEntryCount() == 0) {
                    currentMenu = CURRENTMENU.USER;
                } else {
                    currentMenu = CURRENTMENU.valueOf(fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName());
                }
                for (int i = 0, k = fm.getBackStackEntryCount(); i<k; i++) {
                    Log.v(TAG, "BSE: " + i + "\t" + fm.getBackStackEntryAt(i).getName());
                }
            }
        });
        mcFragment = new MainContainerFragment();
        fm
        .beginTransaction()
        .replace(R.id.fragment_container, mcFragment)
        .commit();
        currentMenu = CURRENTMENU.USER;

        // Set up the action bar to show a dropdown list.
        actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableSites);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    }

    /**
     * Show a {@link android.widget.Toast} with the given Text
     * @param   str     the Text shown by the Toast
     */
    public void showNotification(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        if (bs.isScanning()) {
            bs.writeToFile(mcFragment.getCoords());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    /**
     * {@inheritDoc}
     * <p>Binds the {@link android.widget.Spinner} to the {@link android.widget.ArrayAdapter} {@link #adapter} on the
     * {@link android.app.ActionBar} {@link #actionBar} so the {@link de.hsmainz.gi.indoornavcl.comm.types.Site} selection
     * by the user can be handed over to the {@link de.hsmainz.gi.indoornavcl.BeaconScanService} {@link #bs}.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        spinnerItem = menu.findItem( R.id.spinner);
        View view1 = spinnerItem.getActionView();
        if (view1 instanceof Spinner) {
            final Spinner spinner = (Spinner) view1;
            spinner.setAdapter(adapter);
            Log.d(TAG, "SPINNER setAdapter");
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Log.d(TAG, "SPINNER onItemSelected 0: " + arg0 + " 1: " + arg1 + " 2: " + arg2 + " 3: " + arg3);
                    Log.v(TAG, "CALLBACK onNavigationItemSelected(" + arg2 + "," + arg3 + ") = " + availableSites.get(arg2));
                    boolean switchedSite = bs.setCurrentSite(availableSites.get(arg2));
                    if (switchedSite) {
                        mcFragment.changeSite(availableSites.get(arg2));
                        bs.setUserOverride(true);
                        // TODO remove this or find a better way
                        // terrible hacky workaround since onNothingSelected is never fired but instead
                        // a warning is logged by InputEventReceiver:
                        // "Attempted to finish an input event but the input event receiver has already been disposed."
                        // so 10 seconds delay until it gets reset
                        new Runnable() {
                            @Override
                            public void run() {
                                bs.setUserOverride(false);
                                handler.postDelayed(this, 10000);
                            }
                        };
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    Log.d(TAG, "SPINNER onNothingSelected 0: " + arg0);
                    bs.setUserOverride(false);
                }
            });
            Log.d(TAG, "SPINNER setOnItemSelectedListener");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.settings:
                showNotification("SETTINGS ButtonClicked");
                setCurrentFragment(CURRENTMENU.ADMIN_OVERVIEW);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * This method is called whenever a navigation item in the {@link android.app.ActionBar} is selected.
     * @param itemPosition Position of the item clicked.
     * @param itemId       ID of the item clicked.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.v(TAG, "CALLBACK onNavigationItemSelected("+itemPosition+","+itemId+") = " + availableSites.get(itemPosition));
        boolean switchedSite = bs.setCurrentSite(availableSites.get(itemPosition));
        if (switchedSite) {
            mcFragment.changeSite(availableSites.get(itemPosition));
        }
        return switchedSite;
    }

    public void setCurrentFragment(final CURRENTMENU menu) {
        if (menu != currentMenu) {
            String              tag = currentMenu == null ? null : currentMenu.name();
            Fragment            newFragment;
            FragmentTransaction ft = fm.beginTransaction();
            switch (menu) {
                case USER:
                    mcFragment = new MainContainerFragment();
                    newFragment = mcFragment;
                    break;
                case ADMIN_OVERVIEW:
                    adminFragment = new AdminFragment();
                    newFragment = adminFragment;
                    ft.addToBackStack(tag);
                    break;
                case BEACON_ADMIN:
                    beaconAdminFragment = new BeaconAdminFragment();
                    newFragment = beaconAdminFragment;
                    ft.addToBackStack(CURRENTMENU.ADMIN_OVERVIEW.name());
                    break;
                case SITE_ADMIN:
                    siteAdminFragment = new SiteAdminFragment();
                    newFragment = siteAdminFragment;
                    ft.addToBackStack(CURRENTMENU.ADMIN_OVERVIEW.name());
                    break;
                case LOCATION_ADMIN:
                    locationAdminFragment = new LocationAdminFragment();
                    newFragment = locationAdminFragment;
                    ft.addToBackStack(CURRENTMENU.ADMIN_OVERVIEW.name());
                    break;
                case EDIT_BEACON:
                    editBeaconFragment = new EditBeaconFragment();
                    newFragment = editBeaconFragment;
                    ft.addToBackStack(CURRENTMENU.BEACON_ADMIN.name());
                    break;
                /*case EDIT_SITE:
                    break;
                case EDIT_LOCATION:
                    break;*/
                default:
                    mcFragment = new MainContainerFragment();
                    newFragment = mcFragment;
            }
            ft.replace(R.id.fragment_container, newFragment, menu.name());
            ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            currentMenu = menu;
        }
    }

    public boolean isScanning() {
        return bs.isScanning();
    }

    public void startScanning() {
        bs.startScanning();
    }

    public Beacon[] getBeaconList() {
        List<Beacon> currentBeacons = new ArrayList<>();
        for (WkbLocation loc: getLocations()) {
            currentBeacons.add(loc.getBeacon());
        }
        return currentBeacons.toArray(new Beacon[0]);
    }

    public WkbLocation[] getLocations() {
        return bs.getCurrentSiteLocations().toArray(new WkbLocation[0]);
    }


    public List<String> getSites() {
        return availableSites;
    }

    public void stopScanning() {
        bs.stopScanning();
        bs.writeToFile(mcFragment.getCoords());
    }

    /**
     * Tell the Backend to write all logged Positions since the last scan start to a file.
     * @param   what    the Positions
     */
    public void writeToFile(String what) {
        bs.writeToFile(what);
    }

    /**
     * This method will be invoked when a button in the dialog is clicked.
     *
     * @param dialog The dialog that received the click.
     * @param which  The button that was clicked (e.g.
     *               {@link android.content.DialogInterface#BUTTON1}) or the position
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {

        // TODO call correct delete method for selection
        Log.v(TAG, "Click on dialog " + dialog + "(which: " + which + ")");
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() instanceof ArrayAdapter) {
            Log.v(TAG, "Parent is instance of ArrayAdapter -> SiteAdminFragment");
        } else if (parent.getAdapter() instanceof BeaconRowAdapter) {
            Log.v(TAG, "Parent is instance of BeaconRowAdapter -> BeaconAdminFragment");
            Beacon beacon = (Beacon) parent.getItemAtPosition(position);
            setCurrentFragment(CURRENTMENU.EDIT_BEACON);
            List<WkbLocation> locations = new ArrayList<>(bs.getCurrentSiteLocations());
            for (WkbLocation loc: locations) {
                if (loc.getSite().equals(currentSite)
                        && loc.getBeacon().equals(beacon)) {
                    editBeaconFragment.setLocation(loc);
                    break;
                }
            }
        } else if (parent.getAdapter() instanceof BeaconLocationRowAdapter) {
            Log.v(TAG, "Parent is instance of BeaconLocationRowAdapter -> LocationAdminFragment");
        }

        // TODO go to correct fragment
        Log.v(TAG, "Click on " + view + "(parent: " + parent + ") " + position + " " + id);
    }

    /**
     * Callback method to be invoked when an item in this view has been
     * clicked and held.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent   The AbsListView where the click happened
     * @param view     The view within the AbsListView that was clicked
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() instanceof ArrayAdapter) {
            Log.v(TAG, "Parent is instance of ArrayAdapter");
        } else if (parent.getAdapter() instanceof BeaconRowAdapter) {
            Log.v(TAG, "Parent is instance of BeaconRowAdapter");
        } else if (parent.getAdapter() instanceof BeaconLocationRowAdapter) {
            Log.v(TAG, "Parent is instance of BeaconLocationRowAdapter");
        }

        // TODO show delete dialog
        Log.v(TAG, "LongClick on " + view + "(parent: " + parent + ") " + position + " " + id);
        return false;
    }

}
