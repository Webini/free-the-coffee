package fr.webini.nico.freethecoffee;

import android.util.Log;

/**
 * Created by nico on 13/01/18.
 */

public class KeyGenerator {
    private byte[] uid;

    private static final byte[][] A_KEYS = {
        { (byte)0xa0, (byte)0xa1, (byte)0xa2, (byte)0xa3, (byte)0xa4, (byte)0xa5 },
        { (byte)0x09, (byte)0x12, (byte)0x5a, (byte)0x25, (byte)0x89, (byte)0xe5 },
        { (byte)0xab, (byte)0x75, (byte)0xc9, (byte)0x37, (byte)0x92, (byte)0x2f },
        { (byte)0xe2, (byte)0x72, (byte)0x41, (byte)0xaf, (byte)0x2c, (byte)0x09 },
        { (byte)0x31, (byte)0x7a, (byte)0xb7, (byte)0x2f, (byte)0x44, (byte)0x90 },
        { (byte)0x5c, (byte)0x8f, (byte)0xf9, (byte)0x99, (byte)0x0d, (byte)0xa2 },
    };

    private static final byte[][] B_KEYS = {
        { (byte)0xb4, (byte)0xc1, (byte)0x32, (byte)0x43, (byte)0x9e, (byte)0xef },
        { (byte)0xf1, (byte)0x2c, (byte)0x84, (byte)0x53, (byte)0xd8, (byte)0x21 },
        { (byte)0x73, (byte)0xe7, (byte)0x99, (byte)0xfe, (byte)0x32, (byte)0x41 },
        { (byte)0xaa, (byte)0x4d, (byte)0x13, (byte)0x76, (byte)0x56, (byte)0xae },
        { (byte)0xb0, (byte)0x13, (byte)0x27, (byte)0x27, (byte)0x2d, (byte)0xfd },
        { (byte)0xd0, (byte)0x1a, (byte)0xfe, (byte)0xeb, (byte)0x89, (byte)0x0a }
    };

    KeyGenerator(byte[] mUid) {
        uid = mUid;
    }

    public byte[] getKeyA(int sector) {
        if (sector == 0) {
            return KeyGenerator.A_KEYS[0];
        } else if(sector >= 5) {
            return KeyGenerator.A_KEYS[5];
        }

        return new byte[] {
            (byte)(KeyGenerator.A_KEYS[sector][0] ^ uid[0]),
            (byte)(KeyGenerator.A_KEYS[sector][1] ^ uid[1]),
            (byte)(KeyGenerator.A_KEYS[sector][2] ^ uid[2]),
            (byte)(KeyGenerator.A_KEYS[sector][3] ^ uid[3]),
            (byte)(KeyGenerator.A_KEYS[sector][4] ^ uid[0]),
            (byte)(KeyGenerator.A_KEYS[sector][5] ^ uid[1]),
        };
    }

    public byte[] getKeyB(int sector) {
        if (sector == 0) {
            return KeyGenerator.B_KEYS[0];
        } else if (sector >= 5) {
            return KeyGenerator.B_KEYS[5];
        }

        return new byte[] {
            (byte)(KeyGenerator.B_KEYS[sector][0] ^ uid[2]),
            (byte)(KeyGenerator.B_KEYS[sector][1] ^ uid[3]),
            (byte)(KeyGenerator.B_KEYS[sector][2] ^ uid[0]),
            (byte)(KeyGenerator.B_KEYS[sector][3] ^ uid[1]),
            (byte)(KeyGenerator.B_KEYS[sector][4] ^ uid[2]),
            (byte)(KeyGenerator.B_KEYS[sector][5] ^ uid[3]),
        };
    }

}
