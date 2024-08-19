package xyz.hooy.vxi11;

import org.acplt.oncrpc.*;
import org.acplt.oncrpc.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hooy.vxi11.entity.ByteArrayBuffer;
import xyz.hooy.vxi11.entity.StatusByte;
import xyz.hooy.vxi11.entity.Vxi11ServiceRequestListener;
import xyz.hooy.vxi11.exception.Vxi11ClientException;
import xyz.hooy.vxi11.rpc.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Vxi11Client {

    private final static Logger log = LoggerFactory.getLogger(Vxi11Client.class);

    private final int clientId = hashCode();

    private final List<Link> links = new ArrayList<>();

    private final InetAddress host;

    private final int corePort;

    private final int interruptPort;

    private String charset = StandardCharsets.UTF_8.name();

    private int timeout = 3000;
    
    private int ioTimeout = 0; // 0: not block
    
    private int lockTimeout = 0;

    private OncRpcClient coreChannel;

    private OncRpcClient abortChannel;

    private InterruptServer interruptChannel;

    public Vxi11Client(InetAddress host) {
        this(host, 1180, 1181);
    }

    public Vxi11Client(InetAddress host, int corePort, int interruptPort) {
        this.host = host;
        this.corePort = corePort;
        this.interruptPort = interruptPort;
    }

    public Link createLink(String device) {
        return createLink(device, lockTimeout);
    }

    public Link createLink(String device, int lockTimeout) {
        if (!connectedCoreChannel()) {
            openCoreChannel();
        }
        CreateLinkParams request = new CreateLinkParams(clientId, lockTimeout > 0, Math.max(lockTimeout, 0), device);
        CreateLinkResponse response = new CreateLinkResponse();
        try {
            call(coreChannel, Channels.Core.Options.CREATE_LINK, request, response);
            response.getError().checkErrorThrowException();
        } catch (Exception e) {
            emptyLinksCloseAllChannel();
            throw e;
        }
        if (!connectedAbortChannel()) {
            openAbortChannel(response.getAbortPort());
        }
        if (!connectedInterruptChannel()) {
            openInterruptChannel();
        }
        Link link = new Link(response.getLink(), response.getMaxReceiveSize());
        links.add(link);
        return link;
    }

    private void openCoreChannel() {
        try {
            this.coreChannel = OncRpcClient.newOncRpcClient(host, Channels.Core.PROGRAM, Channels.Core.VERSION, corePort, OncRpcProtocols.ONCRPC_TCP);
            coreChannel.setTimeout(timeout);
            coreChannel.setCharacterEncoding(charset);
        } catch (Exception e) {
            throw new Vxi11ClientException(e);
        }
    }

    private void openAbortChannel(int abortPort) {
        try {
            this.abortChannel = OncRpcClient.newOncRpcClient(host, Channels.Abort.PROGRAM, Channels.Abort.VERSION, abortPort, OncRpcProtocols.ONCRPC_TCP);
            abortChannel.setTimeout(timeout);
            abortChannel.setCharacterEncoding(charset);
        } catch (Exception e) {
            log.warn("Failed to establish the abort channel, the instrument may not support it.");
        }
    }

    private void openInterruptChannel() {
        try {
            InterruptServer interruptServer = new InterruptServer(interruptPort);
            interruptChannel.setCharacterEncoding(charset);
            interruptChannel.run();
            this.interruptChannel = interruptServer;
        } catch (Exception e) {
            log.warn("Failed to run the interrupt server.\n {}", e.getMessage());
            return;
        }
        try {
            DeviceRemoteFunction request = new DeviceRemoteFunction(addressInt(host), interruptPort, Channels.Interrupt.PROGRAM, Channels.Interrupt.PROGRAM);
            DeviceError response = new DeviceError();
            call(coreChannel, Channels.Core.Options.CREATE_INTERRUPT_CHANNEL, request, response);
            response.getError().checkErrorThrowException();
        } catch (Exception e) {
            log.warn("Failed to establish the interrupt channel, the instrument may not support it.\n {}", e.getMessage());
            interruptChannel.close();
            interruptChannel = null;
        }
    }

    private void closeCoreChannel() {
        if (connectedCoreChannel()) {
            try {
                coreChannel.close();
            } catch (Exception e) {
                log.warn("Close core channel failed.", e);
            }
            this.coreChannel = null;
        }
    }

    private void closeAbortChannel() {
        if (connectedAbortChannel()) {
            try {
                abortChannel.close();
            } catch (Exception e) {
                log.warn("Close abort channel failed.", e);
            }
            this.abortChannel = null;
        }
    }

    private void closeInterruptChannel() {
        if (connectedInterruptChannel()) {
            try {
                XdrVoid request = XdrVoid.XDR_VOID;
                DeviceError response = new DeviceError();
                call(coreChannel, Channels.Core.Options.DESTROY_INTERRUPT_CHANNEL, request, response);
                interruptChannel.close();
            } catch (Exception e) {
                log.warn("Close interrupt channel failed.", e);
            }
            this.interruptChannel = null;
        }
    }

    private void emptyLinksCloseAllChannel() {
        if (links.stream().allMatch(Link::isClosed)) {
            closeInterruptChannel();
            closeAbortChannel();
            closeCoreChannel();
        }
    }

    public boolean connectedCoreChannel() {
        return Objects.nonNull(coreChannel);
    }

    public boolean connectedAbortChannel() {
        return Objects.nonNull(abortChannel);
    }

    public boolean connectedInterruptChannel() {
        return Objects.nonNull(interruptChannel);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        if (connectedCoreChannel()) {
            coreChannel.setTimeout(timeout);
        }
        if (connectedAbortChannel()) {
            abortChannel.setTimeout(timeout);
        }
        // InterruptChannel default timeout
        this.timeout = timeout;
    }

    public int getIoTimeout() {
        return ioTimeout;
    }

    public void setIoTimeout(int ioTimeout) {
        this.ioTimeout = ioTimeout;
    }

    public int getLockTimeout() {
        return lockTimeout;
    }

    public void setLockTimeout(int lockTimeout) {
        this.lockTimeout = lockTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        if (connectedCoreChannel()) {
            coreChannel.setCharacterEncoding(charset);
        }
        if (connectedAbortChannel()) {
            abortChannel.setCharacterEncoding(charset);
        }
        if (connectedInterruptChannel()) {
            interruptChannel.setCharacterEncoding(charset);
        }
        this.charset = charset;
    }

    private void call(OncRpcClient channel, int procedureNumber, XdrAble params, XdrAble result) {
        if (Objects.isNull(channel)) {
            throw new IllegalArgumentException("No channel established, method not supported.");
        }
        try {
            channel.call(procedureNumber, params, result);
        } catch (Exception e) {
            throw new Vxi11ClientException(e);
        }
    }

    private int addressInt(InetAddress host) {
        int address = 0;
        byte[] addressBytes = host.getAddress();
        for (byte bytes : addressBytes) {
            address <<= 8;
            address |= (bytes & 0xFF);
        }
        return address;
    }

    public class Link implements AutoCloseable {

        private final String handle = String.valueOf(hashCode());

        private final DeviceLink link;

        private final int blockSize;

        private volatile boolean closed = false;

        private final Set<Vxi11ServiceRequestListener> serviceRequestListeners = new HashSet<>();

        protected Link(DeviceLink link, int blockSize) {
            this.link = link;
            this.blockSize = blockSize;
        }

        @Override
        public void close() {
            if (!closed) {
                DeviceError response = new DeviceError();
                call(coreChannel, Channels.Core.Options.DESTROY_LINK, link, response);
                response.getError().checkErrorThrowException();
                links.remove(this);
                this.closed = true;
                emptyLinksCloseAllChannel();
            }
        }

        public void write(byte[] data) {
            write(data, ioTimeout, lockTimeout);
        }

        public void write(byte[] data, int ioTimeout, int lockTimeout) {
            int writeSize = 0;
            while (writeSize < data.length) {
                byte[] block = new byte[Math.min(data.length - writeSize, blockSize)];
                System.arraycopy(data, writeSize, block, 0, block.length);
                DeviceFlags deviceFlags = new DeviceFlags().enableEnd(writeSize + blockSize >= data.length).enableWaitLock(lockTimeout > 0);
                DeviceWriteParams request = new DeviceWriteParams(link, block, Math.max(ioTimeout, 0), Math.max(lockTimeout, 0), deviceFlags);
                DeviceWriteResponse response = new DeviceWriteResponse();
                call(coreChannel, Channels.Core.Options.DEVICE_WRITE, request, response);
                response.getError().checkErrorThrowException();
                writeSize += block.length;
            }
        }

        public void writeString(String data) {
            writeString(data, ioTimeout, lockTimeout);
        }

        public void writeString(String data, int ioTimeout, int lockTimeout) {
            try {
                write(data.getBytes(charset), ioTimeout, lockTimeout);
            } catch (UnsupportedEncodingException e) {
                throw new Vxi11ClientException(e);
            }
        }

        public byte[] read(char terminationCharacter) {
            return read(terminationCharacter, ioTimeout, lockTimeout);
        }

        public byte[] read(char terminationCharacter, int ioTimeout, int lockTimeout) {
            return read((byte) terminationCharacter, ioTimeout, lockTimeout);
        }

        public byte[] read(byte terminationCharacter) {
            return read(terminationCharacter, ioTimeout, lockTimeout);
        }

        public byte[] read(byte terminationCharacter, int ioTimeout, int lockTimeout) {
            DeviceReadResponse response;
            ByteArrayBuffer buffer = new ByteArrayBuffer();
            READ_TERMINATION:
            do {
                DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(true).enableWaitLock(lockTimeout > 0);
                DeviceReadParams request = new DeviceReadParams(link, blockSize, Math.max(ioTimeout, 0), Math.max(lockTimeout, 0), terminationCharacter, deviceFlags);
                response = new DeviceReadResponse();
                call(coreChannel, Channels.Core.Options.DEVICE_READ, request, response);
                response.getError().checkErrorThrowException();
                for (byte data : response.getData()) {
                    buffer.append(data);
                    if (data == terminationCharacter) break READ_TERMINATION;
                }
            } while (response.noReason());
            return buffer.toByteArray();
        }

        public String readString(char terminationCharacter) {
            return readString(terminationCharacter, ioTimeout, lockTimeout);
        }

        public String readString(char terminationCharacter, int ioTimeout, int lockTimeout) {
            byte[] read = read(terminationCharacter, ioTimeout, lockTimeout);
            try {
                return new String(read, charset);
            } catch (UnsupportedEncodingException e) {
                throw new Vxi11ClientException(e);
            }
        }

        public StatusByte readStatusByte() {
            return readStatusByte(ioTimeout, lockTimeout);
        }

        public StatusByte readStatusByte(int ioTimeout, int lockTimeout) {
            DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
            DeviceGenericParams request = new DeviceGenericParams(link, Math.max(ioTimeout, 0), Math.max(lockTimeout, 0), deviceFlags);
            DeviceReadStbResponse response = new DeviceReadStbResponse();
            call(coreChannel, Channels.Core.Options.DEVICE_READ_STB, request, response);
            response.getError().checkErrorThrowException();
            return new StatusByte(response.getStb());
        }

        public void trigger() {
            trigger(ioTimeout, lockTimeout);
        }

        public void trigger(int ioTimeout, int lockTimeout) {
            genericRpc(Channels.Core.Options.DEVICE_TRIGGER, ioTimeout, lockTimeout);
        }

        public void clear() {
            clear(ioTimeout, lockTimeout);
        }

        public void clear(int ioTimeout, int lockTimeout) {
            genericRpc(Channels.Core.Options.DEVICE_CLEAR, ioTimeout, lockTimeout);
        }

        public void remote() {
            remote(ioTimeout, lockTimeout);
        }

        public void remote(int ioTimeout, int lockTimeout) {
            genericRpc(Channels.Core.Options.DEVICE_REMOTE, ioTimeout, lockTimeout);
        }

        public void local() {
            local(ioTimeout, lockTimeout);
        }

        public void local(int ioTimeout, int lockTimeout) {
            genericRpc(Channels.Core.Options.DEVICE_LOCAL, ioTimeout, lockTimeout);
        }

        public void lock() {
            lock(ioTimeout, lockTimeout);
        }

        public void lock(int ioTimeout, int lockTimeout) {
            genericRpc(Channels.Core.Options.DEVICE_LOCK, ioTimeout, lockTimeout);
        }

        public void unlock() {
            DeviceError response = new DeviceError();
            call(coreChannel, Channels.Core.Options.DEVICE_UNLOCK, link, response);
            response.getError().checkErrorThrowException();
        }

        public void abort() {
            DeviceError response = new DeviceError();
            call(abortChannel, Channels.Abort.Options.DEVICE_ABORT, link, response);
            response.getError().checkErrorThrowException();
        }

        public void enableServiceRequest() {
            enableServiceRequest(true);
        }

        public void enableServiceRequest(boolean enable) {
            DeviceEnableServiceRequestParams request = new DeviceEnableServiceRequestParams(link, enable, handle);
            DeviceError response = new DeviceError();
            call(coreChannel, Channels.Core.Options.DEVICE_ENABLE_SRQ, request, response);
            response.getError().checkErrorThrowException();
            if (enable) {
                interruptChannel.registerServiceRequestLinks(handle, this);
            } else {
                interruptChannel.unregisterServiceRequestLinks(handle);
            }
        }

        public void addServiceRequestListener(Vxi11ServiceRequestListener listener) {
            serviceRequestListeners.add(listener);
        }

        public void removeServiceRequestListener(Vxi11ServiceRequestListener listener) {
            serviceRequestListeners.remove(listener);
        }

        protected void actionListener() {
            for (Vxi11ServiceRequestListener listener : serviceRequestListeners) {
                listener.action();
            }
        }

        public boolean isClosed() {
            return closed;
        }

        protected void genericRpc(int options, int ioTimeout, int lockTimeout) {
            DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
            DeviceGenericParams request = new DeviceGenericParams(link, Math.max(ioTimeout, 0), Math.max(lockTimeout, 0), deviceFlags);
            DeviceError response = new DeviceError();
            call(coreChannel, options, request, response);
            response.getError().checkErrorThrowException();
        }
    }

    private static class InterruptServer extends OncRpcServerStub implements OncRpcDispatchable, AutoCloseable {

        private final Map<String, Link> serviceRequestLinks = new HashMap<>();

        private static final int BUFFER_SIZE = Short.MAX_VALUE;

        public InterruptServer(int port) throws OncRpcException, IOException {
            this(null, port);
        }

        public InterruptServer(InetAddress bindAddr, int port) throws OncRpcException, IOException {
            info = new OncRpcServerTransportRegistrationInfo[]{
                    new OncRpcServerTransportRegistrationInfo(Channels.Interrupt.PROGRAM, Channels.Interrupt.VERSION)
            };
            transports = new OncRpcServerTransport[]{
                    new OncRpcUdpServerTransport(this, bindAddr, port, info, BUFFER_SIZE),
                    new OncRpcTcpServerTransport(this, bindAddr, port, info, BUFFER_SIZE)
            };
        }

        public void dispatchOncRpcCall(OncRpcCallInformation call, int program, int version, int procedure) throws OncRpcException, IOException {
            if (version == Channels.Interrupt.VERSION) {
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

        public void registerServiceRequestLinks(String handle, Link link) {
            serviceRequestLinks.put(handle, link);
        }

        public void unregisterServiceRequestLinks(String handle) {
            serviceRequestLinks.remove(handle);
        }

        private void interruptServiceRequest(DeviceServiceRequestParams request) {
            Link link = serviceRequestLinks.get(request.getHandle());
            if (Objects.nonNull(link)) {
                link.actionListener();
            }
        }

        @Override
        public void close() {
            stopRpcProcessing();
        }
    }
}
