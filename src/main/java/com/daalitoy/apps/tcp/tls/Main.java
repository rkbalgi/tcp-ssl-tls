package com.daalitoy.apps.tcp.tls;

import com.daalitoy.apps.tcp.tls.server.NioTcpServer;
import com.daalitoy.apps.tcp.tls.server.ServerIoHandler;

public class Main {



    public static void main(String[] args) throws InterruptedException {

        NioTcpServer nioTcpServer = new NioTcpServer(8876);
        nioTcpServer.setHandler(new ServerIoHandler(2));
        nioTcpServer.start();
        Thread.sleep(Integer.MAX_VALUE);
        nioTcpServer.stop();

    }
}
