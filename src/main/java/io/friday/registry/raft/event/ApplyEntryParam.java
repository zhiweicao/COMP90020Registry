package io.friday.registry.raft.event;

import io.friday.registry.raft.entity.LogEntry;
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
