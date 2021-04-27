package io.friday.registry.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PeerMessage implements Serializable {
    private static final long serialVersionUID = 12359L;
    private Message message;
    private PeerMessageType type;

    public PeerMessage(PeerMessageType type) {
        this.type = type;
    }

    public enum PeerMessageType {
        Join,
        JoinResponse,
        Normal,
        Heartbeat
    }

}
