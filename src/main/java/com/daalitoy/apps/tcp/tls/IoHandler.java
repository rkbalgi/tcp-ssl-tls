package com.daalitoy.apps.tcp.tls;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by ragha on 5/8/2016.
 */
public interface IoHandler {
    public void connectionOpened(SocketChannel channel);

    public void exceptionOccured(Throwable t);

    public void msgReceived(SelectableChannel channel, ByteBuffer msgBuf);

    public void dataReceived(SelectableChannel channel);
    public void channelClosed();

}
