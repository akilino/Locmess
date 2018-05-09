package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import pt.ulisboa.tecnico.meic.cmu.locmess.location.Location;

public class GPS extends Location {

    public String lon, lat, radius;

    public GPS(String locationName, String lon, String lat, String radius){
        super(locationName);
        this.lon = lon;
        this.lat = lat;
        this.radius = radius;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getLat(){
        return lat;
    }
    public String getLon(){
        return lon;
    }

}
