package vxi11;

import xyz.hooy.vxi11.Vxi11Client;
import xyz.hooy.vxi11.Vxi11VisaManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Vxi11 {

    void visa() throws UnknownHostException {
        try (Vxi11Client.Link instr0 = Vxi11VisaManager.newSession("TCPIP0::192.168.1.100::inst0::INSTR")) {
            instr0.writeString("*IDN?");
            String read = instr0.readString('\n');
            System.out.println(read);
        }
    }

    void vxi11() throws UnknownHostException {
        Vxi11Client client = new Vxi11Client(InetAddress.getByName("192.168.1.100"));
        try (Vxi11Client.Link instr0 = client.createLink("instr0")) {
            instr0.writeString("*IDN?");
            String read = instr0.readString('\n');
            System.out.println(read);
        }
    }
}
