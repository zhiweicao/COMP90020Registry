package io.friday.registry.raft.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RaftPeerMessage implements Serializable {
    private static final long serialVersionUID = 12359L;
    private Object message;
    private RaftPeerMessageType type;

    public RaftPeerMessage(RaftPeerMessageType type) {
        this.type = type;
    }

    public enum RaftPeerMessageType {
        connect,
        join,
        joinResponse,
        vote,
        voteResponse,
        applyEntry,
        applyEntryResponse,
        sync,
        syncResponse,
        heartbeat
    }
}
