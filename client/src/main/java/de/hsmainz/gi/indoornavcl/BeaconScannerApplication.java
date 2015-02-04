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
}