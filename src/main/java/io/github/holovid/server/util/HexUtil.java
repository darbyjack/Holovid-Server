package io.github.holovid.server.util;

// Yoinked from Apache's HexBin class
public final class HexUtil {

    private static final int LOOKUPLENGTH = 16;
    private static final char[] lookUpHexAlphabet = new char[LOOKUPLENGTH];

    static {
        for (int i = 0; i < 10; i++) {
            lookUpHexAlphabet[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            lookUpHexAlphabet[i] = (char) ('A' + i - 10);
        }
    }

    public static String encode(final byte[] binaryData) {
        if (binaryData == null) return null;

        final int lengthData = binaryData.length;
        final int lengthEncode = lengthData * 2;
        final char[] encodedData = new char[lengthEncode];
        int temp;
        for (int i = 0; i < lengthData; i++) {
            temp = binaryData[i];
            if (temp < 0) {
                temp += 256;
            }
            encodedData[i * 2] = lookUpHexAlphabet[temp >> 4];
            encodedData[i * 2 + 1] = lookUpHexAlphabet[temp & 0xf];
        }
        return new String(encodedData);
    }
}
