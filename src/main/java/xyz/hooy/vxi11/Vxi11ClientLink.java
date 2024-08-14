package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcTimeoutException;
import org.acplt.oncrpc.XdrAble;
import xyz.hooy.vxi11.rpc.*;
import xyz.hooy.vxi11.entity.*;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

public class Vxi11ClientLink implements AutoCloseable {

    private final static int DEFAULT_IO_TIMEOUT = 0; // Not block

    private final static int DEFAULT_LOCK_TIMEOUT = 0;

    private final Vxi11Client client;

    private final DeviceLink link;

    private final String handle;

    private final int blockSize;

    private volatile boolean closed = false;

    private final Set<Vxi11ServiceRequestListener> serviceRequestListeners = new HashSet<>();

    protected Vxi11ClientLink(Vxi11Client client, CreateLinkResponse response) {
        this.client = client;
        this.link = response.getLink();
        this.handle = "TODO";
        this.blockSize = response.getMaxReceiveSize();
    }

    @Override
    public void close() {
        if (!closed) {
            DeviceError response = new DeviceError();
            call(client.coreChannel, Channels.Core.Options.DESTROY_LINK, link, response);
            response.getError().checkErrorThrowException();
            this.closed = true;
        }
    }

    public int write(byte[] data) {
        return write(data, DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public int write(byte[] data, int ioTimeout, int lockTimeout) {
        int writeSize = 0;
        while (writeSize < data.length) {
            byte[] block = new byte[Math.min(data.length - writeSize, blockSize)];
            System.arraycopy(data, writeSize, block, 0, block.length);
            DeviceFlags deviceFlags = new DeviceFlags().enableEnd(writeSize + blockSize >= data.length).enableWaitLock(lockTimeout > 0);
            DeviceWriteParams request = new DeviceWriteParams(link, block, Math.max(ioTimeout, DEFAULT_IO_TIMEOUT), Math.max(lockTimeout, DEFAULT_LOCK_TIMEOUT), deviceFlags);
            DeviceWriteResponse response = new DeviceWriteResponse();
            call(client.coreChannel, Channels.Core.Options.DEVICE_WRITE, request, response);
            response.getError().checkErrorThrowException();
            writeSize += block.length;
        }
        return writeSize;
    }

    public int writeString(String data) {
        return writeString(data, DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public int writeString(String data, int ioTimeout, int lockTimeout) {
        try {
            return write(data.getBytes(client.charset), ioTimeout, lockTimeout);
        } catch (UnsupportedEncodingException e) {
            throw new Vxi11Exception(e);
        }
    }

    public byte[] read(char terminationCharacter) {
        return read(terminationCharacter, DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public byte[] read(char terminationCharacter, int ioTimeout, int lockTimeout) {
        return read((byte) terminationCharacter, ioTimeout, lockTimeout);
    }

    public byte[] read(byte terminationCharacter) {
        return read(terminationCharacter, DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public byte[] read(byte terminationCharacter, int ioTimeout, int lockTimeout) {
        DeviceReadResponse response;
        ByteArrayBuffer buffer = new ByteArrayBuffer();
        READ_TERMINATION:
        do {
            DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(true).enableWaitLock(lockTimeout > 0);
            DeviceReadParams request = new DeviceReadParams(link, blockSize, Math.max(ioTimeout, DEFAULT_IO_TIMEOUT), Math.max(lockTimeout, DEFAULT_LOCK_TIMEOUT), terminationCharacter, deviceFlags);
            response = new DeviceReadResponse();
            call(client.coreChannel, Channels.Core.Options.DEVICE_READ, request, response);
            response.getError().checkErrorThrowException();
            for (byte data : response.getData()) {
                buffer.append(data);
                if (data == terminationCharacter) break READ_TERMINATION;
            }
        } while (response.noReason());
        return buffer.toByteArray();
    }

    public String readString(char terminationCharacter) {
        return readString(terminationCharacter, DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public String readString(char terminationCharacter, int ioTimeout, int lockTimeout) {
        byte[] read = read(terminationCharacter, ioTimeout, lockTimeout);
        try {
            return new String(read, client.charset);
        } catch (UnsupportedEncodingException e) {
            throw new Vxi11Exception(e);
        }
    }

    public byte readStatusByte() {
        return readStatusByte(DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public byte readStatusByte(int ioTimeout, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
        DeviceGenericParams request = new DeviceGenericParams(link, Math.max(ioTimeout, DEFAULT_IO_TIMEOUT), Math.max(lockTimeout, DEFAULT_LOCK_TIMEOUT), deviceFlags);
        DeviceReadStbResponse response = new DeviceReadStbResponse();
        call(client.coreChannel, Channels.Core.Options.DEVICE_READ_STB, request, response);
        response.getError().checkErrorThrowException();
        return response.getStb();
    }

    public void trigger() {
        trigger(DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public void trigger(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_TRIGGER, ioTimeout, lockTimeout);
    }

    public void clear() {
        clear(DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public void clear(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_CLEAR, ioTimeout, lockTimeout);
    }

    public void remote() {
        remote(DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public void remote(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_REMOTE, ioTimeout, lockTimeout);
    }

    public void local() {
        local(DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public void local(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_LOCAL, ioTimeout, lockTimeout);
    }

    public void lock() {
        lock(DEFAULT_IO_TIMEOUT, DEFAULT_LOCK_TIMEOUT);
    }

    public void lock(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_LOCK, ioTimeout, lockTimeout);
    }

    public void unlock() {
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_UNLOCK, link, response);
        response.getError().checkErrorThrowException();
    }

    public void abort() {
        if (!client.connectedAbortChannel()) {
            throw new UnsupportedOperationException("No channel established, method not supported.");
        }
        DeviceError response = new DeviceError();
        call(client.abortChannel, Channels.Abort.Options.DEVICE_ABORT, link, response);
        response.getError().checkErrorThrowException();
    }

    public void enableServiceRequest() {
        enableServiceRequest(true);
    }

    public void enableServiceRequest(boolean enable) {
        DeviceEnableServiceRequestParams request = new DeviceEnableServiceRequestParams(link, enable, handle);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_ENABLE_SRQ, request, response);
        response.getError().checkErrorThrowException();
        if (enable) {
            client.interruptChannel.registerServiceRequestLinks(handle, this);
        } else {
            client.interruptChannel.unregisterServiceRequestLinks(handle);
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
            listener.action(this);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    protected void genericRpc(int options, int ioTimeout, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
        DeviceGenericParams request = new DeviceGenericParams(link, Math.max(ioTimeout, DEFAULT_IO_TIMEOUT), Math.max(lockTimeout, DEFAULT_LOCK_TIMEOUT), deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, options, request, response);
        response.getError().checkErrorThrowException();
    }

    private void call(OncRpcClient channel, int procedureNumber, XdrAble params, XdrAble result) {
        try {
            channel.call(procedureNumber, params, result);
        } catch (OncRpcTimeoutException e) {
            throw new Vxi11Exception(ErrorCode.IO_TIMEOUT);
        } catch (OncRpcException e) {
            throw new Vxi11Exception(e);
        }
    }
}
