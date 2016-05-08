package com.daalitoy.apps.tcp.tls.client;

import com.daalitoy.apps.tcp.tls.Hex;
import com.daalitoy.apps.tcp.tls.server.ServerIoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ragha on 5/8/2016.
 */
public class ClientIoHandler extends ServerIoHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientIoHandler.class);

    public ClientIoHandler(int adjust) {
        super(adjust);
    }


    public void msgReceived(SelectableChannel channel, ByteBuffer msgBuf) {

        SocketChannel socketChannel = (SocketChannel) channel;
        log.debug("Message Received : " + Hex.toString(msgBuf.array()));


    }


}
