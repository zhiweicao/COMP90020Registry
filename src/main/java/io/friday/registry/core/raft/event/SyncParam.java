package io.friday.registry.core.raft.event;

import java.io.Serializable;

public class SyncParam implements Serializable {
    long startIndex;
    long endIndex;
}
