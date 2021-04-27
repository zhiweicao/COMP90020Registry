package io.friday.registry.core.raft;

import io.friday.registry.common.entity.Address;
import io.netty.channel.Channel;

public interface Cluster {
    void addPeer(Address address, Channel channel);
    void removePeer(Address address);
    void connectPeer(Address address);
    Address getSelfAddress();
}
