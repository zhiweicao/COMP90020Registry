package io.friday.registry.core.rpc.impl;

import io.friday.registry.core.entity.Address;
import io.friday.registry.core.rpc.RegistryNodeManager;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.concurrent.ConcurrentHashMap;


public class RegistryNodeManagerImpl implements RegistryNodeManager {
    private long nodeId;
    private ConcurrentHashMap<Address, Channel> neighbourInfo;
    private ChannelGroup neighboursChannel;
    private static RegistryNodeManager registryNodeManager;

    public RegistryNodeManagerImpl(int Id) {
        this.nodeId = Id;
        neighbourInfo = new ConcurrentHashMap<>();
        neighboursChannel = new DefaultChannelGroup("neighbours", GlobalEventExecutor.INSTANCE);
        registryNodeManager = this;
    }

    public void addNeighbour(Address address, Channel channel) {
        neighbourInfo.put(address, channel);
    }

    public void removeNeighbour(Address address) {
        neighbourInfo.remove(address);
    }


    public void listNeighbour() {
        System.out.println("打印Neighbours.");
        neighbourInfo.forEach((k, v) -> System.out.println(k.getHost() + ":"+  k.getPort()));
    }
    public static RegistryNodeManager getInstance() {
        return registryNodeManager;
    }
}
