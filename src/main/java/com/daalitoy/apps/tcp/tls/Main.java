package com.daalitoy.apps.tcp.tls;

public class Main {



    public static void main(String[] args) throws InterruptedException {

        NioTcpServer nioTcpServer = new NioTcpServer(-2, 8876);
        nioTcpServer.start();
        Thread.sleep(Integer.MAX_VALUE);
        nioTcpServer.stop();

    }
}
