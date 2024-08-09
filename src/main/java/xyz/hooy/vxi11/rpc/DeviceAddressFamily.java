package xyz.hooy.vxi11.rpc;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

import java.io.IOException;

public class DeviceAddressFamily implements XdrAble {

   public static final int DEVICE_TCP = 0;
   public static final int DEVICE_UDP = 1;

   private int family = DEVICE_TCP;

   public DeviceAddressFamily(){}

   protected DeviceAddressFamily(XdrDecodingStream xdr) throws OncRpcException, IOException {
      xdrDecode(xdr);
   }

   @Override
   public void xdrEncode(XdrEncodingStream xdr) throws OncRpcException, IOException {
      xdr.xdrEncodeInt(family);
   }

   @Override
   public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
      family = xdr.xdrDecodeInt();
   }

   public int getFamily() {
      return family;
   }
}
