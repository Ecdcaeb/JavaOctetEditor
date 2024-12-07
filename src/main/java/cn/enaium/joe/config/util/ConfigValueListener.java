package cn.enaium.joe.config.util;

import cn.enaium.joe.config.value.Value;

@FunctionalInterface
public interface ConfigValueListener<T> {
    void update(Value<T> instance, T oldValue, T newValue);
}
