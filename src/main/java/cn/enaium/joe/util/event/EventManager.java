/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.joe.util.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author Enaium
 * @since 1.2.0
 */
public class EventManager {

    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> listenerMap =
            new ConcurrentHashMap<>();

    public <T extends Event> void register(Class<T> eventType, Consumer<T> consumer) {
        listenerMap.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(consumer);
    }

    public void call(Event event) {
        for (Map.Entry<Class<? extends Event>, List<Consumer<? extends Event>>> entry :
                listenerMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(event.getClass())) {
                dispatchEvent(event, entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> void dispatchEvent(Event event, List<Consumer<? extends Event>> consumers) {
        T typedEvent = (T) event;
        consumers.forEach(consumer -> {
            Consumer<T> typedConsumer = (Consumer<T>) consumer;
            typedConsumer.accept(typedEvent);
        });
    }

    /**
     * @author Enaium
     * @since 1.2.0
     */
    public interface Event {
    }
}
