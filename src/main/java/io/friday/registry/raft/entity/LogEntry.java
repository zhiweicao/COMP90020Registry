package io.friday.registry.raft.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class LogEntry implements Serializable {
    long index;
    long term;
    Command command;
}
