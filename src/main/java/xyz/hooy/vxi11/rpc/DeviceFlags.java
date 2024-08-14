package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import xyz.hooy.vxi11.util.BitUtils;

import java.io.IOException;

public class DeviceFlags implements XdrAble {

    public final static int TERMINATION_CHARACTER_OFFSET = 7;

    public final static int END_OFFSET = 3;

    public final static int WAIT_LOCK_OFFSET = 0;

    private int flagBits = 0;

    public DeviceFlags() {
    }

    protected DeviceFlags(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeInt(flagBits);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        flagBits = xdr.xdrDecodeInt();
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
}