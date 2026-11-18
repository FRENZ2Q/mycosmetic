package com.example.mycosmetics;

import com.example.mycosmetics.Cosmetic.Anchor;
import com.example.mycosmetics.Cosmetic.Box;
import com.example.mycosmetics.Cosmetic.Part;
import com.example.mycosmetics.Cosmetic.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Каталог косметики. Чтобы добавить новую - создай Cosmetic и вызови add(...) в блоке static ниже.
 * Координаты в пикселях (1 пиксель = 1/16 блока), см. описание в Cosmetic.java.
 */
public final class Cosmetics {
    public static final List<Cosmetic> ALL = new ArrayList<>();
    private static final Map<String, Cosmetic> BY_ID = new HashMap<>();

    private Cosmetics() {}

    public static Cosmetic get(String id) {
        return id == null ? null : BY_ID.get(id);
    }

    public static List<Cosmetic> bySlot(Slot slot) {
        List<Cosmetic> res = new ArrayList<>();
        for (Cosmetic c : ALL) if (c.slot == slot) res.add(c);
        return res;
    }

    private static void add(Cosmetic c) {
        ALL.add(c);
        BY_ID.put(c.id, c);
    }

    // ------------------------------------------------------------ помощники

    private static final int GOLD = 0xFFC107;

    private static Box b(float x1, float y1, float z1, float x2, float y2, float z2, int rgb) {
        return new Box(x1, y1, z1, x2, y2, z2, rgb, false);
    }

    private static Box glow(float x1, float y1, float z1, float x2, float y2, float z2, int rgb) {
        return new Box(x1, y1, z1, x2, y2, z2, rgb, true);
    }

    /** добавляет блок и его зеркальную копию по x */
    private static void pair(List<Box> l, Box b) {
        l.add(b);
        l.add(new Box(-b.x2(), b.y1(), b.z1(), -b.x1(), b.y2(), b.z2(), b.rgb(), b.glow()));
    }

    private static Part part(List<Box> l) {
        return new Part(l.toArray(new Box[0]));
    }

    private static Part part(Box... bs) {
        return new Part(bs);
    }

    // ------------------------------------------------------------ генераторы

    private static Cosmetic cape(String id, String name, Item icon, int main, int trim) {
        Part p = part(
            b(-5, 0, 2.2f, 5, 14.5f, 3.2f, main),
            b(-5, 14.5f, 2.2f, 5, 16, 3.2f, trim),
            b(-5.2f, 0, 2.1f, 5.2f, 1.4f, 3.4f, trim)
        ).rot('x', 0, 0, 2.5f, 4, 3, 0.07f);
        return new Cosmetic(id, name, Slot.CAPE, Anchor.BODY, icon, p);
    }

