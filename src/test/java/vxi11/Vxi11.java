package vxi11;

import xyz.hooy.vxi11.Vxi11Client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Vxi11 {

    void Test() throws UnknownHostException {
        try (Vxi11Client client = new Vxi11Client(InetAddress.getByName("127.0.0.1"));
             Vxi11Client.Link instr0 = client.createLink("instr0")) {
            instr0.enableServiceRequest();
            instr0.addServiceRequestListener(() -> {
                byte statusByte = instr0.readStatusByte();
                System.out.println(statusByte);
            });
            instr0.writeString("*IDN?");
            String read = instr0.readString('\n');
            System.out.println(read);
        }
    }
}
