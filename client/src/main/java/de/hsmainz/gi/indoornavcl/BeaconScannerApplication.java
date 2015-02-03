package de.hsmainz.gi.indoornavcl;


import android.app.Application;
import de.hsmainz.gi.indoornavcl.comm.Configuration;
import de.hsmainz.gi.indoornavcl.comm.types.Beacon;
import de.hsmainz.gi.indoornavcl.util.FileHelper;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jodwyer
 * @author KekS (mailto:keks@keksfabrik.eu), 29.10.2014
 */
public class BeaconScannerApplication extends Application {

    /** global reference to {@link de.hsmainz.gi.indoornavcl.util.FileHelper} instance. */
    private FileHelper              fileHelper;
    /** just has to exist to save power when in background mode. */
    @SuppressWarnings("unused")
    private BackgroundPowerSaver    backgroundPowerSaver;
    /** global reference to {@link org.altbeacon.beacon.BeaconManager} */
    private BeaconManager           beaconManager;
    /** a set of {@link de.hsmainz.gi.indoornavcl.comm.types.Beacon}s not yet known to the DB */
    private static Set<Beacon>      unregisteredBeacons = new HashSet<>();

    /** whether or not the user is an administrator */
    private static boolean          isAdminUser = true;


    @Override
    public void onCreate() {
        super.onCreate();
        fileHelper = new FileHelper(getExternalFilesDir(null));
        // Allow scanning to continue in the background.
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        new Configuration(getResources().getXml(R.xml.config));
    }

    /**
     * @return  the {@link de.hsmainz.gi.indoornavcl.util.FileHelper}
     */
    public FileHelper getFileHelper() {
        return this.fileHelper;
    }

    /**
     * @return  the {@link org.altbeacon.beacon.BeaconManager}
     */
    public BeaconManager getBeaconManager() {
        return beaconManager;
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
}