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

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.SoapLocatorRequests;
import de.hsmainz.gi.indoornavcl.comm.SoapPositionerRequests;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.LocationId;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.positioning.Locator;
import de.hsmainz.gi.indoornavcl.positioning.LocatorImpl1;
import de.hsmainz.gi.indoornavcl.positioning.Measurement;
import de.hsmainz.gi.indoornavcl.util.Globals;
import de.hsmainz.gi.indoornavcl.util.StringUtils;
import org.altbeacon.beacon.*;

import java.util.*;

/**
 * @author Jan 'KekS' M. <a href='mailto:keks@keksfabrik.eu'>mail</a>, 04.02.15.
 */
public class BeaconScanService
        extends     Service
        implements  BeaconConsumer {

    private static final String         TAG = BeaconScanService.class.getSimpleName();
    private static final boolean        DEBUG = true;

    private static final int            beaconScanInterval              =   500;
    private static final int            beaconScanWaitInterval          =     0;
    private static final int            beaconBackgroundScanInterval    = 10000;
    private static final int            beaconBackgroundScanWaitInterval= 20000;

    private Set<Beacon>                 loggedBeacons       = Collections.synchronizedSet(new HashSet<Beacon>()),
                                        checkedBeacons      = Collections.synchronizedSet(new HashSet<Beacon>()),
                                        unregisteredBeacons = Collections.synchronizedSet(new HashSet<Beacon>());
    private Site                        currentSite         = new Site();
    private Set<Site>                   availableSites      = new HashSet<>();
    private boolean                     siteHasChanged      = true;
    private Set<WkbLocation>            currentSiteLocations= Collections.synchronizedSet(new HashSet<WkbLocation>());
    private Map<WkbLocation, Measurement>
                                        currentSiteMeasurements= Collections.synchronizedMap(new TreeMap<WkbLocation, Measurement>());
    private Region                      region;
    private Point                       currentClientLocation;
    private BeaconScannerApplication    app;
    private boolean                     isScanning;
    private final IBinder               binder = new LocalBinder();
    private Messenger                   updateBeaconPositionMessenger;
    private Locator                     locator = new LocatorImpl1();

    /** whether or not the user is an administrator */
    private static boolean              isAdminUser = true;

    private Handler                     handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            switch (msg.what) {
                                case Globals.ZERO:
                                    break;
                                case Globals.GET_LOCATIONS_CALLBACK_ARRIVED:
                                    synchronized (currentSiteLocations) {
                                        for (WkbLocation loc: currentSiteLocations) {
                                            if (!currentSiteMeasurements.containsKey(loc)) {
                                                currentSiteMeasurements.put(loc, null);
                                            }
//                                            Log.v(TAG, "GET_LOCATIONS_CALLBACK_ARRIVED -> "+StringUtils.toString(loc));
                                        }
                                    }
                                    break;
                                case Globals.CALC_POSITION_CALLBACK_ARRIVED:
                                    uiUpdatePosition(new WkbPoint(currentClientLocation));
                                    break;
                                case Globals.CURRENT_SITE_LOCATIONS_CALLBACK_ARRIVED:
                                    determineSite();
                                    getLocationsForCurrentSite();
                                    break;
                                case Globals.SITES_AVAILABLE_CALLBACK_ARRIVED:
                                    uiSiteChanged();
                                    break;
                            }
                            return false;
                        }
                    });

    /**
     * start looking for beacons.
     */
    public void startScanning() {
        Log.v(TAG, "START scanning");
        verifyBluetooth();
        try {
            app.getBeaconManager().startRangingBeaconsInRegion(region);
            //tryAdminInterface();
            isScanning = true;
        } catch (RemoteException e) {
            Log.w(TAG, "problem with startRangingBeaconsInRegion", e);
        }
        Log.v(TAG, "started scanning");
    }

    /**
     * Stop looking for beacons.
     */
    public void stopScanning() {
        Log.v(TAG, "STOP scanning");
        try {
            app.getBeaconManager().stopRangingBeaconsInRegion(region);
            isScanning = false;
        } catch (RemoteException e) {
            Log.w(TAG, "problem with stopRangingBeaconsInRegion", e);
        }
        Log.v(TAG, "stopped scanning");
    }

    /**
     * make sure bluetooth is available or show a {@link android.widget.Toast} and shut down
     */
    public void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        System.exit(0);
                    }
                });
                builder.show();
            } else {
                Log.d(TAG, "BT verified");
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    System.exit(0);
                }

            });
            builder.show();
        }
    }

    private void getAvailableSites() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                availableSites.addAll(SoapLocatorRequests.getSiteByApproximateName(""));
                Log.v(TAG, "getAvailableSites() -> availableSites = " + Arrays.toString(StringUtils.listAll(availableSites)));
                handler.sendEmptyMessage(Globals.SITES_AVAILABLE_CALLBACK_ARRIVED);
            }
        }).start();
    }

    private void updateCheckedBeacons() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (loggedBeacons) {
                    if (!loggedBeacons.isEmpty()) {
                        checkedBeacons.addAll(SoapLocatorRequests.getBeacons(loggedBeacons));
                        Log.v(TAG, "updateCheckedBeacons() -> checkedBeacons = " + Arrays.toString(StringUtils.listAll(checkedBeacons)));
                    }
                }
                handler.sendEmptyMessage(Globals.ZERO);
            }
        }).start();
    }

    private void updateCheckedBeacons(Set<WkbLocation> locations) {
        for (WkbLocation loc : locations) {
            checkedBeacons.add(loc.getBeacon());
        }
        synchronized (loggedBeacons) {
            if (checkedBeacons != null) {
                for (Beacon b : loggedBeacons) {
                    Log.v(TAG, "Checked Beacon(" + StringUtils.toString(b) + ")");
                    if (!checkedBeacons.contains(b)) {
                        unregisteredBeacons.add(b);
                    }
                }
                loggedBeacons.clear();
            }
        }
    }
    private void getLocationsFromLoggedBeacons() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (loggedBeacons) {
                    if (!loggedBeacons.isEmpty()) {
                        currentSiteLocations.addAll(SoapLocatorRequests.getBeaconLocationsFromBeaconList(loggedBeacons));
                        updateCheckedBeacons(currentSiteLocations);
                        Log.v(TAG, "getLocationsFromLoggedBeacons() -> currentSiteLocations = " + Arrays.toString(StringUtils.listAll(currentSiteLocations)));
                    }
                }
                handler.sendEmptyMessage(Globals.CURRENT_SITE_LOCATIONS_CALLBACK_ARRIVED);
            }
        }).start();
    }


    private void getLocationsFromCheckedBeacons() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (checkedBeacons) {
                    if (checkedBeacons != null && !checkedBeacons.isEmpty()) {
                        currentSiteLocations = SoapLocatorRequests.getBeaconLocationsFromBeaconList(checkedBeacons);
                        Log.v(TAG, "getLocationsFromCheckedBeacons() -> currentSiteLocations = "+Arrays.toString(StringUtils.listAll(currentSiteLocations)));
                    }
                }
                handler.sendEmptyMessage(Globals.GET_LOCATIONS_CALLBACK_ARRIVED);
            }
        }).start();
    }

    private void determineSite() {
        synchronized (currentSiteLocations) {
            if (currentSiteLocations != null && !currentSiteLocations.isEmpty()) {
                Map<Site, Integer> hits = new TreeMap<>();
                for (WkbLocation loc : currentSiteLocations) {
                    Site site = loc.getSite();
                    if (hits.containsKey(site)) {
                        hits.put(site, hits.get(site) + 1);
                    } else {
                        hits.put(site, 1);
                    }
                }
                TreeMap<Integer, Site> mostLikely = new TreeMap<>();
                for (Map.Entry<Site, Integer> s : hits.entrySet()) {
                    mostLikely.put(s.getValue(), s.getKey());
                }
                Site site = mostLikely.lastEntry().getValue();
                siteHasChanged = currentSite == null || !currentSite.isVerified() ? true : !currentSite.equals(site);
                currentSite = site;
                Log.v(TAG, "Most likely Site: " + StringUtils.toString(currentSite));
            }
        }
    }

    private void getLocationsForCurrentSite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (currentSiteLocations) {
                    if (currentSite != null
                        && currentSite.isVerified()
                        && siteHasChanged) {
                        currentSiteLocations = SoapLocatorRequests.getBeaconLocationsFromSite(currentSite);
                        Log.v(TAG, "getLocationsForCurrentSite() -> currentSiteLocations = "+Arrays.toString(StringUtils.listAll(currentSiteLocations)));
                    }
                }
                handler.sendEmptyMessage(Globals.GET_LOCATIONS_CALLBACK_ARRIVED);
            }
        }).start();
    }


    private boolean checkMeasuredBeacon(Beacon beacon, Measurement measurement) {
        boolean calcPosition = false;
        synchronized (loggedBeacons) {
            for (Beacon b: checkedBeacons) {
                if (b.equals(beacon)) {
                    WkbLocation location = new WkbLocation();
                    location.setBeacon(b);
                    location.setSite(currentSite);
                    location.setId(new LocationId(b.getId(), currentSite.getSite()));
                    for (Map.Entry<WkbLocation, Measurement> entry : currentSiteMeasurements.entrySet()) {
                        if (entry.getKey().equals(location)) {
                            Log.v(TAG, "found location: replacing " + StringUtils.toString(location) + " with " + StringUtils.toString(entry.getKey()));
                            location = entry.getKey();
                            break;
                        }
                    }
                    Log.d(TAG, "currentSiteMeasurements.containsKey("+StringUtils.toString(location)+") = " + currentSiteMeasurements.containsKey(location));
                    if (currentSiteMeasurements.containsKey(location)) {
                        currentSiteMeasurements.put(location, measurement);
                        calcPosition = true;
                    }
                    break;
                }
            }
            if (!checkedBeacons.contains(beacon)
                && !unregisteredBeacons.contains(beacon)) {
                loggedBeacons.add(beacon);
            }
            Log.v(TAG, "================== currentSiteMeasurements: ================== ");
            for (Map.Entry<WkbLocation, Measurement> entry : currentSiteMeasurements.entrySet()) {
                Log.v(TAG, StringUtils.toString(entry.getKey()) + " -> " + entry.getValue());
            }
            Log.v(TAG, "================== currentSiteMeasurements EOF ================== ");
            if (calcPosition) {
                Log.d(TAG, "calcPosition -> start to calculate the position!");
            }
        }
        return calcPosition;
    }


    private void calcPosition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (currentSiteMeasurements) {
                    currentClientLocation = locator.getLocation(currentSiteMeasurements);
                    Log.v(TAG, "calcPosition() -> Calculated site: " + currentClientLocation.toText());
                    for (Beacon b: unregisteredBeacons) {
                        Log.v(TAG, "UNREGISTERED: " + StringUtils.toString(b));
                    }
                    handler.sendEmptyMessage(Globals.CALC_POSITION_CALLBACK_ARRIVED);
                }
            }
        }).start();
    }

    /*
    ==================================================================================


    ==================================================================================


    ==================================================================================
     */


    /**
     * try out the admin interface with all its commands
     */
    private void tryAdminInterface() {
        AsyncTask<String,Void, Long> task = new AsyncTask<String, Void, Long>() {
            protected Long doInBackground(String... params) {
                long startTime = System.currentTimeMillis();
                de.hsmainz.gi.indoornavcl.comm.types.Beacon testBeacon = new de.hsmainz.gi.indoornavcl.comm.types.Beacon(100, 0, 0, "00000000000000000000000000000000");
                de.hsmainz.gi.indoornavcl.comm.types.Beacon repBeacon = new de.hsmainz.gi.indoornavcl.comm.types.Beacon(111, 1, 1, "11111111111111111111111111111111");
                repBeacon.setId(0);
                de.hsmainz.gi.indoornavcl.comm.types.Beacon noIdBeacon = new de.hsmainz.gi.indoornavcl.comm.types.Beacon();
                noIdBeacon.setUuid("00000000000000000000000000000000");
                noIdBeacon.setMajor(0);
                noIdBeacon.setMinor(0);
                Site noIdSite = new Site();
                noIdSite.setName("TESTSITE");
                WkbLocation noIdLocation = new WkbLocation();
                Point coord2 = new com.vividsolutions.jts.geom.GeometryFactory()
                        .createPoint(
                                new com.vividsolutions.jts.geom.Coordinate(
                                        2, 2, 1
                                )
                        );
                noIdLocation.setBeacon(noIdBeacon);
                noIdLocation.setSite(noIdSite);
                noIdLocation.setCoord(new WkbPoint(coord2));



                boolean result = false;
                int i = 1;
                Log.d(TAG + ":ADMIN" , "trying admin interface");
                Log.d(TAG + ":TEST" , (i++) + " - addBeacon("+ StringUtils.toString(testBeacon)+")");
                result = SoapPositionerRequests.addBeacon(testBeacon);
                Log.d(TAG + ":ADMINTEST", result + " (false) \t= " + (result == false));

                Log.d(TAG + ":TEST" , (i++) + " - addBeacon("+StringUtils.toString(noIdBeacon)+")");
                result = SoapPositionerRequests.addBeacon(noIdBeacon);
                Log.d(TAG + ":ADMINTEST", result + " (true)  \t= "+ result);

                Log.d(TAG + ":TEST" , (i++) + " - addBeaconFromUuidMajorMinor("+repBeacon.getUuid()+", "+repBeacon.getMajor()+", "+repBeacon.getMinor()+")");
                result = SoapPositionerRequests.addBeaconFromUuidMajorMinor(repBeacon.getUuid(), repBeacon.getMajor(), repBeacon.getMinor());
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= " + result);

                Log.d(TAG + ":TEST" , (i++) + " - addSite("+noIdSite+")");
                result = SoapPositionerRequests.addSite(noIdSite);
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= " + result);

                Log.d(TAG + ":TEST" , (i++) + " - placeBeacon("+StringUtils.toString(noIdSite)+", "+StringUtils.toString(noIdBeacon)+", "+StringUtils.toString(coord2)+")");
                result = SoapPositionerRequests.placeBeacon(noIdSite, noIdBeacon, coord2);
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= "+ result);

                Log.d(TAG + ":TEST" , (i++) + " - replaceBeacon("+StringUtils.toString(noIdSite)+", "+StringUtils.toString(noIdBeacon)+", "+StringUtils.toString(repBeacon)+")");
                result = SoapPositionerRequests.replaceBeacon(noIdSite, noIdBeacon, repBeacon);
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= "+ result);

                Log.d(TAG + ":TEST" , (i++) + " - removeBeaconFromSite("+StringUtils.toString(repBeacon)+", "+StringUtils.toString(noIdSite)+")");
                result = SoapPositionerRequests.removeBeaconFromSite(repBeacon, noIdSite);
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= "+ result);

                Log.d(TAG + ":TEST" , (i++) + " - deleteBeacon("+StringUtils.toString(noIdBeacon)+")");
                result = SoapPositionerRequests.deleteBeacon(noIdBeacon);
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= "+ result);

                Log.d(TAG + ":TEST" , (i++) + " - deleteBeacon("+StringUtils.toString(repBeacon)+")");
                result = SoapPositionerRequests.deleteBeacon(repBeacon);
                Log.d(TAG + ":ADMINTEST", result + " (true) \t= "+ result);
                Log.d(TAG + ":ADMIN" , "DONE admin interface");
                return System.currentTimeMillis() - startTime;
            }

            // This is called when doInBackground() is finished
            protected void onPostExecute(Long result) {
                uiTellUser("Request took " + result + "ms");
            }
        };
        task.execute("");
    }


    /**
     * TODO proper user authentication
     * @return  whether you are a privileged user
     */
    public boolean isIsAdminUser() {
        return isAdminUser;
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.v(TAG, "callback to onBeaconServiceConnect arrived");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        verifyBluetooth();
        this.app = (BeaconScannerApplication) this.getApplication();
        loggedBeacons = new HashSet<>();
        app.getBeaconManager().getBeaconParsers().add(
            new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        );
        app.getBeaconManager().bind(this);
        region = new Region("myRangingUniqueId", null, null, null);
        app.getBeaconManager().setForegroundScanPeriod(beaconScanInterval);
        app.getBeaconManager().setForegroundBetweenScanPeriod(beaconScanWaitInterval);
        app.getBeaconManager().setBackgroundScanPeriod(beaconBackgroundScanInterval);
        app.getBeaconManager().setBackgroundBetweenScanPeriod(beaconBackgroundScanWaitInterval);
        app.getBeaconManager().setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<org.altbeacon.beacon.Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Iterator<org.altbeacon.beacon.Beacon> beaconIterator = beacons.iterator();
                    boolean calcPosition = false;
                    while (beaconIterator.hasNext()) {
                        org.altbeacon.beacon.Beacon beacon = beaconIterator.next();
                        Beacon ownBeacon = new Beacon(beacon);
                        Measurement measurement = new Measurement(beacon.getRssi(), beacon.getTxPower());
                        boolean shouldCalc = checkMeasuredBeacon(ownBeacon, measurement);
                        calcPosition = calcPosition || shouldCalc;
                    }
                    getLocationsFromLoggedBeacons();
                    if (calcPosition) {
                        calcPosition();
                    }
                }
            }
        });
        getAvailableSites();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            updateBeaconPositionMessenger = (Messenger) extras.get(Globals.UPDATE_POSITION_HANDLER);
        }
        Log.v(TAG, "BeaconScanService bound.");
        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "BeaconScanService unbound.");
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScanning();
        app.getBeaconManager().unbind(this);
        Log.v(TAG, "BeaconScanService destroyed.");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Service is restarted if it gets terminated. Intent data passed to the onStartCommand method is null.
        // Used for services which manages their own state and do not depend on the Intent data.
        return Service.START_STICKY;
    }

    public Site getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(Site site) {
        this.currentSite = site;
    }

    public Set<Site> getAllAvailableSites() {
        return availableSites;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    public void writeToFile(String str) {
        if (DEBUG)
            app.getFileHelper().createFile(str);
    }

    public boolean isScanning() {
        return isScanning;
    }

    public class LocalBinder extends Binder {
        BeaconScanService getService() {
            return BeaconScanService.this;
        }
    }

    private void uiUpdatePosition(WkbPoint point) {
        try {
            Message msg = Message.obtain();
            msg.what = Globals.UPDATE_POSITION_MSG;
            Bundle bundle = new Bundle();
            bundle.putParcelable(Globals.CURRENT_POSITION, point);
            msg.setData(bundle);
            updateBeaconPositionMessenger.send(msg);
        }
        catch (android.os.RemoteException e1) {
            Log.w(getClass().getName(), "Exception sending message", e1);
        }
    }


    private void uiSiteChanged() {
        try {
            Message msg = Message.obtain();
            msg.what = Globals.SITE_CHANGED_MSG;
            Bundle bundle = new Bundle();
            bundle.putParcelable(Globals.SITE_CHANGED, currentSite);
            msg.setData(bundle);
            updateBeaconPositionMessenger.send(msg);
        }
        catch (android.os.RemoteException e1) {
            Log.w(getClass().getName(), "Exception sending message", e1);
        }
    }

    private void uiTellUser(String str) {
        try {
            Message msg = Message.obtain();
            msg.what = Globals.DISPLAY_TOAST_MSG;
            Bundle bundle = new Bundle();
            bundle.putString(Globals.DISPLAY_TOAST, str);
            msg.setData(bundle);
            updateBeaconPositionMessenger.send(msg);
        }
        catch (android.os.RemoteException e1) {
            Log.w(getClass().getName(), "Exception sending message", e1);
        }
    }
}
