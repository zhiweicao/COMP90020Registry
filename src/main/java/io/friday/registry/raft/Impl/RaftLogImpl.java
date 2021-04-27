package io.friday.registry.raft.Impl;

import io.friday.registry.raft.RaftLog;
import io.friday.registry.raft.StateMachine;
import io.friday.registry.raft.entity.LogEntry;

import java.util.ArrayList;
import java.util.List;

public class RaftLogImpl implements RaftLog {
    private final List<LogEntry> logEntries;
    private final StateMachine stateMachine;

    public RaftLogImpl(StateMachine stateMachine) {
        logEntries = new ArrayList<>();
        this.stateMachine = stateMachine;
    }

    @Override
    public void apply(LogEntry logEntry) {
        logEntries.add(logEntry);
        stateMachine.apply(logEntry.getCommand());
    }

    @Override
    public List<LogEntry> sync(long startIndex, long endIndex) {
        ArrayList<LogEntry> res = new ArrayList<>();
        for(int i = 0; i < endIndex; i++) {
            res.add(logEntries.get(i));
        }

        return res;
    }

}
