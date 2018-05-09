package pt.ulisboa.tecnico.meic.cmu.locmess.post;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created by Akilino on 16/03/2017.
 */

public class Post implements Serializable{

    public String title;
    public String location;
    public String user;
    public String message;
    public String mode;

    public Post(String title, String message, String location, String user, String mode) throws UnsupportedEncodingException {
        this.title=title;
        this.message = message;
        this.location = location;
        this.user = user;
        this.mode = mode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
