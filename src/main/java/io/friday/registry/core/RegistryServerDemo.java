package io.friday.registry.core;


import io.friday.registry.core.client.NioClient;
import io.friday.registry.core.entity.Message;
import io.friday.registry.core.entity.PeerMessage;
import io.friday.registry.core.handler.PeerMessageCodec;
import io.friday.registry.core.handler.PeerMessageHandler;
import io.friday.registry.core.server.NioServer;
import io.friday.registry.core.rpc.RegistryNodeManager;
import io.friday.registry.core.rpc.impl.RegistryNodeManagerImpl;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;

public class RegistryServerDemo {
    private NioServer server;
    private ArrayList<NioClient> nioClients;

    private final String host;
    private final int port;
    private RegistryNodeManager registryNodeManager;
    private ChannelHandler[] channelHandlers;

    public RegistryServerDemo(String host, int port) {
        this.host = host;
        this.port = port;
        this.registryNodeManager = new RegistryNodeManagerImpl(port);
        this.nioClients = new ArrayList<>();
    }

    public void init() {
        this.channelHandlers = new ChannelHandler[]{
                new PeerMessageCodec(),
                new PeerMessageHandler(registryNodeManager)
        };

        server = new NioServer(port, channelHandlers);
        System.out.println("创建节点: " + port);
    }

    public void start() {
        server.start();
    }

    public void connect(String host, int port) {
        NioClient nioClient = new NioClient(host, port, channelHandlers);
        nioClients.add(nioClient);
        nioClient.start();
        nioClient.send(this.buildJoinMessage());
    }

    public void list() {
        registryNodeManager.listNeighbour();
    }

    public void close() {
        server.close();
        nioClients.forEach(NioClient::close);
    }

    private PeerMessage buildJoinMessage() {
        Message message = new Message();
        message.setData(host+":" +port);
        return new PeerMessage(message, PeerMessage.PeerMessageType.Join);
    }
}
