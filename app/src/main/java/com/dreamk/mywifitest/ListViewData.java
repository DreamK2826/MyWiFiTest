package com.dreamk.mywifitest;

public class ListViewData {

    private String SSID;
    private int RSSI;
    private String mac;

    public ListViewData(String SSID, int RSSI,String mac) {
        this.SSID = SSID;
        this.RSSI = RSSI;
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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

