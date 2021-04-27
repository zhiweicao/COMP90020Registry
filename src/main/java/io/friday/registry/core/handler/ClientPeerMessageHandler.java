package io.friday.registry.core.handler;

import io.friday.registry.core.entity.PeerMessage;
import io.friday.registry.core.entity.Address;
import io.friday.registry.core.rpc.RegistryNodeManager;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientPeerMessageHandler extends SimpleChannelInboundHandler<Object> {
    private RegistryNodeManager registryNodeManager;

    public ClientPeerMessageHandler(RegistryNodeManager registryNodeManager) {
        this.registryNodeManager = registryNodeManager;
    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        Channel inComing = ctx.channel();
//        System.out.println("已连接: "+ inComing.remoteAddress());
//    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PeerMessage) {
            PeerMessage peerMessage = (PeerMessage) msg;
            System.out.println("收到信息: " + peerMessage);

            switch (peerMessage.getType()) {
                case JoinResponse:
                    addChannel(ctx, ctx.channel().remoteAddress());
                    break;
            }
        }
    }
    public void addChannel(ChannelHandlerContext ctx,  SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        Address address = new Address(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        registryNodeManager.addNeighbour(address, ctx.channel());
    }

}
