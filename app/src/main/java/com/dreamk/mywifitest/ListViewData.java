package com.dreamk.mywifitest;

public class ListViewData {

    private String SSID;
    private int RSSI;

    public ListViewData(String SSID, int RSSI) {
        this.SSID = SSID;
        this.RSSI = RSSI;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }
}

