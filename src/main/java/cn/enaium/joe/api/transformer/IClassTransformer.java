package cn.enaium.joe.api.transformer;

import org.objectweb.asm.tree.ClassNode;

@FunctionalInterface
public interface IClassTransformer {
    boolean transform(ClassNode classNode);
}
