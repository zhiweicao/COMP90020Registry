package io.friday.registry.core.raft;

import io.friday.registry.core.entity.Address;
import io.friday.registry.core.raft.entity.*;
import io.netty.channel.Channel;

public interface RaftNode extends Consensus, Cluster, LifeCycle{
}
