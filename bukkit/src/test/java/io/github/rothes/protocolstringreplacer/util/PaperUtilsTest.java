package io.github.rothes.protocolstringreplacer.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaperUtilsTest {

    @Test
    void parsesMiniMessageInsideItemComponentText() {
        Component original = Component.text("<white>Hello <bold>menu</bold>")
                .color(NamedTextColor.GRAY)
                .append(Component.text(" tail"));

        Component parsed = PaperUtils.parseMiniMessageTags(original);

        assertFalse(PaperUtils.containsMiniMessageTag(parsed));
        assertEquals("Hello menu tail", plainText(parsed));
    }

    @Test
    void excludesBooksFromMiniMessageItemParsing() {
        assertFalse(PaperUtils.isEditableBook(Material.BOOK));
        assertTrue(PaperUtils.isEditableBook(Material.WRITABLE_BOOK));
        assertTrue(PaperUtils.isEditableBook(Material.WRITTEN_BOOK));
    }

    private static String plainText(Component component) {
        StringBuilder result = new StringBuilder();
        if (component instanceof TextComponent) {
            result.append(((TextComponent) component).content());
        }
        for (Component child : component.children()) {
            result.append(plainText(child));
        }
        return result.toString();
    }
}
