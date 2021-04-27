package io.friday.registry.core.raft;

import io.friday.registry.core.raft.event.*;

public interface Consensus {
    VoteResult handleRequestVote(VoteParam param);
    void handleVoteResult(VoteResult voteResult);
    void handleHeartbeat(HeartbeatMessage heartbeatMessage);
    ApplyEntryResult handleAppendEntries(ApplyEntryParam param);
    SyncResult handleSyncRequest(SyncParam syncParam);
}
