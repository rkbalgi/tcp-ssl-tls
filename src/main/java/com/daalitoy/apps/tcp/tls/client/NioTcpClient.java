package com.daalitoy.apps.tcp.tls.client;

import com.daalitoy.apps.tcp.tls.ChannelHandler;
import com.daalitoy.apps.tcp.tls.IoHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by ragha on 5/8/2016.
 */
public class NioTcpClient {

    private String host;
    private int port;
    private SocketChannel s;

    public  NioTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {

        if ((s == null) || !s.isConnected()) {
            s = SocketChannel.open(new InetSocketAddress(host, port));
            s.configureBlocking(false);
            Selector selector = Selector.open();
            s.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            ChannelHandler.register(selector,new IoHandler(-2));
        } else {
            throw new IllegalStateException("socket already connected.");
        }
    }

    public void write(int adjust, byte[] data) throws IOException {
        if (s != null) {
            s.write(ByteBuffer.wrap(data));
        } else {
            throw new IllegalStateException("socket not connected");
        }


    }
}
