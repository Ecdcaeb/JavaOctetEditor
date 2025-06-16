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

import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.classes.ClassNode;
import cn.enaium.joe.util.task.AbstractTask;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.BiConsumer;

/**
 * @author Enaium
 * @since 0.10.0
 */
public abstract class SearchInstructionTask<T> extends AbstractTask<T> {

    private final Jar jar;

    public SearchInstructionTask(String name, Jar jar) {
        super(name);
        this.jar = jar;
    }

    public void searchInstruction(BiConsumer<ClassNode, AbstractInsnNode> consumer) {
        int totalClasses = jar.getClassSize();
        int processedClasses = 0;
        int lastProgress = -1;

        for (ClassNode classNode : jar.getClasses()) {
            for (MethodNode method : classNode.getMethods()) {
                for (AbstractInsnNode instruction : method.instructions) {
                    consumer.accept(classNode, instruction);
                }
            }

            processedClasses++;
            int currentProgress = (processedClasses * 100) / totalClasses;
            if (currentProgress > lastProgress) {
                setProgress(currentProgress);
                lastProgress = currentProgress;
            }
        }
    }
}
