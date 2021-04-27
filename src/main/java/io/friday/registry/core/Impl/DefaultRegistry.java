package io.friday.registry.core.Impl;

import io.friday.registry.common.entity.Address;
import io.friday.registry.common.entity.LifeCycle;
import io.friday.registry.core.RegistryService;
import io.friday.registry.raft.Impl.DefaultRaftNode;
import io.friday.registry.raft.RaftNode;
import io.friday.registry.transport.Impl.DefaultTransportNode;
import io.friday.registry.transport.TransportNode;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultRegistry implements LifeCycle {
    private final String host;
    private final int port;

    private RegistryService registryService;
    private RaftNode raftNode;
    private TransportNode transportNode;

    public DefaultRegistry(String host, int port) {
        this.host = host;
        this.port = port;
        this.transportNode = new DefaultTransportNode(host, port);
        raftNode = new DefaultRaftNode(host, port, this.transportNode);
    }

    @Override
    public void init() {
        transportNode.init();
        raftNode.init();
    }

    @Override
    public void start() {
        transportNode.start();
        raftNode.start();
    }

    @Override
    public void stop() {

    }
}
