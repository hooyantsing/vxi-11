package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcTimeoutException;
import org.acplt.oncrpc.XdrAble;
import xyz.hooy.vxi11.entity.ByteArrayBuffer;
import xyz.hooy.vxi11.entity.Channels;
import xyz.hooy.vxi11.entity.ErrorCode;
import xyz.hooy.vxi11.exception.Vxi11Exception;
import xyz.hooy.vxi11.rpc.*;

import java.time.Instant;

public class Vxi11LinkClient implements AutoCloseable {

    private final static int DEFAULT_IO_BUFFER = 8192;

    private final static int DEFAULT_IO_TIMEOUT = 20000;

    private final Vxi11Client client;

    private final DeviceLink link;

    private final int writeBlockSize;

    private boolean closed = false;

    protected Vxi11LinkClient(Vxi11Client client, CreateLinkResponse response) {
        this.client = client;
        this.link = response.getLink();
        this.writeBlockSize = response.getMaxReceiveSize();
    }

    public int write(byte[] data) {
        return deviceWrite(data, DEFAULT_IO_TIMEOUT, false, 0);
    }

    public int write(byte[] data, int ioTimeout) {
        return deviceWrite(data, ioTimeout, false, 0);
    }

    public int write(byte[] data, int ioTimeout, int lockTimeout) {
        return deviceWrite(data, ioTimeout, true, lockTimeout);
    }

    private int deviceWrite(byte[] data, int ioTimeout, boolean enableWaitLock, int lockTimeout) {
        int writeSize = 0;
        int remainingTime = ioTimeout;
        while (writeSize < data.length) {
            if (remainingTime < 0) throw new Vxi11Exception(ErrorCode.IO_TIMEOUT);
            long startTime = Instant.now().toEpochMilli();
            byte[] block = new byte[Math.min(data.length - writeSize, writeBlockSize)];
            System.arraycopy(data, writeSize, block, 0, block.length);
            DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(false).enableEnd(writeSize + writeBlockSize >= data.length).enableWaitLock(enableWaitLock);
            DeviceWriteParams request = new DeviceWriteParams(link, block, remainingTime, lockTimeout, deviceFlags);
            DeviceWriteResponse response = new DeviceWriteResponse();
            call(client.coreChannel, Channels.Core.Options.DEVICE_WRITE, request, response, remainingTime);
            response.getError().checkErrorThrowException();
            writeSize += block.length;
            remainingTime -= (int) (Instant.now().toEpochMilli() - startTime);
        }
        return writeSize;
    }

    public byte[] read(int requestSize) {
        return read(requestSize, DEFAULT_IO_TIMEOUT);
    }

    public byte[] read(int requestSize, int ioTimeout) {
        return deviceRead(requestSize, ioTimeout, false, 0, false, (byte) 0);
    }

    public byte[] read(int requestSize, int ioTimeout, int lockTimeout) {
        return deviceRead(requestSize, ioTimeout, true, lockTimeout, false, (byte) 0);
    }

    public byte[] read(byte terminationCharacter) {
        return read(terminationCharacter, DEFAULT_IO_TIMEOUT);
    }

    public byte[] read(byte terminationCharacter, int ioTimeout) {
        return deviceRead(DEFAULT_IO_BUFFER, ioTimeout, false, 0, true, terminationCharacter);
    }

    public byte[] read(byte terminationCharacter, int ioTimeout, int lockTimeout) {
        return deviceRead(DEFAULT_IO_BUFFER, ioTimeout, true, lockTimeout, true, terminationCharacter);
    }

    private byte[] deviceRead(int requestSize, int ioTimeout, boolean enableWaitLock, int lockTimeout, boolean enableTerminationCharacter, byte terminationCharacter) {
        DeviceReadResponse response;
        ByteArrayBuffer buffer = new ByteArrayBuffer();
        int remainingTime = ioTimeout;
        do {
            if (remainingTime < 0) throw new Vxi11Exception(ErrorCode.IO_TIMEOUT);
            long startTime = Instant.now().toEpochMilli();
            DeviceFlags deviceFlags = new DeviceFlags().enableTerminationCharacter(enableTerminationCharacter).enableEnd(false).enableWaitLock(enableWaitLock);
            DeviceReadParams request = new DeviceReadParams(link, requestSize, remainingTime, lockTimeout, terminationCharacter, deviceFlags);
            response = new DeviceReadResponse();
            call(client.coreChannel, Channels.Core.Options.DEVICE_READ, request, response, remainingTime);
            response.getError().checkErrorThrowException();
            buffer.append(response.getData());
            remainingTime -= (int) (Instant.now().toEpochMilli() - startTime);
        } while (response.noReason());
        return buffer.toByteArray();
    }

    public byte readStb() {
        return readStb(DEFAULT_IO_TIMEOUT);
    }

    public byte readStb(int ioTimeout) {
        return deviceReadStb(ioTimeout, false, 0);
    }

    public byte readStb(int ioTimeout, int lockTimeout) {
        return deviceReadStb(ioTimeout, true, lockTimeout);
    }

    private byte deviceReadStb(int ioTimeout, boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceReadStbResponse response = new DeviceReadStbResponse();
        call(client.coreChannel, Channels.Core.Options.DEVICE_READ_STB, request, response, ioTimeout);
        response.getError().checkErrorThrowException();
        return response.getStb();
    }

    public void trigger(int ioTimeout,boolean enableWaitLock, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_TRIGGER, request, response);
        response.getError().checkErrorThrowException();
    }

    public void clear(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_CLEAR, request, response);
        response.getError().checkErrorThrowException();
    }

    public void remote(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_REMOTE, request, response);
        response.getError().checkErrorThrowException();
    }

    public void local(int ioTimeout, int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceGenericParams request = new DeviceGenericParams(link, ioTimeout, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_LOCAL, request, response);
        response.getError().checkErrorThrowException();
    }

    public void lock(int lockTimeout, boolean enableWaitLock) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(enableWaitLock);
        DeviceLockParams request = new DeviceLockParams(link, lockTimeout, deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_LOCK, request, response);
        response.getError().checkErrorThrowException();
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

    private void call(OncRpcClient channel, int procedureNumber, XdrAble params, XdrAble result, int ioTimeout) {
        int timeout = channel.getTimeout();
        channel.setTimeout(ioTimeout);
        try {
            call(channel, procedureNumber, params, result);
        } finally {
            channel.setTimeout(timeout);
        }
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
