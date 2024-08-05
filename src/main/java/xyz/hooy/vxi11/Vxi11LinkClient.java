package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcTimeoutException;
import org.acplt.oncrpc.XdrAble;
import xyz.hooy.vxi11.rpc.*;
import xyz.hooy.vxi11.entity.*;
import xyz.hooy.vxi11.exception.*;

public class Vxi11LinkClient implements AutoCloseable {

    private final static int DEFAULT_IO_BUFFER = 8192;

    private final Vxi11Client client;

    private final DeviceLink link;

    private final int writeBlockSize;

    private volatile boolean closed = false;

    protected Vxi11LinkClient(Vxi11Client client, CreateLinkResponse response) {
        this.client = client;
        this.link = response.getLink();
        this.writeBlockSize = response.getMaxReceiveSize();
    }

    public int write(byte[] data) {
        return deviceWrite(data, false, 0);
    }

    public int write(byte[] data, int lockTimeout) {
        return deviceWrite(data, true, lockTimeout);
    }

    private int deviceWrite(byte[] data, boolean enableWaitLock, int lockTimeout) {
        int writeSize = 0;
        while (writeSize < data.length) {
            byte[] block = new byte[Math.min(data.length - writeSize, writeBlockSize)];
            System.arraycopy(data, writeSize, block, 0, block.length);
            DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(false).enableEnd(writeSize + writeBlockSize >= data.length).enableWaitLock(enableWaitLock);
            DeviceWriteParams request = new DeviceWriteParams(link, block, client.coreChannel.getTimeout(), lockTimeout, deviceFlags);
            DeviceWriteResponse response = new DeviceWriteResponse();
            call(client.coreChannel, Channels.Core.Options.DEVICE_WRITE, request, response);
            response.getError().checkErrorThrowException();
            writeSize += block.length;
        }
        return writeSize;
    }


    public byte[] read(int requestSize) {
        return deviceRead(requestSize, false, 0, false, (byte) 0);
    }

    public byte[] read(int requestSize, int lockTimeout) {
        return deviceRead(requestSize, true, lockTimeout, false, (byte) 0);
    }

    public byte[] read(byte terminationCharacter) {
        return deviceRead(DEFAULT_IO_BUFFER, false, 0, true, terminationCharacter);
    }

    public byte[] read(byte terminationCharacter, int lockTimeout) {
        return deviceRead(DEFAULT_IO_BUFFER, true, lockTimeout, true, terminationCharacter);
    }

    private byte[] deviceRead(int requestSize, boolean enableWaitLock, int lockTimeout, boolean enableTerminationCharacter, byte terminationCharacter) {
        DeviceReadResponse response;
        ByteArrayBuffer buffer = new ByteArrayBuffer();
        do {
            DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(enableTerminationCharacter).enableEnd(false).enableWaitLock(enableWaitLock);
            DeviceReadParams request = new DeviceReadParams(link, requestSize, client.coreChannel.getTimeout(), lockTimeout, terminationCharacter, deviceFlags);
            response = new DeviceReadResponse();
            call(client.coreChannel, Channels.Core.Options.DEVICE_READ, request, response);
            response.getError().checkErrorThrowException();
            buffer.append(response.getData());
        } while (response.noReason());
        return buffer.toByteArray();
    }

    public byte readStb() {
        return deviceReadStb(false, 0);
    }

    public byte readStb(int lockTimeout) {
        return deviceReadStb(true, lockTimeout);
    }

    private byte deviceReadStb(boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, client.coreChannel.getTimeout(), lockTimeout, deviceFlags);
        DeviceReadStbResponse response = new DeviceReadStbResponse();
        call(client.coreChannel, Channels.Core.Options.DEVICE_READ_STB, request, response);
        response.getError().checkErrorThrowException();
        return response.getStb();
    }

    public void trigger() {
        deviceTrigger(false, 0);
    }

    public void trigger(int lockTimeout) {
        deviceTrigger(true, lockTimeout);
    }

    private void deviceTrigger(boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, client.coreChannel.getTimeout(), lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_TRIGGER, request, response);
        response.getError().checkErrorThrowException();
    }

    public void clear() {
        deviceClear(false, 0);
    }

    public void clear(int lockTimeout) {
        deviceClear(true, lockTimeout);
    }

    private void deviceClear(boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, client.coreChannel.getTimeout(), lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_CLEAR, request, response);
        response.getError().checkErrorThrowException();
    }

    public void remote() {
        deviceRemote(false, 0);
    }

    public void remote(int lockTimeout) {
        deviceRemote(true, lockTimeout);
    }

    private void deviceRemote(boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, client.coreChannel.getTimeout(), lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_REMOTE, request, response);
        response.getError().checkErrorThrowException();
    }

    public void local() {
        deviceLocal(false, 0);
    }

    public void local(int lockTimeout) {
        deviceLocal(true, lockTimeout);
    }

    private void deviceLocal(boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, client.coreChannel.getTimeout(), lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_LOCAL, request, response);
        response.getError().checkErrorThrowException();
    }

    public void lock() {
        deviceLock(false, 0);
    }

    public void lock(int lockTimeout) {
        deviceLock(true, lockTimeout);
    }

    private void deviceLock(boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceLockParams request = new DeviceLockParams(link, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_LOCK, request, response);
        response.getError().checkErrorThrowException();
    }

    public void unlock() {
        deviceUnlock();
    }

    private void deviceUnlock() {
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_UNLOCK, link, response);
        response.getError().checkErrorThrowException();
    }

    public void abort() {
        deviceAbort();
    }

    private void deviceAbort() {
        if (!client.connectedAbortChannel()) {
            throw new UnsupportedOperationException("No channel established, method not supported.");
        }
        DeviceError response = new DeviceError();
        call(client.abortChannel, Channels.Abort.Options.DEVICE_ABORT, link, response);
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

    public boolean isClosed() {
        return closed;
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
}
