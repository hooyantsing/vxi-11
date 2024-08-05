package xyz.hooy.vxi11.entity;

public final class Channels {

    private Channels() {
    }

    public final static class Core {
        public static final int PROGRAM = 395183;
        public static final int VERSION = 1;

        private Core() {
        }

        public final static class Options {
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

            private Options() {
            }
        }
    }

    public final static class Abort {
        public static final int PROGRAM = 395184;
        public static final int VERSION = 1;

        private Abort() {
        }

        public final static class Options {
            public static final int DEVICE_ABORT = 1;

            private Options() {
            }
        }
    }

    public final static class Interrupt {
        public static final int PROGRAM = 395185;
        public static final int VERSION = 1;

        private Interrupt() {
        }
    }
}
