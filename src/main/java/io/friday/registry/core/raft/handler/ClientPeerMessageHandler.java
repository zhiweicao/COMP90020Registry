package io.friday.registry.core.raft.handler;

import io.friday.registry.core.entity.Address;
import io.friday.registry.core.raft.entity.RaftPeerMessage;
import io.friday.registry.core.rpc.RegistryNodeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
        if (msg instanceof RaftPeerMessage) {
            RaftPeerMessage raftPeerMessage = (RaftPeerMessage) msg;
            System.out.println("收到信息: " + raftPeerMessage);

            switch (raftPeerMessage.getType()) {
                case joinResponse:
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
