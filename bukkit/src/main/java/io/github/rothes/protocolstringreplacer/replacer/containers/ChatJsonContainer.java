package io.github.rothes.protocolstringreplacer.replacer.containers;

import io.github.rothes.protocolstringreplacer.api.exceptions.JsonSyntaxException;
import io.github.rothes.protocolstringreplacer.util.SpigotUtils;
import org.jetbrains.annotations.NotNull;

public class ChatJsonContainer extends AbstractContainer<String> {

    private boolean createComponents = false;
    private final boolean parseMiniMessageTags;
    private ComponentsContainer componentsContainer = null;

    public ChatJsonContainer(@NotNull String json) {
        super(json);
        parseMiniMessageTags = false;
    }

    public ChatJsonContainer(@NotNull String json, boolean createComponents) {
        this(json, createComponents, false);
    }

    public ChatJsonContainer(@NotNull String json, boolean createComponents, boolean parseMiniMessageTags) {
        super(json);
        this.createComponents = createComponents;
        this.parseMiniMessageTags = parseMiniMessageTags;
    }

    public ChatJsonContainer(@NotNull String json, @NotNull Container<?> root) {
        super(json, root);
        parseMiniMessageTags = false;
    }

    public ChatJsonContainer(@NotNull String json, @NotNull Container<?> root, boolean createComponents) {
        this(json, root, createComponents, false);
    }

    public ChatJsonContainer(@NotNull String json, @NotNull Container<?> root, boolean createComponents,
                             boolean parseMiniMessageTags) {
        super(json, root);
        this.createComponents = createComponents;
        this.parseMiniMessageTags = parseMiniMessageTags;
    }

    @Override
    public void createDefaultChildren() {
        if (createComponents) {
            try {
                componentsContainer = new ComponentsContainer(SpigotUtils.parseComponents(content), root);
            } catch (Throwable t) {
                throw new JsonSyntaxException("Serializer can't parse Json: " + content, t);
            }
            children.add(componentsContainer);
        }
        super.createDefaultChildren();
    }

    @Override
    public void createJsons(@NotNull Container<?> container) {
        super.createJsons(container);
        root.addJson(new ReplaceableImpl());
    }

    @Override
    public String getResult() {
        if (componentsContainer != null) {
            return SpigotUtils.serializeComponents(parseMiniMessageTags, componentsContainer.getResult());
        } else {
            return super.getResult();
        }
    }

    public ComponentsContainer getComponentsContainer() {
        return componentsContainer;
    }

    private class ReplaceableImpl implements Replaceable {

        @Override
        public String getText() {
            return content;
        }

        @Override
        public void setText(String text) {
            content = text;
        }

        @Override
        public String toString() {
            return content;
        }

    }

}
