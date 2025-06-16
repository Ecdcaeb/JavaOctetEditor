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

import java.util.*;

/**
 * @author Enaium
 */
public class Jar {
    // internal name + ".class" -> ClassNode
    private Map<String, ClassNode> classes = new LinkedHashMap<>();
    private Map<String, byte[]> resources = new LinkedHashMap<>();

    /**
     * @param classNode the classNode
     */
    public void putClassNode(ClassNode classNode){
        classes.put(classNode.getInternalName(), classNode);
    }

    /**
     * @param internalName the internal name, e.g. "java/lang/Object"
     * @return the classNode
     */
    public ClassNode getClassNode(String internalName){
        return classes.get(internalName);
    }

    /**
     * @param internalName the internal name, e.g. "java/lang/Object"
     * @return true if has
     */
    public boolean hasClass(String internalName) {
        return classes.containsKey(internalName);
    }

    /**
     * @return collection of ClassNodes
     */
    public Collection<ClassNode> getClasses(){
        return classes.values();
    }

    public int getClassSize() {
        return this.classes.size();
    }

    /**
     * @return the map of resources. path -> bytes
     * e.g. "assets/lang/en_us.lang" -> null
     */
    public Map<String,byte[]> getResources() {
        return resources;
    }

    /**
     * @param path the path, e.g. "assets/lang/en_us.lang"
     * @param value the bytes, not null
     */
    public void putResource(String path, byte[] value) {
        resources.put(path, value);
    }

    public void putAllResource(Map<String, byte[]> map) {
        resources.putAll(map);
    }

    /**
     * @param path the path, e.g. "assets/lang/en_us.lang"
     * @return the bytes, might null
     */
    public byte[] getResource(String path) {
        return resources.get(path);
    }

    public int getResourceSize(){
        return resources.size();
    }

    public void refresh(){
        Collection<ClassNode> classNodes = getClasses();
        this.classes = new LinkedHashMap<>(classNodes.size());
        for (ClassNode classNode : classNodes) {
            putClassNode(classNode);
        }
    }

    public Jar copy() {
        Jar jar = new Jar();
        for (ClassNode classNode : this.classes.values()) {
            jar.putClassNode(classNode.copy());
        }
        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
            jar.putResource(entry.getKey(), entry.getValue().clone());
        }
        return jar;
    }
}
