package cn.enaium.joe.config.value;

import javax.swing.KeyStroke;

public class KeyValue extends Value<KeyStroke> {
    public KeyValue(String name, KeyStroke value, String description) {
        super(KeyStroke.class, name, value, description);
    }
}
