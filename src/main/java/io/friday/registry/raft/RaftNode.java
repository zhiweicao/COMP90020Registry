package io.friday.registry.raft;

import io.friday.registry.common.entity.LifeCycle;

public interface RaftNode extends Consensus, Cluster, LifeCycle {
}
