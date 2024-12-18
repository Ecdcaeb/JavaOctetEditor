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

package cn.enaium.joe.event;

import cn.enaium.joe.util.Pair;
import cn.enaium.joe.util.Util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author Enaium
 * @since 1.2.0
 */
public class EventManager {
    List<Pair<Class<? extends Event>, Consumer<? extends Event>>> listeners = new CopyOnWriteArrayList<>();

    public <T extends Event>void register(Class<T> listener, Consumer<T> consumer) {
        listeners.add(new Pair<>(listener,  consumer));
    }

    public void call(Event event) {
        listeners.stream().filter(it -> event.getClass().isAssignableFrom(it.getKey())).forEach(it -> it.getValue().accept(Util.cast(event)));
    }
}
