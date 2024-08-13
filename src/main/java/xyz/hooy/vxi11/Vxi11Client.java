package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;
import org.acplt.oncrpc.XdrVoid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hooy.vxi11.rpc.*;
import xyz.hooy.vxi11.exception.Vxi11Exception;

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

    protected OncRpcClient coreChannel;

    protected OncRpcClient abortChannel;

    protected Vxi11ClientInterruptServer interruptChannel;

    private final List<Vxi11ClientLink> links = new ArrayList<>();

    public Vxi11Client(InetAddress host, int port) {
        this.host = host;
        openCoreChannel(port);
    }

    @Override
    public void close() {
        for (Vxi11ClientLink link : links) {
            if (!link.isClosed()) {
                link.close();
            }
        }
        links.clear();
        closeInterruptChannel();
        closeAbortChannel();
        closeCoreChannel();
    }

    public Vxi11ClientLink createLink(String device) {
        return createLink(device, 0);
    }

    public Vxi11ClientLink createLink(String device, int lockTimeout) {
        CreateLinkParams request = new CreateLinkParams(clientId, lockTimeout > 0, Math.max(lockTimeout, 0), device);
        CreateLinkResponse response = new CreateLinkResponse();
        try {
            coreChannel.call(Channels.Core.Options.CREATE_LINK, request, response);
        } catch (OncRpcException e) {
            throw new Vxi11Exception(e);
        }
        response.getError().checkErrorThrowException();
        if (!connectedAbortChannel()) {
            openAbortChannel(response.getAbortPort());
        }
        Vxi11ClientLink link = new Vxi11ClientLink(this, response);
        links.add(link);
        return link;
    }

    private void openCoreChannel(int corePort) {
        try {
            this.coreChannel = OncRpcClient.newOncRpcClient(host, Channels.Core.PROGRAM, Channels.Core.VERSION, corePort, OncRpcProtocols.ONCRPC_TCP);
        } catch (OncRpcException | IOException e) {
            throw new Vxi11Exception(e);
        }
    }

    private void closeCoreChannel(){
        try {
            coreChannel.close();
        } catch (OncRpcException e) {
            log.warn("Close core channel failed.", e);
        }
        this.coreChannel = null;
    }

    private void openAbortChannel(int abortPort) {
        try {
            this.abortChannel = OncRpcClient.newOncRpcClient(host, Channels.Abort.PROGRAM, Channels.Abort.VERSION, abortPort, OncRpcProtocols.ONCRPC_TCP);
        } catch (OncRpcException | IOException e) {
            log.warn("Failed to establish the abort channel, the instrument may not support it.");
        }
    }

    private void closeAbortChannel() {
        if (!connectedAbortChannel()) {
            try {
                abortChannel.close();
            } catch (OncRpcException e) {
                log.warn("Close abort channel failed.", e);
            }
            this.abortChannel = null;
        }
    }

    public boolean connectedAbortChannel() {
        return Objects.nonNull(abortChannel);
    }

    public void openInterruptChannel(int interruptPort) {
        this.interruptChannel = new Vxi11ClientInterruptServer();
        try {
            int address = 0;
            byte[] addressBytes = host.getAddress();
            for (byte bytes : addressBytes) {
                address <<= 8;
                address |= (bytes & 0xFF);
            }
            DeviceRemoteFunction request = new DeviceRemoteFunction(address, interruptPort, Channels.Interrupt.PROGRAM, Channels.Interrupt.PROGRAM);
            DeviceError response = new DeviceError();
            coreChannel.call(Channels.Core.Options.CREATE_INTERRUPT_CHANNEL, request, response);
            response.getError().checkErrorThrowException();
        } catch (Exception e) {
            log.warn("Failed to establish the interrupt channel, the instrument may not support it.\n {}", e.getMessage());
        }
    }

    public void closeInterruptChannel() {
        if (connectedInterruptChannel()) {
            try {
                XdrVoid request = new XdrVoid();
                DeviceError response = new DeviceError();
                coreChannel.call(Channels.Core.Options.DESTROY_INTERRUPT_CHANNEL, request, response);
                interruptChannel.close();
            } catch (Exception e) {
                log.warn("Close interrupt channel failed.", e);
            }
            this.interruptChannel = null;
        }
    }

    public boolean connectedInterruptChannel() {
        return Objects.nonNull(interruptChannel);
    }

    public void setTimeout(int timeout) {
        coreChannel.setTimeout(timeout);
        if (connectedAbortChannel()) {
            abortChannel.setTimeout(timeout);
        }
    }
}
