package net.rehacktive.waspdb;

/**
 * Created by stefano on 17/03/2015.
 */
public class User {

    private int id;
    private String username;
    private String telephone;

    public User() {
    }

    public User(int id, String username, String telephone) {
        this.id = id;
        this.username = username;
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (telephone != null ? !telephone.equals(user.telephone) : user.telephone != null)
            return false;
        if (username != null ? !username.equals(user.username) : user.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        return result;
    }
}
