package net.rehacktive.waspdb.internals.cryptolayer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class AESSerializer extends Serializer {

    private final Serializer serializer;
    private CipherManager cipherManager;

    public AESSerializer(Serializer serializer, CipherManager cm) {
        this.serializer = serializer;
        this.cipherManager = cm;
        //Security.addProvider(new BouncyCastleProvider());
    }

    public void write(Kryo kryo, Output output, Object object) {
        try {
            CipherOutputStream cipherStream = new CipherOutputStream(output, cipherManager.getCipher(Cipher.ENCRYPT_MODE));

            Output cipherOutput = new Output(cipherStream) {
                public void close() throws KryoException {
                    // Don't allow the CipherOutputStream to close the output.
                }
            };
            kryo.writeObject(cipherOutput, object, serializer);
            cipherOutput.flush();

            cipherStream.close();
        } catch (IOException ex) {
            throw new KryoException(ex);
        }

    }

    public Object read(Kryo kryo, Input input, Class type) {
        CipherInputStream cipherInput = new CipherInputStream(input, cipherManager.getCipher(Cipher.DECRYPT_MODE));
        return kryo.readObject(new Input(cipherInput), type, serializer);
    }


}