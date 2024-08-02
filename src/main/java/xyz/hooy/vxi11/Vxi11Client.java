package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hooy.vxi11.rpc.CreateLinkParams;
import xyz.hooy.vxi11.rpc.CreateLinkResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Vxi11Client {

    private static final Logger log = LoggerFactory.getLogger(Vxi11Client.class);

    private final int clientId = ThreadLocalRandom.current().nextInt();

    private final InetAddress host;

    private final short port;

    protected OncRpcClient coreChannel;

    protected OncRpcClient abortChannel;

    public Vxi11Client(InetAddress host, short port) {
        this.host = host;
        this.port = port;
        try {
            this.coreChannel = OncRpcClient.newOncRpcClient(host, DeviceCore.PROGRAM, DeviceCore.VERSION, port, OncRpcProtocols.ONCRPC_TCP);
        } catch (OncRpcException | IOException e) {
            throw new Vxi11Exception(e);
        }
    }

    public DeviceLinkClient createLink(String device, boolean lockDevice, int lockTimeout) {
        CreateLinkParams request = new CreateLinkParams(clientId, lockDevice, lockTimeout, device);
        CreateLinkResponse response = new CreateLinkResponse();
        try {
            coreChannel.call(DeviceCore.Options.CREATE_LINK, request, response);
        } catch (OncRpcException e) {
            throw new Vxi11Exception(e);
        }
        response.getError().checkErrorThrowException();
        if (!connectedAbortChannel()) {
            try {
                this.abortChannel = OncRpcClient.newOncRpcClient(host, DeviceCore.PROGRAM, DeviceCore.VERSION, response.getAbortPort(), OncRpcProtocols.ONCRPC_TCP);
            } catch (OncRpcException | IOException e) {
                log.warn("Link {} failed to establish the termination channel, the instrument may not support it.", response.getLink().getLinkId());
            }
        }
        return new DeviceLinkClient(this, response.getLink().getLinkId());
    }

    public boolean connectedAbortChannel() {
        return Objects.nonNull(abortChannel);
    }

    public int getClientId() {
        return clientId;
    }

    public InetAddress getHost() {
        return host;
    }

    public short getPort() {
        return port;
    }
}
