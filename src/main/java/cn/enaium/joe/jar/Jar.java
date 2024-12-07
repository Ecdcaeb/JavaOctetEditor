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

package cn.enaium.joe.jar;

import cn.enaium.joe.util.classes.ClassNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Enaium
 */
public class Jar {
    public Map<String, ClassNode> classes = new LinkedHashMap<>();
    public Map<String, byte[]> resources = new LinkedHashMap<>();

    public Jar copy() {
        Jar jar = new Jar();
        jar.classes.putAll(classes);
        jar.resources.putAll(resources);
        return jar;
    }
}
