package cn.enaium.joe.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodAccessor<RETURN, INSTANCE> {
    private final Method method;
    public MethodAccessor(Method method){
        method.setAccessible(true);
        this.method = method;
    }

    @SuppressWarnings("unchecked")
    public RETURN invoke(INSTANCE instance, Object... args){
        if (method.getReturnType() == Void.TYPE){
            try {
                method.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }else {
            try {
                return (RETURN) method.invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Method getMethod() {
        return method;
    }
}
