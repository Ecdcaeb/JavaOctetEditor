package cn.enaium.joe.util.classes;

public class ASMClassLoader extends ClassLoader{
    public Class<?> defineClass(String canonicalName, byte[] clazz){
        return defineClass(canonicalName, clazz, 0, clazz.length);
    }

}
