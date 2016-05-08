package com.daalitoy.apps.tcp.tls.client;

import com.daalitoy.apps.tcp.tls.Hex;

import java.io.IOException;

/**
 * Created by ragha on 5/8/2016.
 */
public class ClientTest {

    public static void main(String[] args) throws IOException {
        NioTcpClient client = new NioTcpClient("localhost", 8876);
        client.setHandler(new ClientIoHandler(2));
        client.connect();
        client.write(2, Hex.fromString("00020304050607080910"));
        client.write(2, Hex.fromString("00020304050607080911"));
    }
}
