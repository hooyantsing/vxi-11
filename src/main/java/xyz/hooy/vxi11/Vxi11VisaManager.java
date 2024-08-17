package xyz.hooy.vxi11;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Vxi11VisaManager {

    private final static Map<InetAddress, Vxi11Client> clients = new HashMap<>();

    private static final Pattern pattern = Pattern.compile("TCPIP(?<board>[0-9]*)::(?<host>[^:]+)(::?<instrument>[^:]+)?(::INSTR)?");

    private Vxi11VisaManager(){}

    public static Vxi11Client.Link newSession(String resource) throws UnknownHostException {
        Matcher matcher = pattern.matcher(resource);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("");
        }
        String host = matcher.group("host");
        String instrument = matcher.group("instrument");
        InetAddress address = InetAddress.getByName(host);
        Vxi11Client client = clients.computeIfAbsent(address, c -> new Vxi11Client(address));
        return client.createLink(instrument);
    }
}
