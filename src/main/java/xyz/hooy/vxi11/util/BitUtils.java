package xyz.hooy.vxi11.util;

public final class BitUtils {

    private BitUtils() {
    }

    public static boolean isBit(int byte4, int offset) {
        checkIntegerOffset(offset);
        return (byte4 & 1 << offset) != 0;
    }

    public static int setBit(int byte4, int offset, boolean enable) {
        checkIntegerOffset(offset);
        return enable ? 1 << offset | byte4 : ~(1 << offset) & byte4;
    }

    public static void checkByteOffset(int offset) {
        if (offset < 0 || offset > 7) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkIntegerOffset(int offset) {
        if (offset < 0 || offset > 31) {
            throw new IllegalArgumentException();
        }
    }
}
