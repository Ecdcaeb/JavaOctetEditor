package cn.enaium.joe.config.value;

import cn.enaium.joe.config.Config;
import cn.enaium.joe.config.ConfigManager;
import cn.enaium.joe.util.MessageUtil;
import com.google.gson.JsonElement;

public class ConfigValue<T extends Config> extends Value<T>{
    public ConfigValue(T config, String description){
        super(config.getClass(), config.getName(), config, description);
    }

    @Override
    public void decode(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            ConfigManager.decodeConfig(this.getValue(), jsonElement.getAsJsonObject());
        } else {
            MessageUtil.error("Could Not decode config for " + jsonElement + " as " + this.getName() + "@" + this.getType().getTypeName());
        }
    }
}
