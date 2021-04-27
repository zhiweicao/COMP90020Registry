package io.friday.registry.core.raft.handler;

import io.friday.registry.core.entity.Address;
import io.friday.registry.core.raft.RaftNode;
import io.friday.registry.core.raft.event.HeartbeatMessage;
import io.friday.registry.core.raft.entity.RaftPeerMessage;
import io.friday.registry.core.raft.event.VoteParam;
import io.friday.registry.core.raft.event.VoteResult;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class RaftPeerMessageHandler extends SimpleChannelInboundHandler<Object> {
    private RaftNode raftNode;

    public RaftPeerMessageHandler(RaftNode raftNode) {
        this.raftNode = raftNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RaftPeerMessage) {
            RaftPeerMessage raftPeerMessage = (RaftPeerMessage) msg;
            System.out.println("收到信息: " + raftPeerMessage);

            switch (raftPeerMessage.getType()) {
                case connect:
                    break;
                case join:
                    addChannel(ctx, raftPeerMessage);
                    break;
                case joinResponse:
                    addChannelOnSender(ctx,raftPeerMessage);
                    break;
                case vote:
                    handleVote(ctx, raftPeerMessage);
                    break;
                case voteResponse:
                    handleVoteResponse(ctx, raftPeerMessage);
                    break;
                case heartbeat:
                    handleHeartbeat(ctx, raftPeerMessage);
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


    public void addChannel(ChannelHandlerContext ctx, RaftPeerMessage raftPeerMessage) {
        if (raftPeerMessage.getMessage() instanceof Address) {
            Address address = (Address)raftPeerMessage.getMessage();
            raftNode.addPeer(address, ctx.channel());

            raftPeerMessage.setType(RaftPeerMessage.RaftPeerMessageType.joinResponse);
            raftPeerMessage.setMessage(raftNode.getSelfAddress());
            ctx.writeAndFlush(raftPeerMessage);                 //响应发送者
        }
    }

    public void addChannelOnSender(ChannelHandlerContext ctx, RaftPeerMessage raftPeerMessage) {
        if (raftPeerMessage.getMessage() instanceof Address) {
            Address address = (Address)raftPeerMessage.getMessage();
            raftNode.addPeer(address, ctx.channel());
        }
    }

    public void handleVote(ChannelHandlerContext ctx, RaftPeerMessage raftPeerMessage) {
        if (raftPeerMessage.getMessage() instanceof VoteParam) {
            VoteParam voteParam = (VoteParam) raftPeerMessage.getMessage();
            VoteResult voteResult = raftNode.handleRequestVote(voteParam);
            RaftPeerMessage voteResultMessage = new RaftPeerMessage(voteResult, RaftPeerMessage.RaftPeerMessageType.voteResponse);
            ctx.writeAndFlush(voteResultMessage);
        }
    }

    public void handleVoteResponse(ChannelHandlerContext ctx, RaftPeerMessage raftPeerMessage) {
        if (raftPeerMessage.getMessage() instanceof VoteResult) {
            VoteResult voteResult = (VoteResult) raftPeerMessage.getMessage();
            raftNode.handleVoteResult(voteResult);
        }
    }

    public void handleHeartbeat(ChannelHandlerContext ctx, RaftPeerMessage raftPeerMessage) {
        if (raftPeerMessage.getMessage() instanceof HeartbeatMessage) {
            HeartbeatMessage heartbeatMessage = (HeartbeatMessage) raftPeerMessage.getMessage();
            raftNode.handleHeartbeat(heartbeatMessage);
        }
    }
}
