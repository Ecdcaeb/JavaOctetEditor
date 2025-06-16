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

package cn.enaium.joe.util.config.value;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Enaium
 * @since 0.9.0
 */
public final class StringSetValue extends Value<Set<String>>{
    public static final Type TYPE = TypeToken.getParameterized(Set.class, String.class).getType();//<Set<String>>().getType();
    public StringSetValue(String name, Set<String> value, String description) {
        super(TYPE, name, value, description);
    }

    @Override
    public void decode(JsonElement jsonElement) {
        Set<String> strings = new HashSet<>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            strings.add(element.getAsString());
        }
        this.setValue(strings);
    }
}
