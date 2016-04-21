package net.rehacktive.waspdb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano on 17/03/2015.
 */
public class UserWithNestedContent {

    public static int NUMBER_OF_FRIENDS = 20;

    private int id;
    private String username;
    private String telephone;
    private List<User> friends;

    public UserWithNestedContent() {
    }

    public UserWithNestedContent(int id, String username, String telephone) {
        this.id = id;
        this.username = username;
        this.telephone = telephone;
        // generate list of friends
        this.friends = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_FRIENDS; i++) {
            User p = new User(i, "test"+i, "123");
            friends.add(p);
        }
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWithNestedContent person = (UserWithNestedContent) o;

        if (id != person.id) return false;
        if (telephone != null ? !telephone.equals(person.telephone) : person.telephone != null)
            return false;
        if (username != null ? !username.equals(person.username) : person.username != null)
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

    @Override
    public String toString() {
        return "UserWithNestedContent{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
