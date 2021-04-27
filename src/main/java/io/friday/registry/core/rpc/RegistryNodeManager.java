package io.friday.registry.core.rpc;

import io.friday.registry.core.entity.Address;
import io.netty.channel.Channel;

public interface RegistryNodeManager {
    void addNeighbour(Address address, Channel channel);
    void removeNeighbour(Address address);
    void listNeighbour();
}
