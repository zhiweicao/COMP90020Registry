package io.friday.registry.transport.entity;

import io.friday.registry.common.entity.Address;
import lombok.Data;

import java.io.Serializable;

@Data
public class TransportMessage implements Serializable {
    Address address;
    TransportType type;

    public enum TransportType {
        connect
    }
}
