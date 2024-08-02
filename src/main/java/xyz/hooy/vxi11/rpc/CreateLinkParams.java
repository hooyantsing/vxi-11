package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class CreateLinkParams implements XdrAble {

    private int clientId;
    private boolean lockDevice;
    private int lock_timeout;
    private String device;

    public CreateLinkParams(int clientId, boolean lockDevice, int lock_timeout, String device) {
        this.clientId = clientId;
        this.lockDevice = lockDevice;
        this.lock_timeout = lock_timeout;
        this.device = device;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeInt(clientId);
        xdr.xdrEncodeBoolean(lockDevice);
        xdr.xdrEncodeInt(lock_timeout);
        xdr.xdrEncodeString(device);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        clientId = xdr.xdrDecodeInt();
        lockDevice = xdr.xdrDecodeBoolean();
        lock_timeout = xdr.xdrDecodeInt();
        device = xdr.xdrDecodeString();
    }

    public int getClientId() {
        return clientId;
    }

    public boolean isLockDevice() {
        return lockDevice;
    }

    public int getLock_timeout() {
        return lock_timeout;
    }

    public String getDevice() {
        return device;
    }
}