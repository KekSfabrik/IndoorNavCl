package de.hsmainz.gi.indoornavcl.util;


/**
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 2015
 */
public class Measurement {
    private int rssi;
    private int txPower;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }
}
