package com.daalitoy.apps.tcp.tls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by ragha on 5/8/2016.
 */
@SuppressWarnings("ALL")
public class IoHandler {

    private static final Logger log = LoggerFactory.getLogger(IoHandler.class);
    private final int adjust;
    private ByteBuffer buf = ByteBuffer.allocate(2048);


    public IoHandler(int adjust) {
        this.adjust = adjust;
    }

    public void connectionOpened(SocketChannel channel) {

        log.info("new connection opened -" + channel.socket().getLocalAddress());

        //lets swrite something

        byte[] responseData = Hex.fromString
                ("0010F1000000000000000000000000F2");

        ByteBuffer buf = ByteBuffer.wrap(responseData);//

        try {
            System.out.println("writing " + responseData.length + " bytes.");
            buf.position(0);
            if (log.isDebugEnabled()) {
                log.debug("Outgoing Buffer - " + Hex.toString(Arrays.copyOfRange(buf.array(), 0, buf
                        .limit())));

            }
            channel.write(buf);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void exceptionOccured(Throwable t) {
        log.error("Exception Occured", t);
    }

    public void dataReceived(SelectableChannel channel) {

        if (channel instanceof SocketChannel) {


            try {
                ((SocketChannel) channel).read(buf);
                buf.position(0);
                int pos = buf.position();
                if (log.isDebugEnabled()) {
                    log.debug("Incoming Buffer - " + Hex.toString(Arrays.copyOfRange(buf.array(), buf.position(), buf
                            .limit())));

                }

                if (buf.remaining() >= 2) {
                    int len = buf.getShort();
                    len+=adjust;
                    if (buf.remaining() >= len) {
                        //read packet
                        byte[] msg = new byte[len];
                        buf.get(msg);
                        msgReceived(channel, ByteBuffer.wrap(msg));


                    } else {
                        //reset
                        buf.position(pos);
                    }
                }

            } catch (Exception e) {
                log.error("error = ",e);
            }


        } else {
            log.error("invalid channel type! " + channel.getClass());
        }

    }

    private void msgReceived(SelectableChannel channel, ByteBuffer msgBuf) {

        SocketChannel socketChannel = (SocketChannel) channel;
        log.debug("Message Received : " + Hex.toString(msgBuf.array()));


        byte[] responseData = Hex.fromString
                ("00000000000000000010");

        short len = (short) responseData.length;
        len += (-adjust);
        ByteBuffer buf = ByteBuffer.allocate(responseData.length + 2);
        buf.putShort(len);
        buf.put(responseData);

        try {
            log.debug("writing " + responseData.length + " bytes.");
            buf.position(0);
            if (log.isDebugEnabled()) {
                log.debug("Outgoing Buffer - " + Hex.toString(Arrays.copyOfRange(buf.array(), 0, buf
                        .limit())));

            }
            socketChannel.write(buf);

        } catch (IOException e) {
            log.error("Exception Caught", e);
        }

    }
}
