package com.daalitoy.apps.tcp.tls;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ragha on 5/8/2016.
 */
public class ChannelEventLoop {

    private static final Logger log = LoggerFactory.getLogger(ChannelEventLoop.class);
    private static final LinkedBlockingQueue<Selector> selectors = new LinkedBlockingQueue<Selector>();
    private static Map<Selector, IoHandler> selectorsMap = Maps.newConcurrentMap();

    static {
        new Thread() {

            public void run() {

                while (true) {
                    Selector selector = null;
                    try {
                        selector = selectors.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    IoHandler handler = selectorsMap.get(selector);
                    //log.debug("running ..." + selector.selectedKeys());
                    try {
                        selector.select();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (SelectionKey key : selector.selectedKeys()) {
                        if (key.isConnectable()) {
                            log.debug("started listening ");
                        } else if (key.isAcceptable()) {
                            log.debug("accepting client - ");
                            try {
                                SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
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
                }
            }


        }.start();
    }


    public synchronized static void register(Selector selector, IoHandler handler) {
        selectors.add(selector);
        selectorsMap.put(selector, handler);
    }
}
