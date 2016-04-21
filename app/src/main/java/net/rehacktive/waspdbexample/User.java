package net.rehacktive.waspdbexample;

/**
 * Created by stefano on 28/07/2015.
 */
public class User {

    private String id;
    private String user_name;
    private String email;

    public User() {
    }

    public User(String username, String email) {
        this.user_name = username;
        this.email = email;
        this.id = ""+System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
