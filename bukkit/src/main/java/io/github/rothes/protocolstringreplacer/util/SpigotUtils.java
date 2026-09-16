package io.github.rothes.protocolstringreplacer.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.rothes.protocolstringreplacer.ProtocolStringReplacer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.EntitySerializer;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.ItemSerializer;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.hover.content.TextSerializer;
import net.md_5.bungee.chat.ChatVersion;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.chat.VersionedComponentSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpigotUtils {

    private static final Gson psrSerializer;
    private static final JsonParser jsonParser = new JsonParser();

    static {
        Gson temp = null;
        ProtocolStringReplacer plugin = ProtocolStringReplacer.getInstance();
        try {
            Class<?> clazz;
            Object instance = null;
            if (plugin.getServerMajorVersion() > 21
                    || plugin.getServerMajorVersion() == 21 && plugin.getServerMinorVersion() >= 5) {
                clazz = VersionedComponentSerializer.class;
                instance = VersionedComponentSerializer.forVersion(ChatVersion.V1_21_5);
            } else {
                clazz = ComponentSerializer.class;
            }
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.getType() == Gson.class) {
                    declaredField.setAccessible(true);
                    temp = (Gson) declaredField.get(instance);
                    try {
                        temp = temp.newBuilder().disableHtmlEscaping().create();
                    } catch (NoSuchMethodError e) {
                        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping();
                        try {
                            gsonBuilder.registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
                                    .registerTypeAdapter(TextComponent.class, newInstance(TextComponentSerializer.class))
                                    .registerTypeAdapter(TranslatableComponent.class, newInstance(TranslatableComponentSerializer.class))
                                    .registerTypeAdapter(KeybindComponent.class, newInstance(KeybindComponentSerializer.class))
                                    .registerTypeAdapter(ScoreComponent.class, newInstance(ScoreComponentSerializer.class))
                                    .registerTypeAdapter(SelectorComponent.class, newInstance(SelectorComponentSerializer.class))
                                    .registerTypeAdapter(Entity.class, newInstance(EntitySerializer.class))
                                    .registerTypeAdapter(Text.class, new TextSerializer())
                                    .registerTypeAdapter(Item.class, new ItemSerializer())
                                    .registerTypeAdapter(ItemTag.class, new ItemTag.Serializer());
                        } catch (NoClassDefFoundError ignored) {
                            // Some types are not available on legacy servers.
                        }
                        temp = gsonBuilder.create();
                    }
                    break;
                }
            }
        } catch (Throwable e) {
            ProtocolStringReplacer.error("Unable to disableHtmlEscaping for SpigotComponentSerializer:", e);
        }
        psrSerializer = temp;
    }

    public static BaseComponent[] parseComponents(String json) {
        try {
            JsonElement jsonElement = jsonParser.parse(json);
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                BaseComponent[] components = psrSerializer.fromJson(jsonElement, BaseComponent[].class);
                if (components == null || components.length != jsonArray.size()) {
                    components = new BaseComponent[jsonArray.size()];
                }
                for (int i = 0; i < components.length; i++) {
                    if (components[i] == null) {
                        components[i] = parseComponent(jsonArray.get(i));
                    }
                }
                return components;
            }
            return new BaseComponent[] { parseComponent(jsonElement) };

        } catch (Throwable t) {
            try {
                return ComponentSerializer.parse(json);
            } catch (Throwable ignored) {
                return new BaseComponent[] { new TextComponent(json) };
            }
        }
    }

    private static BaseComponent parseComponent(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return new TextComponent("");
        }
        if (jsonElement.isJsonPrimitive()) {
            return new TextComponent(jsonElement.getAsString());
        }
        BaseComponent component = psrSerializer.fromJson(jsonElement, BaseComponent.class);
        return component == null ? new TextComponent("") : component;
    }

    public static String serializeComponents(BaseComponent... components) {
        return serializeComponents(false, components);
    }

    public static String serializeComponents(boolean parseMiniMessageTags, BaseComponent... components) {
        try {
            if (parseMiniMessageTags
                    && ProtocolStringReplacer.getInstance().getConfigManager() != null
                    && ProtocolStringReplacer.getInstance().getConfigManager().parseMiniMessageTags
                    && containsMiniMessageTags(components)) {
                components = parseMiniMessageTags(components);
            }
            if (components.length == 1) {
                return psrSerializer.toJson(components[0]);
            } else {
                return psrSerializer.toJson(new TextComponent(components));
            }
        } catch (Throwable t) {
            // ComponentSerializer Gson may be modified during Runtime.
            return ComponentSerializer.toString(components);
        }
    }

    private static boolean containsMiniMessageTags(BaseComponent[] components) {
        for (BaseComponent component : components) {
            if (component instanceof TextComponent
                    && PaperUtils.containsMiniMessageTag(((TextComponent) component).getText())) {
                return true;
            }
            if (component.getExtra() != null
                    && containsMiniMessageTags(component.getExtra().toArray(new BaseComponent[0]))) {
                return true;
            }
        }
        return false;
    }

    private static BaseComponent[] parseMiniMessageTags(BaseComponent[] components) {
        List<BaseComponent> result = new ArrayList<>(components.length);
        for (BaseComponent component : components) {
            BaseComponent[] transformedExtra = component.getExtra() == null ? null
                    : parseMiniMessageTags(component.getExtra().toArray(new BaseComponent[0]));

            if (component instanceof TextComponent
                    && PaperUtils.containsMiniMessageTag(((TextComponent) component).getText())) {
                BaseComponent[] parsed = ComponentSerializer.parse(
                        PaperUtils.deserializeMiniMessage(((TextComponent) component).getText()));
                TextComponent wrapper = new TextComponent("");
                wrapper.copyFormatting(component, net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.ALL, false);
                List<BaseComponent> extra = new ArrayList<>(Arrays.asList(parsed));
                if (transformedExtra != null) {
                    extra.addAll(Arrays.asList(transformedExtra));
                }
                wrapper.setExtra(extra);
                result.add(wrapper);
            } else {
                component = component.duplicate();
                if (transformedExtra != null) {
                    component.setExtra(new ArrayList<>(Arrays.asList(transformedExtra)));
                }
                result.add(component);
            }
        }
        return result.toArray(new BaseComponent[0]);
    }

    @SuppressWarnings({"deprecation"})
    public static boolean compareComponents(BaseComponent[] a, BaseComponent[] b) {
        if (a == null && b != null || a != null && b == null) {
            return false;
        }
        if (a == null) {
            return true;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0, length = a.length; i < length; i++) {
            BaseComponent component = a[i];
            BaseComponent other = b[i];
            if (!component.toLegacyText().equals(other.toLegacyText())) {
                return false;
            } else if (component.getHoverEvent() != other.getHoverEvent() && component.getHoverEvent() != null
                    && !compareComponents(component.getHoverEvent().getValue(), other.getHoverEvent().getValue())) {
                return false;
            }
        }
        return true;
    }

    private static Object newInstance(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

}
