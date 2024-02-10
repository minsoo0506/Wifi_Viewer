package com.publicwifi;

public class LocationInfo {
    private int id;
    private double lat;
    private double lng;
    private String dttm;

    @Override
    public String toString() {
        return "LocationInfo{" +
                "location_ID=" + id +
                ", LAT=" + lat +
                ", LNG=" + lng +
                ", Search_DTTM='" + dttm + '\'' +
                '}';
    }

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
