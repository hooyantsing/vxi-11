package xyz.hooy.vxi11.rpc;

import xyz.hooy.vxi11.rpc.idl.Device_Flags;
import xyz.hooy.vxi11.util.BitUtils;

public class DeviceFlags {

    public final static int TERMINATION_CHARACTER_OFFSET = 7;

    public final static int END_OFFSET = 3;

    public final static int WAIT_LOCK_OFFSET = 0;

    private int flagBits = 0;

    public DeviceFlags() {
    }

    public boolean isTerminationCharacter() {
        return BitUtils.isBit(flagBits, TERMINATION_CHARACTER_OFFSET);
    }

    public boolean isEnd() {
        return BitUtils.isBit(flagBits, END_OFFSET);
    }

    public boolean isWaitLock() {
        return BitUtils.isBit(flagBits, WAIT_LOCK_OFFSET);
    }

    public DeviceFlags enableTerminationCharacter(boolean enable) {
        this.flagBits = BitUtils.setBit(flagBits, TERMINATION_CHARACTER_OFFSET, enable);
        return this;
    }

    public DeviceFlags enableEnd(boolean enable) {
        this.flagBits = BitUtils.setBit(flagBits, END_OFFSET, enable);
        return this;
    }

    public DeviceFlags enableWaitLock(boolean enable) {
        this.flagBits = BitUtils.setBit(flagBits, WAIT_LOCK_OFFSET, enable);
        return this;
    }

    public Device_Flags buildDeviceFlags() {
        return new Device_Flags(flagBits);
    }
}