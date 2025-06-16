package cn.enaium.joe.util.config.util;

import cn.enaium.joe.util.config.value.Value;

@FunctionalInterface
public interface ConfigValueListener<T> {
    void update(Value<T> instance, T oldValue, T newValue);
}
