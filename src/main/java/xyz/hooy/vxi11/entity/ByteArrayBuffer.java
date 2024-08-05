package xyz.hooy.vxi11.entity;

import java.util.ArrayList;
import java.util.List;

public class ByteArrayBuffer {

    private final List<Byte> buffer = new ArrayList<>();

    public ByteArrayBuffer() {
    }

    public ByteArrayBuffer(byte[] bytes) {
        append(bytes);
    }

    public void append(byte... bytes) {
        for (byte aByte : bytes) {
            buffer.add(aByte);
        }
    }

    public void clear() {
        buffer.clear();
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[buffer.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get(i);
        }
        return bytes;
    }
}
