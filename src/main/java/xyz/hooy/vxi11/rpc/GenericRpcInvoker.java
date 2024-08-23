package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;

import java.io.IOException;

@FunctionalInterface
public interface GenericRpcInvoker<REQUEST extends XdrAble, RESPONSE extends XdrAble> {

    RESPONSE invoke(REQUEST request) throws OncRpcException, IOException;
}