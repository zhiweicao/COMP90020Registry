package io.friday.registry.core;

import io.friday.registry.common.entity.Address;
import io.friday.registry.common.entity.NotifyListener;
import io.friday.registry.common.entity.RegisterMeta;
import io.friday.registry.common.entity.ServiceMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RegistryService {

    /**
     * Register service to registry server.
     */
    void register(RegisterMeta meta);

    /**
     * Unregister service to registry server.
     */
    void unregister(RegisterMeta meta);

    /**
     * Subscribe a service from registry server.
     */
    //TODO
//    void subscribe(ServiceMeta serviceMeta, NotifyListener listener);

    void subscribe(ServiceMeta serviceMeta, Address address);

    /**
     * Find a service in the local scope.
     */
    Collection<RegisterMeta> lookup(ServiceMeta serviceMeta);

    /**
     * List all consumer's info.
     */
    Map<ServiceMeta, List<Address>> consumers();

    /**
     * List all provider's info.
     * @return
     */
    List<RegisterMeta> providers();

    /**
     * Returns {@code true} if {@link RegistryService} is shutdown.
     */
    boolean isShutdown();

    /**
     * Shutdown.
     */
    void shutdownGracefully();

}
