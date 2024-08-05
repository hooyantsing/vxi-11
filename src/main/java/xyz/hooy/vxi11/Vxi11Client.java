package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hooy.vxi11.entity.Channels;
import xyz.hooy.vxi11.exception.Vxi11Exception;
import xyz.hooy.vxi11.rpc.CreateLinkParams;
import xyz.hooy.vxi11.rpc.CreateLinkResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Vxi11Client implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Vxi11Client.class);

    private final int clientId = ThreadLocalRandom.current().nextInt();

    private final InetAddress host;

    private final int port;

    private final List<Vxi11LinkClient> links = new ArrayList<>();

    protected OncRpcClient coreChannel;

    protected OncRpcClient abortChannel;

    public Vxi11Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.coreChannel = OncRpcClient.newOncRpcClient(host, Channels.Core.PROGRAM, Channels.Core.VERSION, port, OncRpcProtocols.ONCRPC_TCP);
        } catch (OncRpcException | IOException e) {
            throw new Vxi11Exception(e);
        }
    }

    public Vxi11LinkClient createLink(String device) {
        return createLink(device, false, 0);
    }

    public Vxi11LinkClient createLink(String device, int lockTimeout) {
        return createLink(device, true, lockTimeout);
    }

    private Vxi11LinkClient createLink(String device, boolean lockDevice, int lockTimeout) {
        CreateLinkParams request = new CreateLinkParams(clientId, lockDevice, lockTimeout, device);
        CreateLinkResponse response = new CreateLinkResponse();
        try {
            coreChannel.call(Channels.Core.Options.CREATE_LINK, request, response);
        } catch (OncRpcException e) {
            throw new Vxi11Exception(e);
        }
        response.getError().checkErrorThrowException();
        if (!connectedAbortChannel()) {
            try {
                this.abortChannel = OncRpcClient.newOncRpcClient(host, Channels.Abort.PROGRAM, Channels.Abort.VERSION, response.getAbortPort(), OncRpcProtocols.ONCRPC_TCP);
            } catch (OncRpcException | IOException e) {
                log.warn("Link {} failed to establish the abort channel, the instrument may not support it.", response.getLink().getLinkId());
            }
        }
        Vxi11LinkClient link = new Vxi11LinkClient(this, response);
        links.add(link);
        return link;
    }

    public boolean connectedAbortChannel() {
        return Objects.nonNull(abortChannel);
    }

    public void setTimeout(int timeout) {
        coreChannel.setTimeout(timeout);
        if (connectedAbortChannel()) {
            abortChannel.setTimeout(timeout);
        }
    }

    @Override
    public void close() {
        for (Vxi11LinkClient link : links) {
            if (!link.isClosed()) {
                link.close();
            }
        }
        try {
            coreChannel.close();
            if (connectedAbortChannel()) {
                abortChannel.close();
            }
        } catch (OncRpcException e) {
            log.warn("Close channel failed.", e);
        }
    }

    public int getClientId() {
        return clientId;
    }

    public InetAddress getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
