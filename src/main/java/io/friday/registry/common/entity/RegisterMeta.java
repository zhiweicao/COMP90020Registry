package io.friday.registry.common.entity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode
public class RegisterMeta {
    private Address address;
    private ServiceMeta serviceMeta;
}