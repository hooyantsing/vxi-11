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

    public long deviceWrite(byte[] data, int ioTimeout, int lockTimeout, boolean enableTerminationCharacter, boolean enableEnd, boolean enableWaitLock) {
        DeviceWriteParams request = new DeviceWriteParams(link, ioTimeout, lockTimeout, new DeviceFlags(enableTerminationCharacter, enableEnd, enableWaitLock), data);
        DeviceWriteResponse response = new DeviceWriteResponse();
        call(client.coreChannel, DeviceCore.Options.DEVICE_WRITE, request, response);
        response.getError().checkErrorThrowException();
        return response.getSize();
    }

    public byte[] deviceRead() {
        return null;
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
