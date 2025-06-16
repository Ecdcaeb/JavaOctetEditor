package cn.enaium.joe.util.config.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class KeyValue extends Value<KeyStroke> {
    public KeyValue(String name, KeyStroke value, String description) {
        super(KeyStroke.class, name, value, description);
    }

    @Override
    public void decode(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()){
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            this.setValue(KeyStroke.getKeyStroke(jsonObject.get("keyCode").getAsInt(), jsonObject.get("modifiers").getAsInt(), jsonObject.get("onKeyRelease").getAsBoolean()));
        } else {
            this.setValue(KeyStroke.getKeyStroke(jsonElement.getAsString()));
        }
    }

    public static Component createGui(final KeyValue keyValue){
        return new JButton(keyValue.getValue().toString()) {{
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() != 65535) {
                        KeyStroke newKey = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiersEx());
                        keyValue.setValue(newKey);
                        setText(newKey.toString());
                    }
                }
            });
        }};
    }
}
