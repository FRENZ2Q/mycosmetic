package com.example.mycosmetics;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

/**
 * Косметика = набор цветных блоков (в пикселях текстуры, 1 пиксель = 1/16 блока),
 * привязанных к голове или к телу. Система координат модели игрока:
 *  голова: x -4..4, y -8..0 (минус = вверх), z -4..4 (лицо смотрит в -z)
 *  тело:   x -4..4, y 0..12, z -2..2 (спина = +z)
 */
public final class Cosmetic {

    public enum Slot {
        CAPE("Плащ"), HAT("Шапка"), MASK("Маска"), BACK("Спина"), TAIL("Хвост"), PET("Питомец");

        public final String label;

        Slot(String label) {
            this.label = label;
        }
    }

    public enum Anchor { HEAD, BODY }

    /** Один цветной блок. glow = светится в темноте. */
    public record Box(float x1, float y1, float z1, float x2, float y2, float z2, int rgb, boolean glow) {}

    /** Группа блоков с необязательной анимацией (качание вокруг оси и/или парение). */
    public static final class Part {
        final List<Box> boxes;
        boolean rotates = false;
        char axis = 'y';
        float px, py, pz, baseDeg, swayDeg, swaySpeed = 0.1f;
        float bobAmp = 0f, bobSpeed = 0.1f;
        float phase = 0f;

        public Part(Box... boxes) {
            this.boxes = List.of(boxes);
        }

        /** Качание вокруг оси axis с центром в (px,py,pz): угол = baseDeg + swayDeg * sin(время * speed). */
        public Part rot(char axis, float px, float py, float pz, float baseDeg, float swayDeg, float speed) {
            this.rotates = true;
            this.axis = axis;
            this.px = px;
            this.py = py;
            this.pz = pz;
            this.baseDeg = baseDeg;
            this.swayDeg = swayDeg;
            this.swaySpeed = speed;
            return this;
        }

        /** Плавное покачивание вверх-вниз (для питомцев, нимба). */
        public Part bob(float amp, float speed) {
            this.bobAmp = amp;
            this.bobSpeed = speed;
            return this;
        }
    }

    public final String id;
    public final String name;
    public final Slot slot;
    public final Anchor anchor;
    public final Item icon;
    public final Part[] parts;

    public Cosmetic(String id, String name, Slot slot, Anchor anchor, Item icon, Part... parts) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.anchor = anchor;
        this.icon = icon;
        this.parts = parts;
    }

    // ------------------------------------------------------------ отрисовка

    private static final int FULL_LIGHT = 0xF000F0;

    public void draw(MatrixStack matrices, VertexConsumer vc, int light, float age) {
        for (Part p : parts) {
            matrices.push();
            if (p.bobAmp != 0f) {
                matrices.translate(0.0, p.bobAmp / 16.0 * Math.sin(age * p.bobSpeed + p.phase), 0.0);
            }
            if (p.rotates) {
                matrices.translate(p.px / 16f, p.py / 16f, p.pz / 16f);
                float ang = p.baseDeg + (float) (p.swayDeg * Math.sin(age * p.swaySpeed + p.phase));
                matrices.multiply(axisOf(p.axis).rotationDegrees(ang));
                matrices.translate(-p.px / 16f, -p.py / 16f, -p.pz / 16f);
            }
            for (Box b : p.boxes) {
                drawBox(matrices.peek(), vc, b, b.glow() ? FULL_LIGHT : light);
            }
            matrices.pop();
        }
    }

    private static RotationAxis axisOf(char a) {
        return switch (a) {
            case 'x' -> RotationAxis.POSITIVE_X;
            case 'z' -> RotationAxis.POSITIVE_Z;
            default -> RotationAxis.POSITIVE_Y;
        };
    }

    private static void drawBox(MatrixStack.Entry e, VertexConsumer vc, Box b, int light) {
        float s = 1f / 16f;
        float x1 = Math.min(b.x1(), b.x2()) * s, x2 = Math.max(b.x1(), b.x2()) * s;
        float y1 = Math.min(b.y1(), b.y2()) * s, y2 = Math.max(b.y1(), b.y2()) * s;
        float z1 = Math.min(b.z1(), b.z2()) * s, z2 = Math.max(b.z1(), b.z2()) * s;
        int r = (b.rgb() >> 16) & 255, g = (b.rgb() >> 8) & 255, bl = b.rgb() & 255;

        // -z (лицо)
        quad(e, vc, light, r, g, bl, 0, 0, -1, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1);
        // +z (спина)
        quad(e, vc, light, r, g, bl, 0, 0, 1, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2);
        // -x
        quad(e, vc, light, r, g, bl, -1, 0, 0, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2);
        // +x
        quad(e, vc, light, r, g, bl, 1, 0, 0, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1);
        // -y (верх)
        quad(e, vc, light, r, g, bl, 0, -1, 0, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1);
        // +y (низ)
        quad(e, vc, light, r, g, bl, 0, 1, 0, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2);
    }

    private static void quad(MatrixStack.Entry e, VertexConsumer vc, int light, int r, int g, int b,
                             float nx, float ny, float nz,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz) {
        vertex(e, vc, light, r, g, b, nx, ny, nz, ax, ay, az);
        vertex(e, vc, light, r, g, b, nx, ny, nz, bx, by, bz);
        vertex(e, vc, light, r, g, b, nx, ny, nz, cx, cy, cz);
        vertex(e, vc, light, r, g, b, nx, ny, nz, dx, dy, dz);
    }

    private static void vertex(MatrixStack.Entry e, VertexConsumer vc, int light, int r, int g, int b,
                               float nx, float ny, float nz, float x, float y, float z) {
        vc.vertex(e.getPositionMatrix(), x, y, z)
            .color(r, g, b, 255)
            .texture(0.5f, 0.5f)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(e, nx, ny, nz);
    }
}
