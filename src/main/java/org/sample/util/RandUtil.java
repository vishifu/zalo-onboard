package org.sample.util;

import java.util.Random;

public class RandUtil {

    private static final Random rand = new Random(System.nanoTime());

    public static int randInt() {
        return rand.nextInt();
    }

    public static byte[] randBytes(int len) {
        byte[] data = new byte[len];
        rand.nextBytes(data);

        return data;
    }
}
