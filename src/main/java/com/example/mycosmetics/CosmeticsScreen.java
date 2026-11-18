package com.example.mycosmetics;

import com.example.mycosmetics.Cosmetic.Slot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class CosmeticsScreen extends Screen {
    private static final int ROWS = 7;
    private static final int ROW_H = 22;
    private static final int TOP = 40;

    private final Screen parent;
    private Slot slot = Slot.CAPE;
    private int page = 0;
    private final List<Cosmetic> visibleRows = new ArrayList<>(); // null = "ничего"

    public CosmeticsScreen(Screen parent) {
        super(Text.literal("Косметика"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        CosmeticsConfig cfg = CosmeticsConfig.get();
        int left = width / 2 - 180;

        // --- категории слева
        int i = 0;
        for (Slot s : Slot.values()) {
            final Slot fs = s;
            Text label = Text.literal((s == slot ? "▶ " : "") + s.label);
            addDrawableChild(ButtonWidget.builder(label, b -> {
                slot = fs;
                page = 0;
                clearAndInit();
            }).dimensions(left, TOP + i * 24, 84, 20).build());
            i++;
        }

        // --- список выбранной категории
        List<Cosmetic> entries = new ArrayList<>();
        entries.add(null);
        entries.addAll(Cosmetics.bySlot(slot));
        int pages = Math.max(1, (entries.size() + ROWS - 1) / ROWS);
        if (page >= pages) page = pages - 1;

        visibleRows.clear();
        String current = cfg.selected.get(slot.name());
        for (int r = 0; r < ROWS; r++) {
            int idx = page * ROWS + r;
            if (idx >= entries.size()) break;
            Cosmetic c = entries.get(idx);
            visibleRows.add(c);

            boolean sel = c == null ? current == null : c.id.equals(current);
            Text label = Text.literal(c == null ? "— нет —" : c.name);
            if (sel) label = Text.literal((c == null ? "— нет —" : c.name) + " ✔").formatted(Formatting.GREEN);

            addDrawableChild(ButtonWidget.builder(label, b -> select(c))
                .dimensions(left + 112, TOP + r * ROW_H, 128, 20).build());
        }

        int py = TOP + ROWS * ROW_H + 4;
        addDrawableChild(ButtonWidget.builder(Text.literal("◀"), b -> { if (page > 0) { page--; clearAndInit(); } })
            .dimensions(left + 90, py, 30, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("▶"), b -> { if (page < pages - 1) { page++; clearAndInit(); } })
            .dimensions(left + 210, py, 30, 20).build());

        // --- переключатели справа под предпросмотром
        addDrawableChild(ButtonWidget.builder(onOff("Косметика", cfg.enabled), b -> {
            cfg.enabled = !cfg.enabled;
            b.setMessage(onOff("Косметика", cfg.enabled));
            CosmeticsConfig.save();
        }).dimensions(left + 248, TOP + 156, 112, 20).build());

        addDrawableChild(ButtonWidget.builder(onOff("На невидимке", cfg.showWhenInvisible), b -> {
            cfg.showWhenInvisible = !cfg.showWhenInvisible;
            b.setMessage(onOff("На невидимке", cfg.showWhenInvisible));
            CosmeticsConfig.save();
        }).dimensions(left + 248, TOP + 180, 112, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> close())
            .dimensions(width / 2 - 60, py + 28, 120, 20).build());
    }

    private static Text onOff(String name, boolean on) {
        return Text.literal(name + ": ")
            .append(Text.literal(on ? "да" : "нет").formatted(on ? Formatting.GREEN : Formatting.RED));
    }

    private void select(Cosmetic c) {
        CosmeticsConfig cfg = CosmeticsConfig.get();
        if (c == null) cfg.selected.remove(slot.name());
        else cfg.selected.put(slot.name(), c.id);
        CosmeticsConfig.save();
        clearAndInit();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int left = width / 2 - 180;

        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 14, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Косметику видишь только ты (в 3-м лице на F5)"), width / 2, 26, 0xAAAAAA);

        // иконки рядом с рядами
        for (int r = 0; r < visibleRows.size(); r++) {
            Cosmetic c = visibleRows.get(r);
            if (c != null) ctx.drawItem(new ItemStack(c.icon), left + 92, TOP + r * ROW_H + 2);
        }

        // предпросмотр: твой персонаж (двигай мышкой, чтобы поворачивать)
        int x1 = left + 248, y1 = TOP, x2 = left + 360, y2 = TOP + 150;
        ctx.fill(x1, y1, x2, y2, 0x66000000);
        if (client != null && client.player != null) {
            InventoryScreen.drawEntity(ctx, x1, y1, x2, y2, 60, 0.0625f, mouseX, mouseY, client.player);
        }

        int py = TOP + ROWS * ROW_H + 4;
        int pages = Math.max(1, (Cosmetics.bySlot(slot).size() + 1 + ROWS - 1) / ROWS);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal((page + 1) + " / " + pages), left + 165, py + 6, 0xAAAAAA);
    }

    @Override
    public void close() {
        CosmeticsConfig.save();
        client.setScreen(parent);
    }
}
