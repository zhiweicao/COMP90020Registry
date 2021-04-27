package io.friday.registry.raft;

import io.friday.registry.raft.entity.Command;

public interface StateMachine {
    void apply(Command command);
}
