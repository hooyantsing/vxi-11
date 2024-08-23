package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcProtocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hooy.vxi11.entity.ByteArrayBuffer;
import xyz.hooy.vxi11.entity.StatusByteEvent;
import xyz.hooy.vxi11.entity.Vxi11ServiceRequestListener;
import xyz.hooy.vxi11.exception.Vxi11ClientException;
import xyz.hooy.vxi11.rpc.DeviceError;
import xyz.hooy.vxi11.rpc.DeviceFlags;
import xyz.hooy.vxi11.rpc.DeviceReadResponse;
import xyz.hooy.vxi11.rpc.GenericRpcInvoker;
import xyz.hooy.vxi11.rpc.idl.*;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Vxi11Client {

    private final static Logger log = LoggerFactory.getLogger(Vxi11Client.class);

    private final int clientId = super.hashCode();

    private final List<Link> links = new ArrayList<>();

    private final InetAddress host;

    private final int corePort;

    private final int interruptPort;

    private String charset = StandardCharsets.UTF_8.name();

    private int timeout = 3000;

    private int ioTimeout = 1000; // 0: not block

    private int lockTimeout = 0;

    private vxi11_DEVICE_CORE_Client coreChannel;

    private vxi11_DEVICE_ASYNC_Client abortChannel;

    private vxi11_DEVICE_INTR_Server interruptChannel;

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
        Create_LinkResp response;
        try {
            Create_LinkParms request = new Create_LinkParms(clientId, lockTimeout > 0, Math.max(lockTimeout, 0), device);
            response = coreChannel.create_link_1(request);
            new DeviceError(response.error).checkErrorThrowException();
        } catch (Exception e) {
            emptyLinksCloseAllChannel();
            throw new Vxi11ClientException(e);
        }
        if (!connectedAbortChannel()) {
            openAbortChannel(response.abortPort);
        }
        if (!connectedInterruptChannel()) {
            openInterruptChannel();
        }
        Link link = new Link(response.lid, response.maxRecvSize);
        links.add(link);
        return link;
    }

    private void openCoreChannel() {
        try {
            this.coreChannel = new vxi11_DEVICE_CORE_Client(host, vxi11.DEVICE_CORE, vxi11.DEVICE_CORE_VERSION, corePort, OncRpcProtocols.ONCRPC_TCP);
            coreChannel.getClient().setTimeout(timeout);
            coreChannel.getClient().setCharacterEncoding(charset);
        } catch (Exception e) {
            throw new Vxi11ClientException(e);
        }
    }

    private void openAbortChannel(int abortPort) {
        try {
            this.abortChannel = new vxi11_DEVICE_ASYNC_Client(host, vxi11.DEVICE_ASYNC, vxi11.DEVICE_ASYNC_VERSION, abortPort, OncRpcProtocols.ONCRPC_TCP);
            abortChannel.getClient().setTimeout(timeout);
            abortChannel.getClient().setCharacterEncoding(charset);
        } catch (Exception e) {
            log.warn("Failed to establish the abort channel, the instrument may not support it.");
        }
    }

    private void openInterruptChannel() {
        try {
            vxi11_DEVICE_INTR_Server interruptServer = new vxi11_DEVICE_INTR_Server(interruptPort);
            interruptChannel.setCharacterEncoding(charset);
            interruptChannel.run();
            this.interruptChannel = interruptServer;
        } catch (Exception e) {
            log.warn("Failed to run the interrupt server.\n {}", e.getMessage());
            return;
        }
        try {
            Device_RemoteFunc request = new Device_RemoteFunc(addressInt(host), interruptPort, vxi11.DEVICE_INTR, vxi11.DEVICE_INTR_VERSION, Device_AddrFamily.DEVICE_TCP);
            Device_Error response = coreChannel.create_intr_chan_1(request);
            new DeviceError(response).checkErrorThrowException();
        } catch (Exception e) {
            log.warn("Failed to establish the interrupt channel, the instrument may not support it.\n {}", e.getMessage());
            interruptChannel.stopRpcProcessing();
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
                Device_Error response = coreChannel.destroy_intr_chan_1();
                new DeviceError(response).checkErrorThrowException();
                interruptChannel.stopRpcProcessing();
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
            coreChannel.getClient().setTimeout(timeout);
        }
        if (connectedAbortChannel()) {
            abortChannel.getClient().setTimeout(timeout);
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
            coreChannel.getClient().setCharacterEncoding(charset);
        }
        if (connectedAbortChannel()) {
            abortChannel.getClient().setCharacterEncoding(charset);
        }
        if (connectedInterruptChannel()) {
            interruptChannel.setCharacterEncoding(charset);
        }
        this.charset = charset;
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

    private static class vxi11_DEVICE_INTR_Server extends vxi11_DEVICE_INTR_ServerStub {

        private final Map<String, Link> serviceRequestLinks = new HashMap<>();

        public vxi11_DEVICE_INTR_Server() throws OncRpcException, IOException {
        }

        public vxi11_DEVICE_INTR_Server(int port) throws OncRpcException, IOException {
            super(port);
        }

        public vxi11_DEVICE_INTR_Server(InetAddress bindAddr, int port) throws OncRpcException, IOException {
            super(bindAddr, port);
        }

        @Override
        public void device_intr_srq_1(Device_SrqParms arg1) {
            Link link = serviceRequestLinks.get(new String(arg1.handle, StandardCharsets.UTF_8).trim());
            if (Objects.nonNull(link)) {
                link.actionListener();
            }
        }

        public void registerServiceRequestLinks(String handle, Link link) {
            serviceRequestLinks.put(handle, link);
        }

        public void unregisterServiceRequestLinks(String handle) {
            serviceRequestLinks.remove(handle);
        }
    }

    public class Link implements AutoCloseable {

        private final String handle = String.valueOf(super.hashCode());

        private final Device_Link link;

        private final int blockSize;

        private volatile boolean closed = false;

        private final Set<Vxi11ServiceRequestListener> serviceRequestListeners = new HashSet<>();

        protected Link(Device_Link link, int blockSize) {
            this.link = link;
            this.blockSize = blockSize;
        }

        @Override
        public void close() throws OncRpcException, IOException {
            if (!closed) {
                Device_Error response = coreChannel.destroy_link_1(link);
                new DeviceError(response).checkErrorThrowException();
                links.remove(this);
                this.closed = true;
                emptyLinksCloseAllChannel();
            }
        }

        public void write(byte[] data) throws OncRpcException, IOException {
            write(data, ioTimeout, lockTimeout);
        }

        public void write(byte[] data, int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            int writeSize = 0;
            while (writeSize < data.length) {
                byte[] block = new byte[Math.min(data.length - writeSize, blockSize)];
                System.arraycopy(data, writeSize, block, 0, block.length);
                DeviceFlags deviceFlags = new DeviceFlags().enableEnd(writeSize + blockSize >= data.length).enableWaitLock(lockTimeout > 0);
                Device_WriteParms request = new Device_WriteParms(link, Math.max(ioTimeout, 0), Math.max(lockTimeout, 0), deviceFlags.buildDeviceFlags(), block);
                Device_WriteResp response = coreChannel.device_write_1(request);
                new DeviceError(response.error).checkErrorThrowException();
                writeSize += block.length;
            }
        }

        public void writeString(String data) throws OncRpcException, IOException {
            writeString(data, ioTimeout, lockTimeout);
        }

        public void writeString(String data, int ioTimeout, int lockTimeout) throws IOException, OncRpcException {
            write(data.getBytes(charset), ioTimeout, lockTimeout);
        }

        public byte[] read(char terminationCharacter) throws OncRpcException, IOException {
            return read(terminationCharacter, ioTimeout, lockTimeout);
        }

        public byte[] read(char terminationCharacter, int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            return read((byte) terminationCharacter, ioTimeout, lockTimeout);
        }

        public byte[] read(byte terminationCharacter) throws OncRpcException, IOException {
            return read(terminationCharacter, ioTimeout, lockTimeout);
        }

        public byte[] read(byte terminationCharacter, int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            DeviceReadResponse readResponse;
            ByteArrayBuffer buffer = new ByteArrayBuffer();
            READ_TERMINATION:
            do {
                DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(true).enableWaitLock(lockTimeout > 0);
                Device_ReadParms request = new Device_ReadParms(link, blockSize, Math.max(ioTimeout, 0), Math.max(lockTimeout, 0), deviceFlags.buildDeviceFlags(), terminationCharacter);
                Device_ReadResp response = coreChannel.device_read_1(request);
                readResponse = new DeviceReadResponse(response);
                readResponse.getError().checkErrorThrowException();
                for (byte data : readResponse.getData()) {
                    buffer.append(data);
                    if (data == terminationCharacter) break READ_TERMINATION;
                }
            } while (readResponse.noReason());
            return buffer.toByteArray();
        }

        public String readString(char terminationCharacter) throws OncRpcException, IOException {
            return readString(terminationCharacter, ioTimeout, lockTimeout);
        }

        public String readString(char terminationCharacter, int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            byte[] read = read(terminationCharacter, ioTimeout, lockTimeout);
            return new String(read, charset);
        }

        public StatusByteEvent readStatusByte() throws OncRpcException, IOException {
            return readStatusByte(ioTimeout, lockTimeout);
        }

        public StatusByteEvent readStatusByte(int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
            Device_GenericParms request = new Device_GenericParms(link, deviceFlags.buildDeviceFlags(), Math.max(ioTimeout, 0), Math.max(lockTimeout, 0));
            Device_ReadStbResp response = coreChannel.device_readstb_1(request);
            new DeviceError(response.error).checkErrorThrowException();
            return new StatusByteEvent(response.stb);
        }

        public void trigger() throws OncRpcException, IOException {
            trigger(ioTimeout, lockTimeout);
        }

        public void trigger(int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            genericRpc(coreChannel::device_trigger_1, ioTimeout, lockTimeout);
        }

        public void clear() throws OncRpcException, IOException {
            clear(ioTimeout, lockTimeout);
        }

        public void clear(int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            genericRpc(coreChannel::device_clear_1, ioTimeout, lockTimeout);
        }

        public void remote() throws OncRpcException, IOException {
            remote(ioTimeout, lockTimeout);
        }

        public void remote(int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            genericRpc(coreChannel::device_remote_1, ioTimeout, lockTimeout);
        }

        public void local() throws OncRpcException, IOException {
            local(ioTimeout, lockTimeout);
        }

        public void local(int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            genericRpc(coreChannel::device_local_1, ioTimeout, lockTimeout);
        }

        public void lock() throws OncRpcException, IOException {
            lock(lockTimeout);
        }

        public void lock(int lockTimeout) throws OncRpcException, IOException {
            DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
            Device_LockParms request = new Device_LockParms(link, deviceFlags.buildDeviceFlags(), lockTimeout);
            Device_Error response = coreChannel.device_lock_1(request);
            new DeviceError(response).checkErrorThrowException();
        }

        public void unlock() throws OncRpcException, IOException {
            Device_Error response = coreChannel.device_unlock_1(link);
            new DeviceError(response).checkErrorThrowException();
        }

        public void abort() throws OncRpcException, IOException {
            Device_Error response = abortChannel.device_abort_1(link);
            new DeviceError(response).checkErrorThrowException();
        }

        public void enableServiceRequest() throws OncRpcException, IOException {
            enableServiceRequest(true);
        }

        public void enableServiceRequest(boolean enable) throws OncRpcException, IOException {
            Device_EnableSrqParms request = new Device_EnableSrqParms(link, enable, handle.getBytes(StandardCharsets.UTF_8));
            Device_Error response = coreChannel.device_enable_srq_1(request);
            new DeviceError(response).checkErrorThrowException();
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
            try {
                StatusByteEvent statusByteEvent = readStatusByte();
                for (Vxi11ServiceRequestListener listener : serviceRequestListeners) {
                    try {
                        listener.action(statusByteEvent);
                    } catch (Exception e) {
                        log.warn(e.getMessage()); // log and continue;
                    }
                }
            } catch (OncRpcException | IOException ignored) {
            }
        }

        public boolean isClosed() {
            return closed;
        }

        protected void genericRpc(GenericRpcInvoker<Device_GenericParms, Device_Error> rpc, int ioTimeout, int lockTimeout) throws OncRpcException, IOException {
            DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
            Device_GenericParms request = new Device_GenericParms(link, deviceFlags.buildDeviceFlags(), Math.max(ioTimeout, 0), Math.max(lockTimeout, 0));
            Device_Error response = rpc.invoke(request);
            new DeviceError(response).checkErrorThrowException();
        }
    }
}
