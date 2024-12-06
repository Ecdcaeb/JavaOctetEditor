package cn.enaium.joe.config.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.KeyStroke;

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
}
