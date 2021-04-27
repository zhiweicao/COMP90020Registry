package io.friday.registry.raft;

import io.friday.registry.raft.entity.LogEntry;

import java.util.List;

public interface RaftLog {
    void apply(LogEntry logEntry);
    List<LogEntry> sync(long startIndex, long endIndex);
}
