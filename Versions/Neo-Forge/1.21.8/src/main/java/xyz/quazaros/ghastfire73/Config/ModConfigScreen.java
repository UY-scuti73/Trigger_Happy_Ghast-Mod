package xyz.quazaros.ghastfire73.Config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import xyz.quazaros.ghastfire73.Config.ConfigManager;
import xyz.quazaros.ghastfire73.Config.ModConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModConfigScreen extends Screen {
    private static final int ROW_HEIGHT = 28;
    private static final int TOP_MARGIN = 32;
    private static final int BOTTOM_MARGIN = 36;

    private final Screen parent;
    private final List<ConfigRow> rows = new ArrayList<>();

    private int listTop;
    private int listBottom;
    private int scrollAmount;
    private int maxScroll;

    public ModConfigScreen(Screen parent) {
        super(Component.literal("Trigger Happy Ghast Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.rows.clear();

        this.listTop = TOP_MARGIN;
        this.listBottom = this.height - BOTTOM_MARGIN;

        ModConfig config = ConfigManager.get();

        rows.add(new ConfigRow(this.font, getField("explosionValue"), config, 0, 256));
        rows.add(new ConfigRow(this.font, getField("durabilityDamageValue"), config, 0, 100));

        for (ConfigRow row : rows) {
            this.addRenderableWidget(row.slider);
            this.addRenderableWidget(row.box);
        }

        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            ConfigManager.get().validate();
            ConfigManager.save();
            this.onClose();
        }).bounds(this.width / 2 - 102, this.height - 26, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> {
            ConfigManager.load();
            this.onClose();
        }).bounds(this.width / 2 + 2, this.height - 26, 100, 20).build());

        updateLayout();
    }

    private static Field getField(String name) {
        try {
            return ModConfig.class.getField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Missing config field: " + name, e);
        }
    }

    private void updateLayout() {
        int viewportHeight = listBottom - listTop;
        int contentHeight = rows.size() * ROW_HEIGHT;
        this.maxScroll = Math.max(0, contentHeight - viewportHeight);
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0, this.maxScroll);

        int left = Math.max(20, this.width / 2 - 180);

        for (int i = 0; i < rows.size(); i++) {
            int y = listTop + i * ROW_HEIGHT - scrollAmount;
            ConfigRow row = rows.get(i);

            row.setPosition(left, y);
            boolean visible = y >= listTop - 20 && y + 20 <= listBottom + 20;
            row.setVisible(visible);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseY >= listTop && mouseY <= listBottom && maxScroll > 0) {
            this.scrollAmount = Mth.clamp(this.scrollAmount - (int) (scrollY * 14), 0, maxScroll);
            updateLayout();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x88000000);
        guiGraphics.fill(16, listTop - 4, this.width - 16, listBottom + 4, 0x66000000);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        for (ConfigRow row : rows) {
            if (row.visible) {
                row.renderLabel(guiGraphics, this.font);
            }
        }
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        renderListScrollbar(guiGraphics);
    }

    private void renderListScrollbar(GuiGraphics guiGraphics) {
        int viewportHeight = listBottom - listTop;
        int contentHeight = rows.size() * ROW_HEIGHT;
        if (contentHeight <= viewportHeight) return;

        int barX1 = this.width - 12;
        int barX2 = this.width - 8;
        guiGraphics.fill(barX1, listTop, barX2, listBottom, 0xFF2A2A2A);

        int thumbHeight = Math.max(16, viewportHeight * viewportHeight / contentHeight);
        int travel = viewportHeight - thumbHeight;
        int thumbY = listTop + (maxScroll == 0 ? 0 : (scrollAmount * travel / maxScroll));

        guiGraphics.fill(barX1, thumbY, barX2, thumbY + thumbHeight, 0xFFB0B0B0);
    }

    private static String prettyName(String raw) {
        String[] parts = raw.split("(?=[A-Z])");
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!out.isEmpty()) out.append(' ');
            out.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                out.append(part.substring(1).toLowerCase());
            }
        }
        return out.toString();
    }

    private static final class ConfigRow {
        private final Field field;
        private final ModConfig config;
        private final EditBox box;
        private final IntSlider slider;
        private final int min;
        private final int max;

        private int x;
        private int y;
        private boolean visible = true;
        private boolean updatingText = false;

        private ConfigRow(Font font, Field field, ModConfig config, int min, int max) {
            this.field = field;
            this.config = config;
            this.min = min;
            this.max = max;

            int current = getValue();

            this.slider = new IntSlider(0, 0, 150, 20, this, current, min, max);
            this.box = new EditBox(font, 0, 0, 60, 20, Component.literal(field.getName()));
            this.box.setValue(String.valueOf(current));

            this.box.setFilter(text -> text.isEmpty() || text.matches("\\d+"));

            this.box.setResponder(text -> {
                if (updatingText || text.isBlank()) return;
                try {
                    int value = Mth.clamp(Integer.parseInt(text), min, max);
                    setValue(value, false);
                } catch (NumberFormatException ignored) {
                }
            });
        }

        private void setPosition(int left, int y) {
            this.x = left;
            this.y = y;
            this.slider.setX(left + 140);
            this.slider.setY(y);
            this.box.setX(left + 300);
            this.box.setY(y);
        }

        private void setVisible(boolean visible) {
            this.visible = visible;
            this.slider.visible = visible;
            this.slider.active = visible;
            this.box.visible = visible;
            this.box.active = visible;
        }

        private void renderLabel(GuiGraphics guiGraphics, Font font) {
            guiGraphics.drawString(font, prettyName(field.getName()), x, y + 6, 0xFFFFFFFF, false);
        }

        private int getValue() {
            try {
                return field.getInt(config);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to read config field: " + field.getName(), e);
            }
        }

        private void setValue(int value, boolean rewriteTextBox) {
            int clamped = Mth.clamp(value, min, max);

            try {
                field.setInt(config, clamped);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to write config field: " + field.getName(), e);
            }

            this.slider.setSliderValue(clamped);

            if (rewriteTextBox) {
                this.updatingText = true;
                this.box.setValue(String.valueOf(clamped));
                this.updatingText = false;
            }
        }
    }

    private static final class IntSlider extends AbstractSliderButton {
        private final ConfigRow row;
        private final int min;
        private final int max;

        private IntSlider(int x, int y, int width, int height, ConfigRow row, int initialValue, int min, int max) {
            super(x, y, width, height, Component.empty(), (double)(initialValue - min) / (max - min));
            this.row = row;
            this.min = min;
            this.max = max;
            updateMessage();
        }

        private int getIntValue() {
            return min + (int) Math.round(this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(String.valueOf(getIntValue())));
        }

        @Override
        protected void applyValue() {
            row.setValue(getIntValue(), true);
            updateMessage();
        }

        public void setSliderValue(int value) {
            this.value = (double)(value - min) / (max - min);
            updateMessage();
        }
    }
}