package com.dev.minn.ecommerce.common.domain;

public interface EventHandler<T> {

    Class<T> supports();
    void handle(EventEnvelope<T> event);
}