    private static Cosmetic wings(String id, String name, Item icon, int main, int edge) {
        List<Part> parts = new ArrayList<>();
        for (int side = -1; side <= 1; side += 2) {
            List<Box> l = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                float xa = 2 + i * 2.2f;
                float xb = xa + 2.2f;
                float top = -3 + i * 1.6f;
                float bottom = 12 - i * 1.2f;
                int color = i >= 4 ? edge : main;
                if (side > 0) l.add(b(xa, top, 2.8f, xb, bottom, 3.6f, color));
                else l.add(b(-xb, top, 2.8f, -xa, bottom, 3.6f, color));
            }
            float base = side > 0 ? -18 : 18;
            float sway = side > 0 ? -10 : 10;
            parts.add(part(l).rot('y', side * 2f, 0, 3.2f, base, sway, 0.12f));
        }
        return new Cosmetic(id, name, Slot.BACK, Anchor.BODY, icon, parts.toArray(new Part[0]));
    }

    private static Cosmetic mask(String id, String name, Item icon, List<Box> l) {
        return new Cosmetic(id, name, Slot.MASK, Anchor.HEAD, icon, part(l));
    }

    // ------------------------------------------------------------ каталог

    static {
        // ---------------- ПЛАЩИ
        add(cape("cape_red", "Красный плащ", Items.RED_BANNER, 0xB3262E, 0xFFD54F));
        add(cape("cape_blue", "Синий плащ", Items.BLUE_BANNER, 0x2B4DB8, 0xE0E0E0));
        add(cape("cape_black", "Чёрный плащ", Items.BLACK_BANNER, 0x1A1A1F, 0x8B0000));
        add(cape("cape_royal", "Королевский плащ", Items.PURPLE_BANNER, 0x6A1B9A, GOLD));
        add(cape("cape_white", "Белый плащ", Items.WHITE_BANNER, 0xF2F2F2, 0x9E9E9E));
        add(cape("cape_green", "Зелёный плащ", Items.GREEN_BANNER, 0x2E7D32, 0xC8E6C9));
        add(cape("cape_gold", "Золотой плащ", Items.YELLOW_BANNER, 0xFFC107, 0xFFF59D));
        {
            int[] rainbow = {0xE53935, 0xFB8C00, 0xFDD835, 0x43A047, 0x1E88E5, 0x3949AB, 0x8E24AA};
            List<Box> l = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                l.add(b(-5, i * 16f / 7f, 2.2f, 5, (i + 1) * 16f / 7f, 3.2f, rainbow[i]));
            }
            add(new Cosmetic("cape_rainbow", "Радужный плащ", Slot.CAPE, Anchor.BODY, Items.PINK_BANNER,
                part(l).rot('x', 0, 0, 2.5f, 4, 3, 0.07f)));
        }

        // ---------------- ШАПКИ
        {   // китайская шляпа (конус)
            List<Box> l = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                float half = 8.5f - i;
                int color = i % 2 == 0 ? 0xD8B25A : 0xC9A24B;
                l.add(b(-half, -9.6f - i, -half, half, -8.6f - i, half, color));
            }
            add(new Cosmetic("hat_china", "Китайская шляпа", Slot.HAT, Anchor.HEAD, Items.HAY_BLOCK, part(l)));
        }
        add(new Cosmetic("hat_top", "Цилиндр", Slot.HAT, Anchor.HEAD, Items.BLACK_WOOL, part(
            b(-6.5f, -9.6f, -6.5f, 6.5f, -8.6f, 6.5f, 0x1B1B1B),
            b(-4.5f, -17.6f, -4.5f, 4.5f, -9.6f, 4.5f, 0x1B1B1B),
            b(-4.6f, -11.6f, -4.6f, 4.6f, -9.6f, 4.6f, 0xB71C1C))));
        {   // корона
            List<Box> l = new ArrayList<>();
            l.add(b(-4.8f, -11, -4.8f, 4.8f, -8.6f, 4.8f, GOLD));
            pair(l, b(3.6f, -14, 3.6f, 4.8f, -11, 4.8f, GOLD));
            pair(l, b(3.6f, -14, -4.8f, 4.8f, -11, -3.6f, GOLD));
            l.add(b(-0.6f, -14, -4.8f, 0.6f, -11, -3.6f, GOLD));
            l.add(b(-0.6f, -14, 3.6f, 0.6f, -11, 4.8f, GOLD));
            l.add(glow(-0.8f, -10.4f, -5.1f, 0.8f, -9, -4.7f, 0xE53935));
            add(new Cosmetic("hat_crown", "Корона", Slot.HAT, Anchor.HEAD, Items.GOLDEN_HELMET, part(l)));
        }
        add(new Cosmetic("hat_halo", "Нимб", Slot.HAT, Anchor.HEAD, Items.GOLD_NUGGET, part(
            glow(-5, -14.5f, -5, 5, -13.5f, -4, 0xFFE082),
            glow(-5, -14.5f, 4, 5, -13.5f, 5, 0xFFE082),
            glow(-5, -14.5f, -4, -4, -13.5f, 4, 0xFFE082),
            glow(4, -14.5f, -4, 5, -13.5f, 4, 0xFFE082)).bob(0.8f, 0.08f)));
        {   // рога дьявола
            List<Box> l = new ArrayList<>();
            pair(l, b(-4.4f, -10, -1, -3, -8.6f, 1, 0xC62828));
            pair(l, b(-4.6f, -12.2f, -0.8f, -3.4f, -10, 0.8f, 0xC62828));
            pair(l, b(-4.8f, -13.6f, -0.5f, -4, -12.2f, 0.5f, 0x4E0D0D));
            add(new Cosmetic("hat_horns", "Рога дьявола", Slot.HAT, Anchor.HEAD, Items.GOAT_HORN, part(l)));
        }
        {   // колпак волшебника
            List<Box> l = new ArrayList<>();
            l.add(b(-7, -9.6f, -7, 7, -8.6f, 7, 0x5E35B1));
            float half = 4.5f;
            for (int i = 0; i < 5; i++) {
                float y2 = -9.6f - i * 2f;
                l.add(b(-half, y2 - 2f, -half, half, y2, half, 0x5E35B1));
                half -= 0.9f;
            }
            l.add(b(-4.6f, -10.4f, -4.6f, 4.6f, -9.6f, 4.6f, GOLD));
            add(new Cosmetic("hat_wizard", "Колпак волшебника", Slot.HAT, Anchor.HEAD, Items.AMETHYST_SHARD, part(l)));
        }
        add(new Cosmetic("hat_cap", "Кепка", Slot.HAT, Anchor.HEAD, Items.LEATHER_HELMET, part(
            b(-4.8f, -10.4f, -4.8f, 4.8f, -8.6f, 4.8f, 0xD32F2F),
            b(-4.8f, -9.4f, -8.2f, 4.8f, -8.7f, -4.8f, 0xB71C1C))));
        add(new Cosmetic("hat_santa", "Колпак Санты", Slot.HAT, Anchor.HEAD, Items.RED_WOOL, part(
            b(-5, -10, -5, 5, -8.6f, 5, 0xFFFFFF),
            b(-4.4f, -12, -4.4f, 4.4f, -10, 4.4f, 0xD32F2F),
            b(-3.4f, -14, -3.4f, 3.4f, -12, 3.4f, 0xD32F2F),
            b(-2.4f, -15.6f, -2.4f, 2.4f, -14, 2.4f, 0xD32F2F),
            b(-1.4f, -17.2f, -1.4f, 1.4f, -15.6f, 1.4f, 0xFFFFFF))));

        // ---------------- МАСКИ (поверхность лица z = -4.7, наружу до -5.2)
        {   // Анонимус
            List<Box> l = new ArrayList<>();
            l.add(b(-3.9f, -7.6f, -5.2f, 3.9f, -0.6f, -4.7f, 0xF5F5F5));
            pair(l, b(-3.1f, -5.2f, -5.5f, -1.3f, -3.8f, -5.2f, 0x111111));
            pair(l, b(-3.2f, -6.2f, -5.5f, -1.0f, -5.6f, -5.2f, 0x111111));
            pair(l, b(-3.5f, -3.4f, -5.5f, -2.5f, -2.4f, -5.2f, 0xE57373));
            l.add(b(-3.2f, -2.4f, -5.5f, 3.2f, -1.6f, -5.2f, 0x111111));
            l.add(b(-0.8f, -1.6f, -5.5f, 0.8f, -0.6f, -5.2f, 0x111111));
            add(mask("mask_anon", "Маска Анонимуса", Items.BONE_MEAL, l));
        }
        add(mask("mask_ninja", "Маска ниндзя", Items.BLACK_DYE, List.of(
            b(-4.8f, -4.2f, -4.8f, 4.8f, 0, 4.8f, 0x212121),
            b(-4.8f, -7.2f, -4.8f, 4.8f, -5.8f, 4.8f, 0xC62828),
            b(-1.2f, -7f, 4.8f, 1.2f, -6f, 8.2f, 0xC62828),
            b(-1f, -6f, 7f, 1f, -4.2f, 8.4f, 0xC62828))));
        {   // крипер
            List<Box> l = new ArrayList<>();
            l.add(b(-4, -8, -5.2f, 4, 0, -4.7f, 0x43A047));
            pair(l, b(-3, -6.2f, -5.5f, -1, -4.2f, -5.2f, 0x111111));
            l.add(b(-1, -4.2f, -5.5f, 1, -2.2f, -5.2f, 0x111111));
            l.add(b(-2, -2.2f, -5.5f, 2, -0.6f, -5.2f, 0x111111));
            add(mask("mask_creeper", "Маска крипера", Items.CREEPER_HEAD, l));
        }
        {   // очки
            List<Box> l = new ArrayList<>();
            pair(l, b(-3.8f, -4.9f, -5.5f, -0.5f, -2.7f, -4.9f, 0x111111));
            l.add(b(-0.5f, -4.5f, -5.5f, 0.5f, -3.9f, -4.9f, 0x111111));
            pair(l, b(-4.7f, -4.7f, -5.4f, -4.1f, -3.9f, 0, 0x111111));
            add(mask("mask_glasses", "Тёмные очки", Items.COAL, l));
        }
        {   // венецианская
            List<Box> l = new ArrayList<>();
            l.add(b(-3.9f, -5.6f, -5.2f, 3.9f, -1.4f, -4.7f, GOLD));
            pair(l, b(-3.1f, -4.6f, -5.5f, -1.1f, -3.2f, -5.2f, 0x1A1A1A));
            pair(l, b(3.5f, -6.4f, -5.5f, 4.2f, -5.6f, -5.2f, 0xE53935));
            l.add(b(4.6f, -13, -1, 5.4f, -5, 1, 0x7E57C2));
            add(mask("mask_venetian", "Венецианская маска", Items.GOLD_INGOT, l));
        }
        {   // они
            List<Box> l = new ArrayList<>();
            l.add(b(-4, -8, -5.2f, 4, 0, -4.7f, 0xD32F2F));
            pair(l, b(-3.2f, -5.4f, -5.5f, -1.2f, -4, -5.2f, 0xFFEB3B));
            pair(l, b(-3.4f, -6.4f, -5.5f, -0.8f, -5.6f, -5.2f, 0x111111));
            l.add(b(-3, -2.2f, -5.5f, 3, -1.2f, -5.2f, 0x111111));
            pair(l, b(-2.4f, -2f, -5.6f, -1.4f, 0.2f, -5.2f, 0xFFFFFF));
            pair(l, b(-3.4f, -10.6f, -4.8f, -2.4f, -8f, -3.6f, GOLD));
            add(mask("mask_oni", "Маска они", Items.RED_DYE, l));
        }
        {   // череп
            List<Box> l = new ArrayList<>();
            l.add(b(-3.9f, -7.8f, -5.2f, 3.9f, 0, -4.7f, 0xEEEEEE));
            pair(l, b(-3.3f, -5.8f, -5.5f, -1.1f, -3.4f, -5.2f, 0x111111));
            l.add(b(-0.7f, -3.2f, -5.5f, 0.7f, -1.8f, -5.2f, 0x111111));
            l.add(b(-3, -1.8f, -5.5f, 3, -0.4f, -5.2f, 0x111111));
            l.add(b(-2.2f, -1.8f, -5.7f, -1.2f, -0.4f, -5.2f, 0xFFFFFF));
            l.add(b(-0.5f, -1.8f, -5.7f, 0.5f, -0.4f, -5.2f, 0xFFFFFF));
            l.add(b(1.2f, -1.8f, -5.7f, 2.2f, -0.4f, -5.2f, 0xFFFFFF));
            add(mask("mask_skull", "Череп", Items.SKELETON_SKULL, l));
        }

        // ---------------- СПИНА
        add(new Cosmetic("back_pack", "Рюкзак", Slot.BACK, Anchor.BODY, Items.CHEST, part(
            b(-4, 1, 2.2f, 4, 11, 5.5f, 0x6D4C41),
            b(-4, 1, 2.2f, 4, 4, 5.8f, 0x5D4037),
            b(-1, 3, 5.8f, 1, 4.5f, 6.1f, GOLD))));
        add(wings("back_angel", "Крылья ангела", Items.FEATHER, 0xFFFFFF, 0xCFE8FF));
        add(wings("back_dragon", "Крылья дракона", Items.DRAGON_EGG, 0x8B1A1A, 0x4A0C0C));
        {
            List<Box> tanks = new ArrayList<>();
            pair(tanks, b(-4.5f, 1, 2.2f, -1.5f, 10, 5.2f, 0x607D8B));
            pair(tanks, b(-4, 10, 2.8f, -2, 11.5f, 4.6f, 0x37474F));
            List<Box> fire = new ArrayList<>();
            pair(fire, glow(-3.8f, 11.5f, 3, -2.2f, 14.5f, 4.4f, 0xFF9800));
            add(new Cosmetic("back_jet", "Реактивный ранец", Slot.BACK, Anchor.BODY, Items.FIREWORK_ROCKET,
                part(tanks), part(fire).bob(1f, 0.5f)));
        }

        // ---------------- ХВОСТЫ
        add(new Cosmetic("tail_fox", "Лисий хвост", Slot.TAIL, Anchor.BODY, Items.ORANGE_WOOL, part(
            b(-1.6f, 9.5f, 2.2f, 1.6f, 12.5f, 4.4f, 0xE67E22),
            b(-2, 10, 4.2f, 2, 13.5f, 7.2f, 0xE67E22),
            b(-2.2f, 10.5f, 7, 2.2f, 14, 9.4f, 0xE67E22),
            b(-1.8f, 11, 9.2f, 1.8f, 13.8f, 11, 0xFFFFFF)).rot('y', 0, 10.5f, 2.2f, 0, 12, 0.1f)));
        add(new Cosmetic("tail_cat", "Кошачий хвост", Slot.TAIL, Anchor.BODY, Items.STRING, part(
            b(-0.8f, 10, 2.2f, 0.8f, 11.6f, 4.2f, 0x37474F),
            b(-0.8f, 9, 4, 0.8f, 10.6f, 6, 0x37474F),
            b(-0.8f, 7, 5.8f, 0.8f, 9, 7.4f, 0x37474F),
            b(-0.8f, 5.6f, 6.6f, 0.8f, 7.4f, 8, 0xFFFFFF)).rot('y', 0, 10.5f, 2.2f, 0, 14, 0.12f)));
        add(new Cosmetic("tail_devil", "Хвост дьявола", Slot.TAIL, Anchor.BODY, Items.NETHER_WART, part(
            b(-0.6f, 10, 2.2f, 0.6f, 11.2f, 4.5f, 0xC62828),
            b(-0.6f, 10.8f, 4.3f, 0.6f, 12, 7, 0xC62828),
            b(-1.6f, 10.2f, 7, 1.6f, 13, 8.4f, 0xB71C1C)).rot('y', 0, 10.5f, 2.2f, 0, 10, 0.09f)));

        // ---------------- ПИТОМЦЫ (парят у плеча)
        add(new Cosmetic("pet_slime", "Слайм", Slot.PET, Anchor.BODY, Items.SLIME_BALL, part(
            b(8, -6, -1, 13, -1, 4, 0x66BB6A),
            b(9, -5, 0, 12, -2, 3, 0x2E7D32),
            b(9, -4.5f, -1.4f, 10, -3.5f, -1, 0x111111),
            b(11, -4.5f, -1.4f, 12, -3.5f, -1, 0x111111)).bob(1.2f, 0.1f)));
        add(new Cosmetic("pet_bee", "Пчёлка", Slot.PET, Anchor.BODY, Items.HONEYCOMB, part(
            b(8, -6, -1, 13, -2.5f, 3, 0xFFC107),
            b(9, -6.2f, -1.2f, 10, -2.3f, 3.2f, 0x111111),
            b(11, -6.2f, -1.2f, 12, -2.3f, 3.2f, 0x111111),
            b(8, -5, -1.3f, 9, -4, -1, 0x111111),
            b(10, -4, 3, 11, -3, 4.2f, 0x111111),
            b(9, -7.8f, 0, 10.4f, -6, 3, 0xFFFFFF),
            b(10.6f, -7.8f, 0, 12, -6, 3, 0xFFFFFF)).bob(1.6f, 0.2f)));
        add(new Cosmetic("pet_dragon", "Дракончик", Slot.PET, Anchor.BODY, Items.DRAGON_HEAD, part(
            b(8, -5.5f, -0.5f, 12.5f, -2, 3.5f, 0xD32F2F),
            b(12.3f, -6.5f, -1, 15.5f, -3, 2.5f, 0xE53935),
            b(13, -8, -0.8f, 13.8f, -6.5f, 0, GOLD),
            b(14.4f, -8, -0.8f, 15.2f, -6.5f, 0, GOLD),
            glow(15.5f, -5.5f, -0.4f, 15.9f, -4.5f, 0.4f, 0xFFEB3B),
            b(9, -8.5f, 0, 10, -5.5f, 3.5f, 0x7F0000),
            b(10.5f, -8.5f, 0, 11.5f, -5.5f, 3.5f, 0x7F0000),
            b(7.2f, -4, 0.5f, 8, -3, 2.5f, 0xD32F2F)).bob(1.4f, 0.12f)));
        add(new Cosmetic("pet_ghost", "Привидение", Slot.PET, Anchor.BODY, Items.GHAST_TEAR, part(
            b(8, -7, -1, 12.5f, -1, 3.5f, 0xF5F5F5),
            b(8, -1, -1, 9.4f, 0.6f, 3.5f, 0xF5F5F5),
            b(10.2f, -1, -1, 11.4f, 0.6f, 3.5f, 0xF5F5F5),
            b(9, -5.5f, -1.3f, 10, -4, -1, 0x111111),
            b(11, -5.5f, -1.3f, 12, -4, -1, 0x111111),
            b(9.8f, -3.2f, -1.3f, 11.2f, -2.2f, -1, 0x111111)).bob(2f, 0.08f)));
    }
}
