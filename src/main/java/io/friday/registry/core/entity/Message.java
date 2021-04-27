package io.friday.registry.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@ToString
public class Message implements Serializable {
    private static final long serialVersionUID = 78532L;

    private long indexFrom;

    private long indexTo;

    private String data;
}
