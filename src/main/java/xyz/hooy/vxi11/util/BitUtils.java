package xyz.hooy.vxi11.util;

public final class BitUtils {

    private BitUtils() {
    }

    public static boolean isBit(byte byte1, int position) {
        checkByteOffset(position);
        return (byte1 & 1 << position) != 0;
    }

    public static boolean isBit(int byte4, int position) {
        checkIntegerOffset(position);
        return (byte4 & 1 << position) != 0;
    }

    public static int setBit(int byte4, int position, boolean enable) {
        checkIntegerOffset(position);
        return enable ? 1 << position | byte4 : ~(1 << position) & byte4;
    }

    public static void checkByteOffset(int position) {
        if (position < 0 || position > 7) {
            throw new IllegalArgumentException("Bit position out of range (0-7).");
        }
    }

    public static void checkIntegerOffset(int position) {
        if (position < 0 || position > 31) {
            throw new IllegalArgumentException("Bit position out of range (0-31).");
        }
    }
}
