package io.friday.registry.raft.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HeartbeatMessage implements Serializable {
    long leaderId;

    long currentTerm;

    long committedLogIndex;

}
