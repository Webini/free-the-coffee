package fr.webini.nico.freethecoffee;

/**
 * Created by nico on 13/01/18.
 */

public class Tools {
    static String toString(byte[] buffer) {
        String out = "";
        for (Integer i = 0; i < buffer.length; i++) {
            out += String.format("%02x", buffer[i]);
        }
        return out;
    }
}
