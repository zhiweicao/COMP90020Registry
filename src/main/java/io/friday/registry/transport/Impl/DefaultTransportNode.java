package io.friday.registry.transport.Impl;

import io.friday.registry.common.entity.Address;
import io.friday.registry.transport.TransportNode;
import io.friday.registry.transport.client.NioClient;
import io.friday.registry.transport.server.NioServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTransportNode implements TransportNode {
    private String host;
    private int port;
    private NioServer server;
    private ArrayList<NioClient> peerClients;
    private final ConcurrentHashMap<Address, Channel> destChannel;
    private ChannelHandler[] channelHandlers;

    public DefaultTransportNode(String host, int port) {
        this.host = host;
        this.port = port;
        this.channelHandlers = new ChannelHandler[0];
        server = new NioServer(port);
        peerClients = new ArrayList<>();
        destChannel = new ConcurrentHashMap<>();

//        ChannelHandler[] transportChannelHandlers = new ChannelHandler[] {
//                new TransportMessageCodec(),
//                new TransportMessageHandler(this)
//        };
//        this.addHandlerLast(transportChannelHandlers);
    }

    @Override
    public void init() {
        server.init();
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void stop() {
        server.close();
        peerClients.forEach(NioClient::close);
    }

    @Override
    public void send(Address address, Object object) {
        Channel channel = destChannel.get(address);
        channel.writeAndFlush(object);
    }

    @Override
    public void connect(Address address) {
        this.connect(address, new ChannelFutureListener[0]);
    }

    @Override
    public void connect(Address address, ChannelFutureListener[] channelFutureListener) {
        NioClient nioClient = new NioClient(address, channelHandlers);
        peerClients.add(nioClient);
        nioClient.start();
        nioClient.addListenerOnChannel(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                destChannel.put(address, future.channel());
            }
        });

        Arrays.stream(channelFutureListener).forEach(nioClient::addListenerOnChannel);
    }

    @Override
    public void connectedBy(Address address, Channel channel) {
        destChannel.put(address, channel);
    }

    @Override
    public void disconnect(Address address) {

    }

    @Override
    public void addHandlerLast(ChannelHandler[] channelHandlers) {
        this.channelHandlers = ArrayUtils.addAll(this.channelHandlers ,channelHandlers);
        server.setChannelHandlers(this.channelHandlers);
    }
}
