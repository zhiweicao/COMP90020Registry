package io.friday.registry.core.handler;

import io.friday.registry.core.entity.PeerMessage;
import io.friday.registry.core.entity.Address;
import io.friday.registry.core.rpc.RegistryNodeManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class PeerMessageHandler extends SimpleChannelInboundHandler<Object> {
    private RegistryNodeManager registryNodeManager;

    public PeerMessageHandler(RegistryNodeManager registryNodeManager) {
        this.registryNodeManager = registryNodeManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PeerMessage) {
            PeerMessage peerMessage = (PeerMessage) msg;
            System.out.println("收到信息: " + peerMessage);

            switch (peerMessage.getType()) {
                case Join:
                    addChannel(ctx, peerMessage);
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 离线");
    }

    public void addChannel(ChannelHandlerContext ctx, PeerMessage peerMessage) {
        Address address = new Address(peerMessage.getMessage().getData());
        registryNodeManager.addNeighbour(address, ctx.channel());
        peerMessage.setType(PeerMessage.PeerMessageType.JoinResponse);
        ctx.writeAndFlush(peerMessage);                                                     //返回成功响应
    }
}
