package io.friday.registry.core.Impl;

import io.friday.registry.common.entity.Address;
import io.friday.registry.common.entity.NotifyListener;
import io.friday.registry.common.entity.RegisterMeta;
import io.friday.registry.common.entity.ServiceMeta;
import io.friday.registry.core.RegistryService;
import io.friday.registry.raft.StateMachine;
import io.friday.registry.raft.entity.Command;

import java.util.*;

public class DefaultRegistryService implements RegistryService {
    private final List<RegisterMeta> providers;
    private Map<ServiceMeta, List<Address>> consumers;
    private final Map<ServiceMeta, List<RegisterMeta>> serviceMap;

    public DefaultRegistryService() {
        providers = new ArrayList<>();
        consumers = new HashMap<>();
        serviceMap = new HashMap<>();
    }

    @Override
    public void register(RegisterMeta meta) {
        if (!providers.contains(meta)) {
            providers.add(meta);
        }

        if (!serviceMap.containsKey(meta.getServiceMeta())) {
            serviceMap.put(meta.getServiceMeta(), new ArrayList<>());
        }

        List<RegisterMeta> providerList = serviceMap.get(meta.getServiceMeta());
        providerList.add(meta);
    }

    @Override
    public void unregister(RegisterMeta meta) {

    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, Address address) {
        if (!consumers.containsKey(serviceMeta)) {
            consumers.put(serviceMeta, new ArrayList<>());
        }
        List<Address> serviceConsumer = consumers.get(serviceMeta);
        serviceConsumer.add(address);
    }

    @Override
    public Collection<RegisterMeta> lookup(ServiceMeta serviceMeta) {
        return serviceMap.get(serviceMeta);
    }

    @Override
    public Map<ServiceMeta, List<Address>> consumers() {
        return consumers;
    }

    @Override
    public List<RegisterMeta> providers() {
        return providers;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public void shutdownGracefully() {

    }

}
