package com.teenspirit.coderunnerhub.containermanager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ContainerPool {

    private final ConcurrentMap<String, Container> pool;
    private final ContainerManager containerManager;

    public ContainerPool(ContainerManager containerManager) {
        this.containerManager = containerManager;
        this.pool = new ConcurrentHashMap<>();
    }

    public Container getContainer(String containerId) {
        return pool.compute(containerId, (s, container) -> {
            if(container == null) {
                container = containerManager.createContainer();
            }
            container.incrementActiveUsages();
            return container;
        });
    }

    public void releaseContainer(String containerId) {
        pool.compute(containerId, (id, container) -> {
            if (container != null) {
                // Уменьшить счетчик использования
                container.decrementActiveUsages();

                // Если счетчик использования достиг нуля, вернуть контейнер в пул
                if (container.getActiveUsages() <= 0) {
                    return null; // контейнер возвращается в пул
                }
            }

            return container;
        });
    }
}
