package de.hsmainz.gi.indoornavcl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.types.Site;
import de.hsmainz.gi.indoornavcl.comm.SoapPositionerRequests;
import de.hsmainz.gi.indoornavcl.comm.types.*;
import de.hsmainz.gi.indoornavcl.util.StringUtils;
import org.altbeacon.beacon.*;

import java.util.*;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class MainActivity
        extends     Activity
        implements  BeaconConsumer {

    private static final String         TAG = MainActivity.class.getSimpleName();
    private Set<de.hsmainz.gi.indoornavcl.comm.types.Beacon>  loggedBeacons = Collections.synchronizedSet(new HashSet<de.hsmainz.gi.indoornavcl.comm.types.Beacon>());
    private Set<de.hsmainz.gi.indoornavcl.comm.types.Beacon>  checkedBeacons = Collections.synchronizedSet(new HashSet<de.hsmainz.gi.indoornavcl.comm.types.Beacon>());
    private Site                        currentSite;
    private Button                      buttonStart;
    private Region                      region;
    private BeaconScannerApplication    app;
    private boolean                     isScanning;
    private static final int            beaconScanInterval              =  5000;
    private static final int            beaconScanWaitInterval          =     0;
    private static final int            beaconBackgroundScanInterval    = 10000;
    private static final int            beaconBackgroundScanWaitInterval= 20000;

    public Handler handler = new Handler(new Handler.Callback() {
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
    private void verifyBluetooth() {
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
                Point                                       coord2 = new com.vividsolutions.jts.geom.GeometryFactory()
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
                showNotification("Request took " + result + "ms");
            }
        };//.doInBackground(null);
        task.execute("");
    }

    /**
     * App startup
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "started");
        verifyBluetooth();
        this.app = (BeaconScannerApplication) this.getApplication();
        loggedBeacons = new HashSet<>();
        buttonStart = (Button) findViewById(R.id.btnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "START logging");
                // start logging for R.id.inIntervalLength seconds
                if (!isScanning) {
                    startScanning();
                    Toast.makeText(buttonStart.getContext(), "Started", Toast.LENGTH_SHORT).show();
                    buttonStart.setText(R.string.stop);
                } else {
                    stopScanning();
                    Toast.makeText(buttonStart.getContext(), "Stopped", Toast.LENGTH_SHORT).show();
                    buttonStart.setText(R.string.start);
                }
            }
        });
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

    public void showNotification(String str) {
        Toast.makeText(buttonStart.getContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * app shutdown
     */
    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (isScanning)
            stopScanning();
        stopScanning();
        app.getBeaconManager().unbind(this);
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

    @Override
    public void onBeaconServiceConnect() {
        Log.v(TAG, "callback to onBeaconServiceConnect arrived");
    }

    public void showProblem(String problem) {
        Toast.makeText(buttonStart.getContext(), problem, Toast.LENGTH_SHORT).show();
    }
}
