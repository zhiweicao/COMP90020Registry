package io.friday.registry.raft;

import io.friday.registry.raft.event.*;

public interface Consensus {
    VoteResult handleRequestVote(VoteParam param);
    void handleVoteResult(VoteResult voteResult);
    void handleHeartbeat(HeartbeatMessage heartbeatMessage);
    ApplyEntryResult handleAppendEntries(ApplyEntryParam param);
    SyncResult handleSyncRequest(SyncParam syncParam);
}
