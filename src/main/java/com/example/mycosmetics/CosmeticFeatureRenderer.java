package com.example.mycosmetics;

import com.example.mycosmetics.Cosmetic.Slot;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Рисует выбранную косметику на локальном игроке (на других игроках её не видно). */
public class CosmeticFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private static final Identifier WHITE = Identifier.of("mycosmetics", "textures/white.png");

    public CosmeticFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       PlayerEntityRenderState state, float yaw, float pitch) {
        if (!((CosmeticStateAccess) (Object) state).mycosmetics$isLocal()) return;

        CosmeticsConfig cfg = CosmeticsConfig.get();
        if (!cfg.enabled) return;
        if (state.invisible && !cfg.showWhenInvisible) return;

        PlayerEntityModel model = getContextModel();
        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(WHITE));

        for (Slot slot : Slot.values()) {
            Cosmetic c = Cosmetics.get(cfg.selected.get(slot.name()));
            if (c == null || c.slot != slot) continue;

            matrices.push();
            if (c.anchor == Cosmetic.Anchor.HEAD) {
                model.head.rotate(matrices);
            } else {
                model.body.rotate(matrices);
            }
            c.draw(matrices, vc, light, state.age);
            matrices.pop();
        }
    }
}
