package cn.enaium.joe.config.value;

import cn.enaium.joe.config.Config;
import cn.enaium.joe.config.ConfigManager;
import cn.enaium.joe.dialog.ConfigDialog;
import cn.enaium.joe.util.MessageUtil;
import com.google.gson.JsonElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ConfigValue<T extends Config> extends Value<T>{
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

    public static Component createGui(ConfigValue<?> configValue){
        return new JButton(configValue.getName()) {{
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new ConfigDialog(configValue.getValue()).setVisible(true);
                }
            });
        }};
    }
}
