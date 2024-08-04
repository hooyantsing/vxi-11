package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import xyz.hooy.vxi11.rpc.*;

public class DeviceLinkClient implements AutoCloseable {

    private final Vxi11Client client;

    private final DeviceLink link;

    private boolean closed = false;

    public DeviceLinkClient(Vxi11Client client, int linkId) {
        this.client = client;
        this.link = new DeviceLink(linkId);
    }

    public void destroyLink() {
        if (!closed) {
            DeviceError response = new DeviceError();
            call(client.coreChannel, DeviceCore.Options.DESTROY_LINK, link, response);
            response.getError().checkErrorThrowException();
            this.closed = true;
        }
    }

    public long deviceWrite(byte[] data, int ioTimeout, int lockTimeout, boolean enableEnd, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(false).enableEnd(enableEnd).enableWaitLock(enableWaitLock);
        DeviceWriteParams request = new DeviceWriteParams(link, data, ioTimeout, lockTimeout, deviceFlags);
        DeviceWriteResponse response = new DeviceWriteResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_WRITE, request, response);
        response.getError().checkErrorThrowException();
        return response.getSize();
    }

    public byte[] deviceRead(int requestSize, int ioTimeout, int lockTimeout, byte terminationCharacter, boolean enableTerminationCharacter, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(enableTerminationCharacter).enableEnd(false).enableWaitLock(enableWaitLock);
        DeviceReadParams request = new DeviceReadParams(link, requestSize, ioTimeout, lockTimeout, terminationCharacter, deviceFlags);
        DeviceReadResponse response = new DeviceReadResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_READ, request, response);
        response.getError().checkErrorThrowException();
        return response.getData();
    }

    public byte deviceReadStb(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceReadStbResponse response = new DeviceReadStbResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_READ_STB, request, response);
        response.getError().checkErrorThrowException();
        return response.getStb();
    }

    public void deviceTrigger(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, DeviceCore.Options.DEVICE_TRIGGER, request, response);
        response.getError().checkErrorThrowException();
    }

    public void deviceClear(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, DeviceCore.Options.DEVICE_CLEAR, request, response);
        response.getError().checkErrorThrowException();
    }

    public void deviceRemote(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, DeviceCore.Options.DEVICE_REMOTE, request, response);
        response.getError().checkErrorThrowException();
    }

    public void deviceLocal(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, DeviceCore.Options.DEVICE_LOCAL, request, response);
        response.getError().checkErrorThrowException();
    }

    public void deviceLock(int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceLockParams request = new DeviceLockParams(link, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, DeviceCore.Options.DEVICE_LOCK, request, response);
        response.getError().checkErrorThrowException();
    }

    public void deviceUnlock() {
        DeviceError response = new DeviceError();
        call(client.coreChannel, DeviceCore.Options.DEVICE_UNLOCK, link, response);
        response.getError().checkErrorThrowException();
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws Exception {
        destroyLink();
    }

    private void call(OncRpcClient channel, int procedureNumber, XdrAble params, XdrAble result) {
        try {
            channel.call(procedureNumber, params, result);
        } catch (OncRpcException e) {
            throw new Vxi11Exception(e);
        }
    }
}
