package pt.ulisboa.tecnico.meic.cmu.locmess.user;

import pt.ulisboa.tecnico.meic.cmu.locmess.post.Post;

/**
 * Created by Akilino on 16/03/2017.
 */

public class User {

    public String username;
    public byte[] password;
    public Post post;

    public User(String username, byte[] password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
