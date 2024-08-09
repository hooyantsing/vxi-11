package xyz.hooy.vxi11.rpc;
import org.acplt.oncrpc.*;

import java.io.IOException;

public class DeviceRemoteFunction implements XdrAble {

    private int hostAddress;

    private int hostPort;

    private int programNumber;

    private int programVersion;

    private DeviceAddressFamily family;

    public DeviceRemoteFunction(int hostAddress, int hostPort, int programNumber, int programVersion) {
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.programNumber = programNumber;
        this.programVersion = programVersion;
        this.family = new DeviceAddressFamily();
    }

    protected DeviceRemoteFunction(XdrDecodingStream xdr) throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(hostAddress);
        xdr.xdrEncodeInt(hostPort);
        xdr.xdrEncodeInt(programNumber);
        xdr.xdrEncodeInt(programVersion);
        family.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr) throws OncRpcException, IOException {
        hostAddress = xdr.xdrDecodeInt();
        hostPort = xdr.xdrDecodeInt();
        programNumber = xdr.xdrDecodeInt();
        programVersion = xdr.xdrDecodeInt();
        family = new DeviceAddressFamily(xdr);
    }
}
