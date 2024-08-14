package vxi11;

import xyz.hooy.vxi11.Vxi11Client;
import xyz.hooy.vxi11.Vxi11ClientLink;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Vxi11 {

    void Test() throws UnknownHostException {
        try (Vxi11Client client = new Vxi11Client(InetAddress.getByName("127.0.0.1"), 8080,1234);
             Vxi11ClientLink link = client.createLink("instr0")) {
            client.setCharset(StandardCharsets.UTF_8.name());
            client.setTimeout(3000);
            link.enableServiceRequest();
            link.addServiceRequestListener(l -> {
                byte statusByte = l.readStatusByte();
                System.out.println(statusByte);
            });
            link.writeString("*IDN?");
            String read = link.readString('\n');
            System.out.println(read);
        }
    }
}
