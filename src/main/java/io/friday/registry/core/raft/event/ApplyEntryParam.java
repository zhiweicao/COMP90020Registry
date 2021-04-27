package io.friday.registry.core.raft.event;

import io.friday.registry.core.raft.entity.LogEntry;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyEntryParam implements Serializable {
    long leaderId;

    long prevLogIndex;

    long preLogTerm;

    LogEntry[] entries;

    long leaderCommit;
}
