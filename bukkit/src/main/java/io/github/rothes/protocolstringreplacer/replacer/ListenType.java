package io.github.rothes.protocolstringreplacer.replacer;

public enum ListenType {

    CHAT("Chat", true),
    CHAT_PREVIEW("Chat-Preview", true),
    TAB_COMPLETE("Tab-Complete", true),
    SIGN("Sign", true),
    TITLE("Title", true, true),
    ENTITY("Entity", true),
    BOSS_BAR("Boss-Bar", true),
    ITEMSTACK("ItemStack", true),
    WINDOW_TITLE("Window-Title", true, true),
    SCOREBOARD("ScoreBoard", true, true),
    CONSOLE("Console", false),
    KICK_DISCONNECT("Kick-Disconnect", true), // Is it important capture-able now? lol
    COMBAT_KILL("Combat-Kill", true),
    ACTIONBAR("ActionBar", true);

    private String name;
    private boolean capturable;
    private final boolean parseMiniMessageTags;

    ListenType(String name, boolean capturable) {
        this(name, capturable, false);
    }

    ListenType(String name, boolean capturable, boolean parseMiniMessageTags) {
        this.name = name;
        this.capturable = capturable;
        this.parseMiniMessageTags = parseMiniMessageTags;
    }

    public static ListenType getType(String typeName) {
        for (ListenType type : ListenType.values()) {
            if (type.getName().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public boolean isCapturable() {
        return capturable;
    }

    public boolean shouldParseMiniMessageTags() {
        return parseMiniMessageTags;
    }

}
