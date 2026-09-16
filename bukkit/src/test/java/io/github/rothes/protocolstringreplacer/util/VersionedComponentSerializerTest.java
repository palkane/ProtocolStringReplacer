package io.github.rothes.protocolstringreplacer.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ChatVersion;
import net.md_5.bungee.chat.VersionedComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VersionedComponentSerializerTest {

    @Test
    void preservesModernClickEvent() {
        String original = "{\"text\":\"Click me\",\"click_event\":"
                + "{\"action\":\"run_command\",\"command\":\"/help\"}}";
        VersionedComponentSerializer serializer = VersionedComponentSerializer.forVersion(ChatVersion.V1_21_5);

        BaseComponent component = serializer.deserialize(original);
        JsonObject clickEvent = JsonParser.parseString(serializer.toString(component)).getAsJsonObject()
                .getAsJsonObject("click_event");

        assertNotNull(clickEvent);
        assertEquals("run_command", clickEvent.get("action").getAsString());
        assertEquals("/help", clickEvent.get("command").getAsString());
    }
}
