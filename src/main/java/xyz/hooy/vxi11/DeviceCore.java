package xyz.hooy.vxi11;

public final class DeviceCore {

    public static final int PROGRAM = 0x0607AF;
    public static final int VERSION = 1;

    private DeviceCore() {
    }

    public final static class Options {

        private Options() {
        }

        public static final int CREATE_LINK = 10;
        public static final int DEVICE_WRITE = 11;
        public static final int DEVICE_READ = 12;
        public static final int DEVICE_READ_STB = 13;
        public static final int DEVICE_TRIGGER = 14;
        public static final int DEVICE_CLEAR = 15;
        public static final int DEVICE_REMOTE = 16;
        public static final int DEVICE_LOCAL = 17;
        public static final int DEVICE_LOCK = 18;
        public static final int DEVICE_UNLOCK = 19;
        public static final int DEVICE_ENABLE_SRQ = 20;
        public static final int DEVICE_DO_CMD = 22;
        public static final int DESTROY_LINK = 23;
        public static final int CREATE_INTERRUPT_CHANNEL = 25;
        public static final int DESTROY_INTERRUPT_CHANNEL = 26;
    }
}
