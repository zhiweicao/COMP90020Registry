package io.friday.registry.core.raft.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class VoteParam implements Serializable {
    long candidateId;

    long lastLogIndex;

    long voteTerm;
}
