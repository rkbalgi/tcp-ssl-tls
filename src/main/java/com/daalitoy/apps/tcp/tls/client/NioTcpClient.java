package com.daalitoy.apps.tcp.tls.client;

import com.daalitoy.apps.tcp.tls.ChannelEventLoop;
import com.daalitoy.apps.tcp.tls.Hex;
import com.daalitoy.apps.tcp.tls.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log= LoggerFactory.getLogger(NioTcpClient.class);

    private String host;
    private int port;
    private SocketChannel s;

    public IoHandler getHandler() {
        return handler;
    }

    public void setHandler(IoHandler handler) {
        this.handler = handler;
    }

    private IoHandler handler;

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
            ChannelEventLoop.register(selector,handler);
        } else {
            throw new IllegalStateException("socket already connected.");
        }
    }

    public void write(int adjust, byte[] data) throws IOException {
        if (s != null) {
            ByteBuffer buf=ByteBuffer.allocate(2+data.length);
            buf.putShort((short) (data.length+2));
            buf.put(data);
            buf.rewind();
            log.debug("writing .. "+ Hex.toString(data));
            s.write(buf);
        } else {
            throw new IllegalStateException("socket not connected");
        }


    }
}
