package vxi11;

import org.junit.jupiter.api.Test;
import xyz.hooy.vxi11.Vxi11Client;
import xyz.hooy.vxi11.Vxi11VisaManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Vxi11Tests {

    private final String host = "192.168.1.100";

    private final String instrument = "inst0";

    @Test
    void visa() throws UnknownHostException {
        try (Vxi11Client.Link instr0 = Vxi11VisaManager.newSession("TCPIP0::" + host + "::" + instrument + "::INSTR")) {
            instr0.writeString("*IDN?");
            String read = instr0.readString('\n');
            System.out.println(read);
        }
    }

    @Test
    void vxi11() throws UnknownHostException {
        Vxi11Client client = new Vxi11Client(InetAddress.getByName(host));
        try (Vxi11Client.Link instr0 = client.createLink(instrument)) {
            instr0.writeString("*IDN?");
            String read = instr0.readString('\n');
            System.out.println(read);
        }
    }

    @Test
    void stb() throws UnknownHostException {
        Vxi11Client client = new Vxi11Client(InetAddress.getByName(host));
        try (Vxi11Client.Link instr0 = client.createLink(instrument)) {
            instr0.enableServiceRequest();
            instr0.addServiceRequestListener(
                    statusByte -> System.out.println(statusByte.getStatus(0))
            );
        }
    }
}
