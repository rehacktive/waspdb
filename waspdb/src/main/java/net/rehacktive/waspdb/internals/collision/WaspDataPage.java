package net.rehacktive.waspdb.internals.collision;

/**
 * Created by stefano on 14/06/2016.
 */

public class WaspDataPage {

    private byte[] iv;
    private byte[] data;

    public WaspDataPage() {
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
