package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.*;
import xyz.hooy.vxi11.entity.ErrorCode;
import xyz.hooy.vxi11.exception.Vxi11Exception;

import java.io.IOException;

public class DeviceErrorCode implements XdrAble {

    private int errorId;

    protected DeviceErrorCode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
        xdr.xdrEncodeInt(errorId);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        errorId = xdr.xdrDecodeInt();
    }

    public int getErrorId() {
        return errorId;
    }

    public void checkErrorThrowException() {
        if (errorId != ErrorCode.NO_ERROR) {
            throw new Vxi11Exception(errorId);
        }
    }
}