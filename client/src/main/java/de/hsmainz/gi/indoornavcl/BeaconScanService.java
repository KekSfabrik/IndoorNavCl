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
import de.hsmainz.gi.indoornavcl.comm.SoapPositionerRequests;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.types.WkbLocation;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
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
    private static final int            beaconScanInterval              =  5000;
    private static final int            beaconScanWaitInterval          =     0;
    private static final int            beaconBackgroundScanInterval    = 10000;
    private static final int            beaconBackgroundScanWaitInterval= 20000;

    private Set<de.hsmainz.gi.indoornavcl.comm.types.Beacon>
                                        loggedBeacons       = Collections.synchronizedSet(new HashSet<de.hsmainz.gi.indoornavcl.comm.types.Beacon>()),
                                        checkedBeacons      = Collections.synchronizedSet(new HashSet<de.hsmainz.gi.indoornavcl.comm.types.Beacon>()),
                                        unregisteredBeacons = new HashSet<>();
    private Site                        currentSite;
    private Region                      region;
    private BeaconScannerApplication    app;
    private boolean                     isScanning;
    private final IBinder               binder = new LocalBinder();
    private Messenger                   updateBeaconPositionMessenger;

    /** whether or not the user is an administrator */
    private static boolean              isAdminUser = true;

    private Handler                     handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            switch (msg.what) {
                                case 0:
                                    synchronized (loggedBeacons) {
                //                        if (checkedBeacons != null) {
                //                            for (de.hsmainz.gi.indoornavcl.comm.types.Beacon b : checkedBeacons) {
                //                                Log.v(TAG, "Checked Beacon(" + StringUtils.toString(b) + ")");
                //                                if (!loggedBeacons.contains(b)) {
                //                                    app.enqueueUnregisteredBeacon(b);
                //                                }
                //                            }
                //                            loggedBeacons.clear();
                //                            Set<WkbLocation> wkbLocations = SoapLocatorRequests.getBeaconLocationsFromBeaconList(checkedBeacons);
                //                            for (WkbLocation loc : wkbLocations) {
                //                                Log.v(TAG, "(getBeaconLocationsFromBeaconList) Locations(" + StringUtils.toString(loc) + ")");
                //                            }
                //                        }
                                    }
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
            tryAdminInterface();
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

    private void logBeacons() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                synchronized (loggedBeacons) {
                /*for (de.hsmainz.gi.indoornavcl.comm.types.Beacon b: loggedBeacons) {
                    Log.v(TAG, "Logged Beacon("+b.getId()+"): "+b.getUuid() + ", " + b.getMajor() + ", " + b.getMinor());
                }*/
//                checkedBeacons.clear();
                  /* getBeacon(beacon) */
//                checkedBeacons.add(
//                    SoapLocatorRequests.getBeacons(
//                        loggedBeacons != null
//                        ? loggedBeacons
//                        : Arrays.asList(
//                            new de.hsmainz.gi.indoornavcl.comm.types.Beacon[] {
//                                new de.hsmainz.gi.indoornavcl.comm.types.Beacon(0, 100, 12, "00000000000000000000000000000000")
//                            }
//                        )
//                    )
//                );
                  /* getBeacons(beacons) */
//                checkedBeacons = SoapLocatorRequests.getBeacons(loggedBeacons);

//                    Set<Site> siteCandidates = SoapLocatorRequests.getSiteByApproximateName("Lucy");
//                    Site keksfabrik = new Site(1, "KekSfabrik - Lotharstrasse 1,  55116 Mainz");
//                    if (siteCandidates.contains(keksfabrik)) {
//                        currentSite = keksfabrik;
//                    }
//                    Log.d(TAG, "(getSiteByApproximateName) current site = " + currentSite);
//                    currentSite = null;
//                    Log.d(TAG, "(getBeaconLocationsFromSite) All locations at keksfabrik: ");
//                    for (WkbLocation loc: SoapLocatorRequests.getBeaconLocationsFromSite(keksfabrik)) {
//                        Log.d(TAG, StringUtils.toString(loc));
//                    }
//                    /* getBeaconFromUuidMajorMinor */
//                    for (de.hsmainz.gi.indoornavcl.comm.types.Beacon b : loggedBeacons) {
//                        de.hsmainz.gi.indoornavcl.comm.types.Beacon bcn = SoapLocatorRequests
//                                .getBeaconFromUuidMajorMinor(
//                                        b.getUuid(), b.getMajor(), b.getMinor()
//                                );
//                        if (bcn != null) {
//                            checkedBeacons.add(bcn);
//                            if (currentSite == null) {
//                                siteCandidates = SoapLocatorRequests.getSitesFromBeacon(bcn);
//                                for (Site site: siteCandidates) {
//                                    Log.d(TAG, "(getSitesFromBeacon) Site is possibly " + StringUtils.toString(site));
//                                }
//                            }
//                            if (currentSite != null) {
//                                Log.d(TAG, "trying to find location for " + StringUtils.toString(currentSite) + " & " + StringUtils.toString(bcn));
//                                Point point = SoapLocatorRequests.getCoordinate(currentSite, bcn);
//                                Log.d(TAG, "Point: "+point.toString());
//                            }
//                        }
//                    }
//                    for (Site site: SoapLocatorRequests.getSitesFromBeaconList(loggedBeacons)) {
//                        Log.d(TAG, "SitesFromBeaconList" + StringUtils.toString(site));
//                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

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
                tellUser("Request took " + result + "ms");
            }
        };
        task.execute("");
    }



    /**
     *
     * @param   beacon  a unregistered {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon}
     * @return  whether or not successfull
     */
    public boolean enqueueUnregisteredBeacon(Beacon beacon) {
        return unregisteredBeacons.add(beacon);
    }

    /**
     *
     * @param   beacon  the {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon} to unregister
     * @return  whether or not successfull
     */
    public boolean dequeueUnregisteredBeacon(Beacon beacon) {
        return unregisteredBeacons.remove(beacon);
    }

    /**
     *
     * @return  the set of unregistered {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon}s
     */
    public Set<Beacon> getUnregisteredBeacons() {
        return unregisteredBeacons;
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
        app.getBeaconManager().getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
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
                    while (beaconIterator.hasNext()) {
                        org.altbeacon.beacon.Beacon beacon = beaconIterator.next();
                        loggedBeacons.add(new de.hsmainz.gi.indoornavcl.comm.types.Beacon(beacon));
                    }
                    logBeacons();
                }
            }
        });
    }


    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            updateBeaconPositionMessenger = (Messenger) extras.get(Globals.UPDATE_POSITION_HANDLER);
            Message msg = Message.obtain();
        }
        Log.v(TAG, "BeaconScanService bound.");
        return binder;
    }

    /**
     * Called when all clients have disconnected from a particular interface
     * published by the service.  The default implementation does nothing and
     * returns false.
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return true if you would like to have the service's
     * {@link #onRebind} method later called when new clients bind to it.
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "BeaconScanService unbound.");
        return super.onUnbind(intent);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "BeaconScanService destroyed.");
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link android.content.Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link android.os.AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link android.content.Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        //Service is restarted if it gets terminated. Intent data passed to the onStartCommand method is null.
        // Used for services which manages their own state and do not depend on the Intent data.
        return Service.START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    public boolean isScanning() {
        return isScanning;
    }

    public class LocalBinder extends Binder {
        BeaconScanService getService() {
            return BeaconScanService.this;
        }
    }

    private void updatePosition(WkbPoint point) {
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

    private void tellUser(String str) {
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
