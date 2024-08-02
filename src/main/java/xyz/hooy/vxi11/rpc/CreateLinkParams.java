package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class CreateLinkParams implements XdrAble {

    private int clientId;
    private boolean lockDevice;
    private int lockTimeout;
    private String device;

    public CreateLinkParams(int clientId, boolean lockDevice, int lockTimeout, String device) {
        this.clientId = clientId;
        this.lockDevice = lockDevice;
        this.lockTimeout = lockTimeout;
        this.device = device;
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeInt(clientId);
        xdr.xdrEncodeBoolean(lockDevice);
        xdr.xdrEncodeInt(lockTimeout);
        xdr.xdrEncodeString(device);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        clientId = xdr.xdrDecodeInt();
        lockDevice = xdr.xdrDecodeBoolean();
        lockTimeout = xdr.xdrDecodeInt();
        device = xdr.xdrDecodeString();
    }

    public int getClientId() {
        return clientId;
    }

    public boolean isLockDevice() {
        return lockDevice;
    }

    public int getLockTimeout() {
        return lockTimeout;
    }

    public String getDevice() {
        return device;
    }
}