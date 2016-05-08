package com.daalitoy.apps.tcp.tls.server;

/**
 * Created by ragha on 5/8/2016.
 */

import com.daalitoy.apps.tcp.tls.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


@SuppressWarnings("ALL")
public class NioTcpServer {

    private static final Logger log = LoggerFactory.getLogger(NioTcpServer.class);
    private final int port;

    public IoHandler getHandler() {
        return handler;
    }

    public void setHandler(IoHandler handler) {
        this.handler = handler;
    }

    private IoHandler handler;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector =
            null;

    private volatile boolean running = true;

    public NioTcpServer(int port) {

        this.port = port;

        try {

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        running = false;


        for (SelectionKey key : selector.keys()) {
            if (key.channel() instanceof SocketChannel) {
                try {
                    key.channel().close();
                } catch (IOException e) {
                    log.error("error closing channel", e);
                }
            }
        }
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            log.error("error closing server socket channel", e);
        }
    }

    public void start() {
        try {
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            new Thread() {

                public void run() {
                    while (running) {
                        //log.debug("running ..." + selector.selectedKeys());
                        try {
                            selector.select();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        for (SelectionKey key : selector.selectedKeys()) {

                            if (!key.channel().isOpen()) {
                                handler.channelClosed();
                                key.cancel();
                                break;

                            }

                            if (key.isConnectable()) {
                                log.debug("started listening ");
                            } else if (key.isAcceptable()) {
                                log.debug("accepting client - ");
                                try {
                                    SocketChannel channel = serverSocketChannel.accept();
                                    channel.configureBlocking(false);
                                    channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                    handler.connectionOpened(channel);
                                } catch (Throwable e) {
                                    handler.exceptionOccured(e);
                                }
                                //selector.selectedKeys().remove(key);
                            } else if (key.isReadable()) {
                                //there is data, so lets attempt to read
                                //selector.selectedKeys().remove(key);

                                if (key.channel().isOpen()) {
                                    handler.dataReceived(key.channel());
                                }


                            } else if (key.isWritable()) {
                                //lets leave this out for now
                                //selector.selectedKeys().remove(key);
                            }

                        }
                        selector.selectedKeys().clear();
                    }//end while

                }


            }.start();

        } catch (Exception e) {
            log.error("error = ", e);
        }

    }


}


