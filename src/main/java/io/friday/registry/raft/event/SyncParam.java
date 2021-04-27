package io.friday.registry.raft.event;

import java.io.Serializable;

public class SyncParam implements Serializable {
    long startIndex;
    long endIndex;
}
