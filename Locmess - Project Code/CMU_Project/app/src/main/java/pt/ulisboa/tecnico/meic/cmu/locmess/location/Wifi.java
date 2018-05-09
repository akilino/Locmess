package pt.ulisboa.tecnico.meic.cmu.locmess.location;

import pt.ulisboa.tecnico.meic.cmu.locmess.location.Location;

public class Wifi extends Location {

    public String SSID;

    public Wifi(String locationName,String SSID){
        super(locationName);
        this.SSID = SSID;
    }

}