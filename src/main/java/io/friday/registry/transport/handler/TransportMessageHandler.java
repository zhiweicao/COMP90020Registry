package io.friday.registry.transport.handler;

import io.friday.registry.common.entity.Address;
import io.friday.registry.raft.RaftNode;
import io.friday.registry.raft.entity.RaftPeerMessage;
import io.friday.registry.raft.event.HeartbeatMessage;
import io.friday.registry.raft.event.VoteParam;
import io.friday.registry.raft.event.VoteResult;
import io.friday.registry.transport.TransportNode;
import io.friday.registry.transport.entity.TransportMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class TransportMessageHandler extends SimpleChannelInboundHandler<Object> {
    private TransportNode transportNode;

    public TransportMessageHandler(TransportNode transportNode) {
        this.transportNode = transportNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TransportMessage) {
            TransportMessage transportMessage = (TransportMessage) msg;
            System.out.println("收到信息: " + transportMessage);

            switch (transportMessage.getType()) {
                case connect:
                    handleConnectEvent(ctx, transportMessage);
                    break;
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleConnectEvent(ChannelHandlerContext ctx, TransportMessage transportMessage) {
        transportNode.connectedBy(transportMessage.getAddress(), ctx.channel());
    }

}
