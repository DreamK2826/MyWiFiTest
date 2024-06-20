package com.dreamk.mywifitest;

public class SendDataV1 {

    String pointName; //采集点名称
    int pos1; //坐标1
    int pos2; //坐标2

    //以下变量用于储存该点每个ap的信号强度值
    int ap01;
    int ap02;
    int ap03;
    int ap04;
    int ap05;
    int ap06;
    int ap07;
    int ap08;
    int ap09;
    int ap10;
    int ap11;
    int ap12;
    int ap13;
    int ap14;

    public SendDataV1(String pointName, int pos1, int pos2, int ap01, int ap02, int ap03, int ap04, int ap05, int ap06, int ap07, int ap08, int ap09, int ap10, int ap11, int ap12, int ap13, int ap14) {
        this.pointName = pointName;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.ap01 = ap01;
        this.ap02 = ap02;
        this.ap03 = ap03;
        this.ap04 = ap04;
        this.ap05 = ap05;
        this.ap06 = ap06;
        this.ap07 = ap07;
        this.ap08 = ap08;
        this.ap09 = ap09;
        this.ap10 = ap10;
        this.ap11 = ap11;
        this.ap12 = ap12;
        this.ap13 = ap13;
        this.ap14 = ap14;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public int getPos1() {
        return pos1;
    }

    public void setPos1(int pos1) {
        this.pos1 = pos1;
    }

    public int getPos2() {
        return pos2;
    }

    public void setPos2(int pos2) {
        this.pos2 = pos2;
    }

    public int getAp01() {
        return ap01;
    }

    public void setAp01(int ap01) {
        this.ap01 = ap01;
    }

    public int getAp02() {
        return ap02;
    }

    public void setAp02(int ap02) {
        this.ap02 = ap02;
    }

    public int getAp03() {
        return ap03;
    }

    public void setAp03(int ap03) {
        this.ap03 = ap03;
    }

    public int getAp04() {
        return ap04;
    }

    public void setAp04(int ap04) {
        this.ap04 = ap04;
    }

    public int getAp05() {
        return ap05;
    }

    public void setAp05(int ap05) {
        this.ap05 = ap05;
    }

    public int getAp06() {
        return ap06;
    }

    public void setAp06(int ap06) {
        this.ap06 = ap06;
    }

    public int getAp07() {
        return ap07;
    }

    public void setAp07(int ap07) {
        this.ap07 = ap07;
    }

    public int getAp08() {
        return ap08;
    }

    public void setAp08(int ap08) {
        this.ap08 = ap08;
    }

    public int getAp09() {
        return ap09;
    }

    public void setAp09(int ap09) {
        this.ap09 = ap09;
    }

    public int getAp10() {
        return ap10;
    }

    public void setAp10(int ap10) {
        this.ap10 = ap10;
    }

    public int getAp11() {
        return ap11;
    }

    public void setAp11(int ap11) {
        this.ap11 = ap11;
    }

    public int getAp12() {
        return ap12;
    }

    public void setAp12(int ap12) {
        this.ap12 = ap12;
    }

    public int getAp13() {
        return ap13;
    }

    public void setAp13(int ap13) {
        this.ap13 = ap13;
    }

    public int getAp14() {
        return ap14;
    }

    public void setAp14(int ap14) {
        this.ap14 = ap14;
    }
}
