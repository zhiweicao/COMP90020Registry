package io.friday.registry.core.raft;

import io.friday.registry.core.raft.entity.LogEntry;

public interface RaftLog {
    void apply(LogEntry logEntry);
    void sync(long startIndex, long endIndex);
    LogEntry get(String key);
}
