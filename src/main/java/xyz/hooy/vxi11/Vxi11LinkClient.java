package xyz.hooy.vxi11;

import org.acplt.oncrpc.OncRpcClient;
import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.OncRpcTimeoutException;
import org.acplt.oncrpc.XdrAble;
import xyz.hooy.vxi11.rpc.*;
import xyz.hooy.vxi11.entity.*;
import xyz.hooy.vxi11.exception.*;

public class Vxi11LinkClient implements AutoCloseable {

    private final static int DEFAULT_IO_TIMEOUT = 0; // Not block

    private final static int DEFAULT_LOCK_TIMEOUT = 0;

    private final Vxi11Client client;

    private final DeviceLink link;

    private final int blockSize;

    private volatile boolean closed = false;

    protected Vxi11LinkClient(Vxi11Client client, CreateLinkResponse response) {
        this.client = client;
        this.link = response.getLink();
        this.blockSize = response.getMaxReceiveSize();
    }

    private int deviceWrite(byte[] data, int ioTimeout, int lockTimeout) {
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

    private byte[] deviceRead(byte terminationCharacter, int ioTimeout, int lockTimeout) {
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

    private byte deviceReadStb(int ioTimeout, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
        DeviceGenericParams request = new DeviceGenericParams(link, Math.max(ioTimeout, DEFAULT_IO_TIMEOUT), Math.max(lockTimeout, DEFAULT_LOCK_TIMEOUT), deviceFlags);
        DeviceReadStbResponse response = new DeviceReadStbResponse();
        call(client.coreChannel, Channels.Core.Options.DEVICE_READ_STB, request, response);
        response.getError().checkErrorThrowException();
        return response.getStb();
    }

    private void deviceTrigger(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_TRIGGER, ioTimeout, lockTimeout);
    }

    private void deviceClear(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_CLEAR, ioTimeout, lockTimeout);
    }

    private void deviceRemote(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_REMOTE, ioTimeout, lockTimeout);
    }

    private void deviceLocal(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_LOCAL, ioTimeout, lockTimeout);
    }

    private void deviceLock(int ioTimeout, int lockTimeout) {
        genericRpc(Channels.Core.Options.DEVICE_LOCK, ioTimeout, lockTimeout);
    }

    private void deviceUnlock() {
        DeviceError response = new DeviceError();
        call(client.coreChannel, Channels.Core.Options.DEVICE_UNLOCK, link, response);
        response.getError().checkErrorThrowException();
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

    protected void genericRpc(int options, int ioTimeout, int lockTimeout) {
        DeviceFlags deviceFlags = new DeviceFlags().enableWaitLock(lockTimeout > 0);
        DeviceGenericParams request = new DeviceGenericParams(link, Math.max(ioTimeout, DEFAULT_IO_TIMEOUT), Math.max(lockTimeout, DEFAULT_LOCK_TIMEOUT), deviceFlags);
        DeviceError response = new DeviceError();
        call(client.coreChannel, options, request, response);
        response.getError().checkErrorThrowException();
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
