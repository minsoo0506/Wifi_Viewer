package com.publicwifi;

// 위치 정보를 저장하기 위한 클래스
public class LocationInfo {
    private int id;
    private double lat;
    private double lng;
    private String dttm;

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public double getLAT() { return lat;}

    public void setLAT(double lat) {
        this.lat = lat;
    }

    public double getLNG() {
        return lng;
    }

    public void setLNG(double lng) {
        this.lng = lng;
    }

    public String getDTTM() {
        return this.dttm;
    }

    public void setDTTM(String dttm) {
        this.dttm = dttm;
    }
}
