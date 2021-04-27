package io.friday.registry.raft.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class VoteResult implements Serializable {
    long term;
    boolean voteGranted;
    public static VoteResult fail(long term) {
        return new VoteResult(term, false);
    }
    public static VoteResult success(long term) {
        return new VoteResult(term, true);
    }
}
