package cn.enaium.joe.util.event.events;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.util.event.EventManager;

public record EditSaveSuccessEvent(String classInternalName) implements EventManager.Event {
    public static void trigger(String classInternalName){
        JavaOctetEditor.getInstance().EVENTS.call(new EditSaveSuccessEvent(classInternalName));
    }
}
