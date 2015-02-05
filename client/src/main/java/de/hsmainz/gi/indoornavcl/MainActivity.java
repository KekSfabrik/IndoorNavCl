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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.vividsolutions.jts.geom.Point;
import de.hsmainz.gi.indoornavcl.comm.types.WkbPoint;
import de.hsmainz.gi.indoornavcl.util.Globals;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class MainActivity
        extends     Activity {

    private static final String         TAG = MainActivity.class.getSimpleName();
    private Button                      buttonStart;
    private BeaconScanService           bs;
    private boolean                     isBound = false;

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
                                case Globals.UPDATE_POSITION_MSG:
                                    Bundle b = msg.getData();
                                    WkbPoint wkbPoint = b.getParcelable(Globals.CURRENT_POSITION);

                                    Point point = wkbPoint.getPoint();
                                    showNotification("Found 'POINT(" + point.getCoordinate().x + " " + point.getCoordinate().y + " " + point.getCoordinate().z + ")'");

                                    TextView xPos = (TextView) findViewById(R.id.txtX);
                                    xPos.setText(point.getCoordinate().x + "");
                                    TextView yPos = (TextView) findViewById(R.id.txtY);
                                    yPos.setText(point.getCoordinate().y + "");
                                    TextView zPos = (TextView) findViewById(R.id.txtZ);
                                    zPos.setText(point.getCoordinate().z + "");
                                    // TODO display the new point on the UI
                                    break;
                                case Globals.DISPLAY_TOAST_MSG:
                                    showNotification(msg.getData().getString(Globals.DISPLAY_TOAST));
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
        Intent intent = new Intent(this, BeaconScanService.class);
        intent.putExtra(Globals.UPDATE_POSITION_HANDLER, new Messenger(this.handler));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        buttonStart = (Button) findViewById(R.id.btnStart);
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
}
