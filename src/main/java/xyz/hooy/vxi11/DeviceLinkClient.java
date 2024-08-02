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
        DeviceWriteParams request = new DeviceWriteParams(link, data, ioTimeout, lockTimeout, new DeviceFlags(false, enableEnd, enableWaitLock));
        DeviceWriteResponse response = new DeviceWriteResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_WRITE, request, response);
        response.getError().checkErrorThrowException();
        return response.getSize();
    }

    public byte[] deviceRead(int requestSize, int ioTimeout, int lockTimeout, byte terminationCharacter, boolean enableTerminationCharacter, boolean enableWaitLock) {
        DeviceReadParams request = new DeviceReadParams(link, requestSize, ioTimeout, lockTimeout, terminationCharacter, new DeviceFlags(enableTerminationCharacter, false, enableWaitLock));
        DeviceReadResponse response = new DeviceReadResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_READ, request, response);
        response.getError().checkErrorThrowException();
        return response.getData();
    }

    public byte deviceReadStb(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, new DeviceFlags(false, false, enableWaitLock));
        DeviceReadStbResponse response = new DeviceReadStbResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_READ_STB, request, response);
        response.getError().checkErrorThrowException();
        return response.getStb();
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
