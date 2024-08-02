package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;

import java.io.IOException;

public class DeviceFlags implements XdrAble {

    public final static int TERMINATION_CHARACTER_OFFSET = 7;

    public final static int END_OFFSET = 3;

    public final static int WAIT_LOCK_OFFSET = 0;

    private int flagBits = 0;

    public DeviceFlags(boolean terminationCharacter, boolean end, boolean waitLock) {
        enableTerminationCharacter(terminationCharacter);
        enableEnd(end);
        enableWaitLock(waitLock);
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
        return flagBits >> TERMINATION_CHARACTER_OFFSET == 1;
    }

    public boolean isEnd() {
        return flagBits >> END_OFFSET == 1;
    }

    public boolean isWaitLock() {
        return flagBits >> WAIT_LOCK_OFFSET == 1;
    }

    public void enableTerminationCharacter(boolean enable) {
        offsetBit(TERMINATION_CHARACTER_OFFSET, enable);
    }

    public void enableEnd(boolean enable) {
        offsetBit(END_OFFSET, enable);
    }

    public void enableWaitLock(boolean enable) {
        offsetBit(WAIT_LOCK_OFFSET, enable);
    }

    private void offsetBit(int offset, boolean enable) {
        this.flagBits = enable ? 1 << offset | flagBits : ~(1 << offset) & flagBits;
    }
}