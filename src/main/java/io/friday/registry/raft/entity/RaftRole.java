package io.friday.registry.raft.entity;

import java.io.Serializable;

public enum RaftRole implements Serializable {
    leader,
    follower,
    candidate
}
