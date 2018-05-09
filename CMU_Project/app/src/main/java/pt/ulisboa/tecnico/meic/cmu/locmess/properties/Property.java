package pt.ulisboa.tecnico.meic.cmu.locmess.properties;

/**
 * Created by Akilino on 06/04/2017.
 */

public class Property {

    public String key;
    public String value;

    public Property(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText(){
        return key + ": " + value;
    }
}
