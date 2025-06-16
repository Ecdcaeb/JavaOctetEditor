package cn.enaium.joe.util.reflection;

import java.lang.reflect.Field;

@SuppressWarnings("all")
public record FieldAccessor<T, E>(Field field) {

    public FieldAccessor(Field field){
        field.setAccessible(true);
        this.field = field;
    }

    public T get(E instance){
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(E instance, T value){
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
