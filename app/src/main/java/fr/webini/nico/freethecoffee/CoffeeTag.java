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
        int sector = tag.sectorToBlock(2);
        byte[] firstMoneyBlock = tag.readBlock(sector);
        byte[] secMoneyBlock = tag.readBlock(sector + 1);
        byte[] unkThirdBlock = tag.readBlock(sector + 2);
        Log.d(CoffeeTag.class.getSimpleName(),
                Tools.toString(firstMoneyBlock) + " // " +
                Tools.toString(secMoneyBlock) + " // " +
                Tools.toString(unkThirdBlock));
        byte[] bAmount = { (byte)((amount >> 8) & 0xFF), (byte)(amount & 0xFF) };
        firstMoneyBlock[0] = secMoneyBlock[0] = bAmount[0];
        firstMoneyBlock[1] = secMoneyBlock[1] = bAmount[1];
        firstMoneyBlock[2] = secMoneyBlock[2] = bAmount[0];
        firstMoneyBlock[3] = secMoneyBlock[3] = bAmount[1];
        firstMoneyBlock[15] = (byte)0x38;
        secMoneyBlock[15] = (byte)0x37;
        unkThirdBlock[0] = (byte)0xAA;
        unkThirdBlock[1] = (byte)0x38;

        tag.writeBlock(sector, firstMoneyBlock);
        tag.writeBlock(sector + 1, secMoneyBlock);
        tag.writeBlock(sector + 2, unkThirdBlock);
    }

    public void close() throws IOException {
        tag.close();
    }
}
