package net.rehacktive.waspdb.internals.utils;

/**
 * Created by stefano on 20/07/2015.
 */
public class Salt {

    private byte[] salt;

    public Salt() {
    }

    public Salt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getSalt() {
        return salt;
    }
}
