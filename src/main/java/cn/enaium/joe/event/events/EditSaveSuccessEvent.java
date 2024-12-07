package cn.enaium.joe.event.events;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.event.Event;

public record EditSaveSuccessEvent(String classInternalName) implements Event {
    public static void trigger(String classInternalName){
        JavaOctetEditor.getInstance().event.call(new EditSaveSuccessEvent(classInternalName));
    }
}
