package pt.ulisboa.tecnico.meic.cmu.locmess.location;

/**
 * Created by Akilino on 04/04/2017.
 */

public abstract class Location{

    public String locationName;

    public Location(String locationName){
        this.locationName = locationName;
    }

    public String getName(){
        return locationName;
    }

}