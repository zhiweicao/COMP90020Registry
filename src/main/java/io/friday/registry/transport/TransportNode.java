package io.friday.registry.transport;

import io.friday.registry.common.entity.Address;
import io.friday.registry.common.entity.LifeCycle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;

public interface TransportNode extends LifeCycle {
    void send(Address address, Object object);
    void connect(Address address);
    void connect(Address address, ChannelFutureListener[] channelFutureListener);
    void connectedBy(Address address, Channel channel);
    void disconnect(Address address);
    void addHandlerLast(ChannelHandler[] channelHandlers);
}
