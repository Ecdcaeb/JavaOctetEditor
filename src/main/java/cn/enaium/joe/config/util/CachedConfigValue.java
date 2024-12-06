package cn.enaium.joe.config.util;

import cn.enaium.joe.config.value.ConfigValueListener;
import cn.enaium.joe.config.value.Value;

import java.util.function.BiFunction;

public class CachedConfigValue<T, E> implements ConfigValueListener<E> {

    protected T value;
    protected BiFunction<E, E, T> processor;

    public CachedConfigValue(BiFunction<E, E, T> processor){
        this.processor = processor;
        this.value = null;
    }

    @Override
    public void update(Value<E> instance, E oldValue, E newValue) {
        this.value = this.processor.apply(oldValue, newValue);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value){
        this.value = value;
    }
}
