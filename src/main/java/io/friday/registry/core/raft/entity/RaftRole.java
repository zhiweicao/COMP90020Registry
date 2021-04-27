package io.friday.registry.core.raft.entity;

import java.io.Serializable;

public enum RaftRole implements Serializable {
    leader,
    follower,
    candidate
}
