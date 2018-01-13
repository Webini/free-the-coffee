package fr.webini.nico.freethecoffee;

import android.nfc.tech.MifareClassic;
import android.util.Log;

import java.io.IOException;

import fr.webini.nico.freethecoffee.exceptions.UnrecognizedTagException;

/**
 * Created by nico on 13/01/18.
 */

public class CoffeeTag {
    public MifareClassic tag;

    CoffeeTag(MifareClassic mTag) {
        tag = mTag;
    }

    public static CoffeeTag get(MifareClassic mTag) throws IOException, UnrecognizedTagException {
        CoffeeTag ct = new CoffeeTag(mTag);
        mTag.connect();
        byte[] uid = mTag.getTag().getId();
        KeyGenerator kGen = new KeyGenerator(uid);


        Log.d(CoffeeTag.class.getSimpleName(),
            " -k " + Tools.toString(kGen.getKeyA(0)) +
            " -k " + Tools.toString(kGen.getKeyA(1)) +
            " -k " + Tools.toString(kGen.getKeyA(2)) +
            " -k " + Tools.toString(kGen.getKeyA(3)) +
            " -k " + Tools.toString(kGen.getKeyA(4)) +
            " -k " + Tools.toString(kGen.getKeyA(5)) +
            " -k " + Tools.toString(kGen.getKeyB(0)) +
            " -k " + Tools.toString(kGen.getKeyB(1)) +
            " -k " + Tools.toString(kGen.getKeyB(2)) +
            " -k " + Tools.toString(kGen.getKeyB(3)) +
            " -k " + Tools.toString(kGen.getKeyB(4)) +
            " -k " + Tools.toString(kGen.getKeyB(5))

        );


        if (
                //!mTag.authenticateSectorWithKeyA(0, kGen.getKeyA(0)) ||
                //!mTag.authenticateSectorWithKeyA(1, kGen.getKeyA(1)) ||
                //!mTag.authenticateSectorWithKeyA(2, kGen.getKeyA(2)) ||
                //!mTag.authenticateSectorWithKeyA(3, kGen.getKeyA(3)) ||
                //!mTag.authenticateSectorWithKeyA(4, kGen.getKeyA(4)) ||
                //!mTag.authenticateSectorWithKeyA(5, kGen.getKeyA(5)) ||
                //!mTag.authenticateSectorWithKeyB(0, kGen.getKeyB(0)) ||
                //!mTag.authenticateSectorWithKeyB(1, kGen.getKeyB(1)) ||
                !mTag.authenticateSectorWithKeyB(2, kGen.getKeyB(2))
                //!mTag.authenticateSectorWithKeyB(4, kGen.getKeyB(4)) ||
                //!mTag.authenticateSectorWithKeyB(5, kGen.getKeyB(5))
        ) {
            throw new UnrecognizedTagException();
        }

        return ct;
    }

    public int getAmount() throws IOException {
        byte[] moneyBlock = tag.readBlock(tag.sectorToBlock(2));
        return ((int)moneyBlock[2] & 0xFF) << 8 | ((int)moneyBlock[3] & 0xFF);
    }

    public void setAmount(Integer amount) throws IOException {
        byte[] moneyBlock = tag.readBlock(tag.sectorToBlock(2));
        Log.d(CoffeeTag.class.getSimpleName(), Tools.toString(moneyBlock));
        byte[] bAmount = { (byte)((amount >> 8) & 0xFF), (byte)(amount & 0xFF) };
        moneyBlock[0] = bAmount[0];
        moneyBlock[1] = bAmount[1];
        moneyBlock[2] = bAmount[0];
        moneyBlock[3] = bAmount[1];
        Log.d(CoffeeTag.class.getSimpleName(), Tools.toString(moneyBlock));
        tag.writeBlock(tag.sectorToBlock(2), moneyBlock);
    }

    public void close() throws IOException {
        tag.close();
    }
}
