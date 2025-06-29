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

package cn.enaium.joe.util.task.tasks;

import cn.enaium.joe.gui.panel.search.ResultNode;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.ColorUtil;
import cn.enaium.joe.util.HtmlUtil;
import cn.enaium.joe.util.StringUtil;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Enaium
 * @since 0.10.0
 */
public class SearchMethodTask extends SearchInstructionTask<List<ResultNode>> {

    private final String owner;
    private final String name;
    private final String description;

    private final boolean itf;
    private final Predicate<MethodInsnNode> predicate;

    public SearchMethodTask(Jar jar, Predicate<MethodInsnNode> predicate) {
        super("SearchMethod", jar);
        this.predicate = predicate;
        this.name = null;
        this.owner = null;
        this.description = null;
        this.itf = false;
    }

    public SearchMethodTask(Jar jar, String owner, String name, String description) {
        super("SearchMethod", jar);
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.itf = false;
        this.predicate = (methodInsnNode) ->
            (methodInsnNode.owner.contains(this.owner) || StringUtil.isBlank(this.owner)) &&
                    (methodInsnNode.name.contains(this.name) || StringUtil.isBlank(this.name)) &&
                    (methodInsnNode.desc.contains(this.description) || StringUtil.isBlank(this.description));

    }

    public SearchMethodTask(Jar jar, String owner, String name, String description, boolean itf) {
        super("SearchMethod", jar);
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.itf = itf;
        this.predicate = (methodInsnNode) ->
                (methodInsnNode.owner.contains(this.owner) || StringUtil.isBlank(this.owner)) &&
                        (methodInsnNode.name.contains(this.name) || StringUtil.isBlank(this.name)) &&
                        (methodInsnNode.desc.contains(this.description) || StringUtil.isBlank(this.description)) &&
                        (methodInsnNode.itf || !this.itf);
    }

    @Override
    public List<ResultNode> get() {
        List<ResultNode> resultNodes = new ArrayList<>();
        searchInstruction((classNode, instruction) -> {
            if (instruction instanceof MethodInsnNode methodInsnNode) {
                if (this.predicate.test(methodInsnNode)) {
                    resultNodes.add(new ResultNode(classNode, HtmlUtil.setColor(methodInsnNode.name, ColorUtil.name)
                            + HtmlUtil.setColor(":", ColorUtil.opcode)
                            + HtmlUtil.setColor(methodInsnNode.desc, ColorUtil.desc)
                            + HtmlUtil.setColor(String.valueOf(methodInsnNode.itf), ColorUtil.bool)));
                }
            }
        });
        return resultNodes;
    }
}
