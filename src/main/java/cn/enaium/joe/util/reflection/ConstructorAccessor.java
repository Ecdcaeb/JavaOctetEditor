package cn.enaium.joe.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorAccessor<INSTANCE> {

    protected final Constructor<INSTANCE> constructor;

    public ConstructorAccessor(Constructor<INSTANCE> constructor){
        this.constructor = constructor;
        constructor.setAccessible(true);
    }

    public INSTANCE invoke(Object... args){
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public Constructor<INSTANCE> getConstructor() {
        return constructor;
    }
}
