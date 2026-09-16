package io.github.rothes.protocolstringreplacer.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PaperUtils {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static GsonComponentSerializer getPaperGsonComponentSerializer() {
        return SerializerHolder.paperGsonComponentSerializer;
    }

    public static String serializeComponent(Component component) {
        return SerializerHolder.psrSerializer.toJson(component);
    }

    public static boolean containsMiniMessageTag(String text) {
        return text != null && text.indexOf('<') >= 0 && !miniMessage.stripTags(text).equals(text);
    }

    public static String deserializeMiniMessage(String text) {
        return serializeComponent(miniMessage.deserialize(legacyToMiniMessage(text)));
    }

    public static boolean containsMiniMessageTag(ItemStack itemStack) {
        if (isEditableBook(itemStack.getType())) {
            return false;
        }
        if (!itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (containsMiniMessageTag(meta.displayName())) {
            return true;
        }
        List<Component> lore = meta.lore();
        if (lore != null) {
            for (Component line : lore) {
                if (containsMiniMessageTag(line)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isEditableBook(Material material) {
        return material == Material.WRITABLE_BOOK || material == Material.WRITTEN_BOOK;
    }

    public static ItemStack parseMiniMessageTags(ItemStack itemStack) {
        if (!containsMiniMessageTag(itemStack)) {
            return itemStack;
        }

        ItemStack result = itemStack.clone();
        ItemMeta meta = result.getItemMeta();

        Component displayName = meta.displayName();
        if (containsMiniMessageTag(displayName)) {
            meta.displayName(parseMiniMessageTags(displayName));
        }

        List<Component> lore = meta.lore();
        if (lore != null) {
            List<Component> parsedLore = new ArrayList<>(lore.size());
            for (Component line : lore) {
                parsedLore.add(parseMiniMessageTags(line));
            }
            meta.lore(parsedLore);
        }

        result.setItemMeta(meta);
        return result;
    }

    static Component parseMiniMessageTags(Component component) {
        if (component == null) {
            return Component.empty();
        }

        List<Component> children = new ArrayList<>(component.children().size());
        for (Component child : component.children()) {
            children.add(parseMiniMessageTags(child));
        }

        if (component instanceof TextComponent
                && containsMiniMessageTag(((TextComponent) component).content())) {
            Component parsed = miniMessage.deserialize(
                    legacyToMiniMessage(((TextComponent) component).content()));
            Component wrapper = Component.empty().style(component.style()).append(parsed);
            return wrapper.children(appendChildren(wrapper.children(), children));
        }

        return component.children(children);
    }

    private static List<Component> appendChildren(List<Component> existing, List<Component> appended) {
        if (appended.isEmpty()) {
            return existing;
        }
        List<Component> result = new ArrayList<>(existing.size() + appended.size());
        result.addAll(existing);
        result.addAll(appended);
        return result;
    }

    static boolean containsMiniMessageTag(Component component) {
        if (component == null) {
            return false;
        }
        if (component instanceof TextComponent && containsMiniMessageTag(((TextComponent) component).content())) {
            return true;
        }
        for (Component child : component.children()) {
            if (containsMiniMessageTag(child)) {
                return true;
            }
        }
        return false;
    }

    private static String legacyToMiniMessage(String text) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current != '\u00a7' || i + 1 >= text.length()) {
                result.append(current);
                continue;
            }

            char code = Character.toLowerCase(text.charAt(++i));
            if (code == 'x' && i + 12 < text.length()) {
                StringBuilder hex = new StringBuilder(6);
                int end = i + 12;
                boolean valid = true;
                for (int cursor = i + 1; cursor <= end; cursor += 2) {
                    if (text.charAt(cursor) != '\u00a7' || cursor + 1 > end
                            || Character.digit(text.charAt(cursor + 1), 16) < 0) {
                        valid = false;
                        break;
                    }
                    hex.append(text.charAt(cursor + 1));
                }
                if (valid) {
                    result.append("<#").append(hex).append('>');
                    i = end;
                    continue;
                }
            }

            String tag = legacyTag(code);
            if (tag == null) {
                result.append('\u00a7').append(code);
            } else {
                result.append('<').append(tag).append('>');
            }
        }
        return result.toString();
    }

    private static String legacyTag(char code) {
        switch (code) {
            case '0': return "black";
            case '1': return "dark_blue";
            case '2': return "dark_green";
            case '3': return "dark_aqua";
            case '4': return "dark_red";
            case '5': return "dark_purple";
            case '6': return "gold";
            case '7': return "gray";
            case '8': return "dark_gray";
            case '9': return "blue";
            case 'a': return "green";
            case 'b': return "aqua";
            case 'c': return "red";
            case 'd': return "light_purple";
            case 'e': return "yellow";
            case 'f': return "white";
            case 'k': return "obfuscated";
            case 'l': return "bold";
            case 'm': return "strikethrough";
            case 'n': return "underlined";
            case 'o': return "italic";
            case 'r': return "reset";
            default: return null;
        }
    }

    private static final class SerializerHolder {
        private static final GsonComponentSerializer paperGsonComponentSerializer = PaperComponents.gsonSerializer();
        private static final Gson psrSerializer = paperGsonComponentSerializer.populator()
                .apply(new GsonBuilder().disableHtmlEscaping()).create();
    }

}
