package io.friday.registry.core.entity;

import io.friday.registry.raft.entity.Command;

public class RegistryCommand implements Command {

    public enum CommandType{
        register,
        unregister,
        subscript,
    }
}
