package io.github.rothes.protocolstringreplacer.replacer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListenTypeTest {

    @Test
    void miniMessageParsingIsLimitedToDisplaySurfaces() {
        assertTrue(ListenType.TITLE.shouldParseMiniMessageTags());
        assertTrue(ListenType.SCOREBOARD.shouldParseMiniMessageTags());
        assertTrue(ListenType.WINDOW_TITLE.shouldParseMiniMessageTags());

        assertFalse(ListenType.CHAT.shouldParseMiniMessageTags());
        assertFalse(ListenType.ACTIONBAR.shouldParseMiniMessageTags());
        assertFalse(ListenType.BOSS_BAR.shouldParseMiniMessageTags());
        assertFalse(ListenType.SIGN.shouldParseMiniMessageTags());
    }
}
