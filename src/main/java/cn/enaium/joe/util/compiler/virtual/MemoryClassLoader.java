package cn.enaium.joe.util.compiler.virtual;

import cn.enaium.joe.util.classes.ASMClassLoader;

import java.util.Map;

public class MemoryClassLoader extends ASMClassLoader {
    final Map<String, byte[]> classes;

    public MemoryClassLoader(ClassLoader parent, Map<String, byte[]> classes){
        super(parent);
        if (classes == null) classes = Map.of();
        this.classes = classes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classes.containsKey(name)){
            return this.defineClass(name, classes.get(name));
        }
        return super.findClass(name);
    }
}
