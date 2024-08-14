package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrVoid;
import org.acplt.oncrpc.server.*;
import xyz.hooy.vxi11.rpc.Channels;
import xyz.hooy.vxi11.rpc.DeviceServiceRequestParams;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Vxi11ClientInterruptServer extends OncRpcServerStub implements OncRpcDispatchable, AutoCloseable {

    private final Map<String, Vxi11ClientLink> serviceRequestLinks = new HashMap<>();

    private static final int BUFFER_SIZE = Short.MAX_VALUE;

    public Vxi11ClientInterruptServer(int port) throws OncRpcException, IOException {
        this(null, port);
    }

    public Vxi11ClientInterruptServer(InetAddress bindAddr, int port) throws OncRpcException, IOException {
        info = new OncRpcServerTransportRegistrationInfo[]{
                new OncRpcServerTransportRegistrationInfo(Channels.Interrupt.PROGRAM, Channels.Interrupt.VERSION)
        };
        transports = new OncRpcServerTransport[]{
                new OncRpcUdpServerTransport(this, bindAddr, port, info, BUFFER_SIZE),
                new OncRpcTcpServerTransport(this, bindAddr, port, info, BUFFER_SIZE)
        };
    }

    public void dispatchOncRpcCall(OncRpcCallInformation call, int program, int version, int procedure) throws OncRpcException, IOException {
        if (version == 1) {
            switch (procedure) {
                case 0:
                    call.retrieveCall(XdrVoid.XDR_VOID);
                    call.reply(XdrVoid.XDR_VOID);
                    break;
                case 30: {
                    DeviceServiceRequestParams request = new DeviceServiceRequestParams();
                    call.retrieveCall(request);
                    interruptServiceRequest(request);
                    call.reply(XdrVoid.XDR_VOID);
                    break;
                }
                default:
                    call.failProcedureUnavailable();
            }
        } else {
            call.failProgramUnavailable();
        }
    }

    public void registerServiceRequestLinks(String handle, Vxi11ClientLink link) {
        serviceRequestLinks.put(handle, link);
    }

    public void unregisterServiceRequestLinks(String handle) {
        serviceRequestLinks.remove(handle);
    }

    private void interruptServiceRequest(DeviceServiceRequestParams request) {
        Vxi11ClientLink link = serviceRequestLinks.get(request.getHandle());
        if (Objects.nonNull(link)) {
            link.actionListener();
        }
    }

    @Override
    public void close() {
        stopRpcProcessing();
    }
}
