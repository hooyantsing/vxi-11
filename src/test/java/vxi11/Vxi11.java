package vxi11;

import xyz.hooy.vxi11.Vxi11Client;
import xyz.hooy.vxi11.Vxi11ClientLink;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Vxi11 {

    void Test() throws UnknownHostException {
        try (Vxi11Client vxi11Client = new Vxi11Client(InetAddress.getByName("127.0.0.1"), 8080);
             Vxi11ClientLink instr = vxi11Client.createLink("instr0", 1000)) {
            int writeCount = instr.write(new byte[0]);
        }
    }
}
