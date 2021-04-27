package io.friday.registry.core.raft.Impl;

import io.friday.registry.core.client.NioClient;
import io.friday.registry.common.entity.Address;
import io.friday.registry.core.raft.RaftMonitor;
import io.friday.registry.core.raft.RaftNode;
import io.friday.registry.core.raft.entity.*;
import io.friday.registry.core.raft.event.*;
import io.friday.registry.core.raft.handler.RaftPeerMessageCodec;
import io.friday.registry.core.raft.handler.RaftPeerMessageHandler;
import io.friday.registry.core.server.NioServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class NodeImpl implements RaftNode, RaftMonitor {
    private static final int ELECTION_INTERVAL = 20*1000;
    private static final int MAXIMUM_HEARTBEAT_TIMEOUT = 15*1000;
    private static final int HEARTBEAT_INTERVAL = 10*1000;

    private final Address nodeAddress;

    private final long nodeId;
    private final AtomicLong leaderId;
    private final AtomicLong currentLogIndex;
    private final AtomicLong currentTerm;
    private final AtomicLong votedFor;
    private volatile RaftRole raftRole;
    private final AtomicLong lastHeartbeatTime;
    private final AtomicLong currentSupport;
    private final AtomicLong latestLogCommit;


    private NioServer server;
    private ArrayList<NioClient> peerClients;
    private final ArrayList<Integer> peers;
    private final ConcurrentHashMap<Integer, Address> peerAddress;
    private final ConcurrentHashMap<Integer, Channel> peerChannel;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ChannelHandler[] channelHandlers;

    public NodeImpl(String host, int port) {
        this.nodeAddress = new Address(host, port);
        this.nodeId = port;
        this.leaderId = new AtomicLong();
        this.currentLogIndex = new AtomicLong();
        this.currentTerm = new AtomicLong();
        this.votedFor = new AtomicLong();
        this.latestLogCommit = new AtomicLong();
        this.raftRole = RaftRole.candidate;
        this.lastHeartbeatTime = new AtomicLong();
        this.currentSupport = new AtomicLong();

        this.peers = new ArrayList<>();
        this.peerClients = new ArrayList<>();
        this.peerChannel = new ConcurrentHashMap<>();
        this.peerAddress = new ConcurrentHashMap<>();
        this.channelHandlers = new ChannelHandler[]{
                new RaftPeerMessageCodec(),
                new RaftPeerMessageHandler(this)
        };

        this.server = new NioServer(port, channelHandlers);

        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ScheduledHeartBeatTask(), 10,1, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ScheduledElectionTask(), 10,1, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ScheduledTimeoutScanner(), 10,1, TimeUnit.SECONDS);
    }


    @Override
    public void start() {
        this.server.start();
    }

    @Override
    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }

    @Override
    public VoteResult handleRequestVote(VoteParam param) {
        if (param.getVoteTerm() < currentTerm.get()) {
            return VoteResult.fail(currentTerm.get());
        } else if (param.getVoteTerm() == currentTerm.get()) {
            if (raftRole.equals(RaftRole.follower)) {
                return VoteResult.fail(currentTerm.get());
            }

            if (votedFor.get() == 0 || votedFor.get() == param.getCandidateId()) {
                raftRole = RaftRole.follower;
                votedFor.set(param.getCandidateId());
                currentTerm.set(param.getVoteTerm());
                return VoteResult.success(currentTerm.get());
            }
        } else if (param.getVoteTerm() > currentTerm.get()) {
            raftRole = RaftRole.follower;
            votedFor.set(param.getCandidateId());
            currentTerm.set(param.getVoteTerm());
            return VoteResult.success(currentTerm.get());
        }
        System.out.println("my VoteFor:" + this.votedFor.get());
        System.out.println("my VoteFor:" + this.raftRole);
        return VoteResult.fail(currentTerm.get());
    }

    @Override
    public void handleVoteResult(VoteResult voteResult) {
        if (voteResult.isVoteGranted() && voteResult.getTerm() == currentTerm.get()) {
            currentSupport.incrementAndGet();
            if (currentSupport.get() > peers.size()/2) {
                raftRole = RaftRole.leader;
                votedFor.set(0);
                currentSupport.set(0);
                becomeLeaderProcedure();
            }
        // Next term beginning
        } else if (currentTerm.get() > voteResult.getTerm()) {
            currentSupport.set(0);
        }
    }

    // heartbeat from leader
    @Override
    public void handleHeartbeat(HeartbeatMessage heartbeatMessage) {
        // The leadership change event
        if (heartbeatMessage.getCurrentTerm() > currentTerm.get()) {
            lastHeartbeatTime.set(getCurrentTime());
            leaderId.set(heartbeatMessage.getLeaderId());
            latestLogCommit.set(heartbeatMessage.getCommittedLogIndex());
            votedFor.set(0);
            currentSupport.set(0);
            //TODO: replication?
        } else if (raftRole.equals(RaftRole.follower) && heartbeatMessage.getLeaderId() == leaderId.get()) {
            lastHeartbeatTime.set(getCurrentTime());
            leaderId.set(heartbeatMessage.getLeaderId());
            latestLogCommit.set(heartbeatMessage.getCommittedLogIndex());
            votedFor.set(0);
            currentSupport.set(0);
        }

        //FIXME: 2 leader?

    }

    private void becomeLeaderProcedure() {
        System.out.println("current Node become leader, broadcast initial");
        System.out.println("becomeLeaderProcedure: " + getCurrentTime() + ": " + lastHeartbeatTime);
        currentTerm.incrementAndGet();
        HeartbeatMessage heartbeatMessage = new HeartbeatMessage(nodeId, currentTerm.get(), currentLogIndex.get());
        RaftPeerMessage heartBeatPeerMessage = new RaftPeerMessage(heartbeatMessage, RaftPeerMessage.RaftPeerMessageType.heartbeat);
        broadcast(heartBeatPeerMessage);
        System.out.println("current Node become leader, broadcast initial message");
    }

    @Override
    public ApplyEntryResult handleAppendEntries(ApplyEntryParam param) {
        if (raftRole.equals(RaftRole.leader)) {

        } else {

        }
        return null;
    }

    @Override
    public SyncResult handleSyncRequest(SyncParam syncParam) {
        if (raftRole.equals(RaftRole.leader)) {

        } else {

        }
        return null;
    }

    @Override
    public Address getSelfAddress() {
        return nodeAddress;
    }


    @Override
    public synchronized void addPeer(Address address, Channel channel) {
        int id = address.getPort();
        peers.add(id);
        peerAddress.put(id, address);
        peerChannel.put(id, channel);
    }

    @Override
    public synchronized void connectPeer(Address address) {
        NioClient nioClient = new NioClient(address, channelHandlers);
        peerClients.add(nioClient);
        nioClient.start();
        nioClient.addListenerOnChannel(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                nioClient.send(new RaftPeerMessage(getSelfAddress(), RaftPeerMessage.RaftPeerMessageType.join));
            }
        });
    }

    @Override
    public synchronized void removePeer(Address address) {
        peers.remove(address.getPort());
        peerAddress.remove(address.getPort());
        peerChannel.remove(address.getPort());
    }
    private void broadcast(RaftPeerMessage message) {
        for (int peer: peers) {
            Channel channel = peerChannel.get(peer);
            channel.writeAndFlush(message);
        }
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(50);
    }

    @Override
    public void listPeer() {
        peers.forEach(System.out::println);
    }

    class ScheduledHeartBeatTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!raftRole.equals(RaftRole.leader)) {
                    return;
                }
                long currentTime = getCurrentTime();

                if (currentTime - lastHeartbeatTime.get() < HEARTBEAT_INTERVAL) {
                    return;
                }
                HeartbeatMessage heartbeatMessage = new HeartbeatMessage(nodeId, currentTerm.get(), currentLogIndex.get());
                RaftPeerMessage heartBeatPeerMessage = new RaftPeerMessage(heartbeatMessage, RaftPeerMessage.RaftPeerMessageType.heartbeat);
                broadcast(heartBeatPeerMessage);
                lastHeartbeatTime.set(currentTime);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    class ScheduledElectionTask implements Runnable {
        @Override
        public void run() {
            try {
                if (raftRole.equals(RaftRole.leader)) {
                    return;
                }

                if (!raftRole.equals(RaftRole.candidate) && votedFor.get() == 0) {
                    return;
                }

                long currentTime = getCurrentTime();
                if (currentTime - lastHeartbeatTime.get() < ELECTION_INTERVAL) {
                    return;
                }
                // ask vote for himself
                votedFor.set(nodeId);
                lastHeartbeatTime.set(currentTime + ThreadLocalRandom.current().nextInt(200));
                currentTerm.incrementAndGet();

                VoteParam voteParam = new VoteParam(nodeId, currentLogIndex.get(), currentTerm.get());
                RaftPeerMessage voteMessage = new RaftPeerMessage(voteParam, RaftPeerMessage.RaftPeerMessageType.vote);
                broadcast(voteMessage);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    class ScheduledTimeoutScanner implements Runnable {

        @Override
        public void run() {
            try {
                if (raftRole.equals(RaftRole.follower)) {
                    long currentTime = getCurrentTime();
                    if (currentTime - lastHeartbeatTime.get() > MAXIMUM_HEARTBEAT_TIMEOUT) {
                        System.out.println("Leader Timeout");
                        System.out.println("ScheduledTimeoutScanner: " + currentTime + ": " + lastHeartbeatTime);
                        raftRole = RaftRole.candidate;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
