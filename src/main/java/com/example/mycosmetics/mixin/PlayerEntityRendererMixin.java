package com.example.mycosmetics.mixin;

import com.example.mycosmetics.CosmeticStateAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(
        method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void mycosmetics$markLocal(AbstractClientPlayerEntity player, PlayerEntityRenderState state,
                                       float tickDelta, CallbackInfo ci) {
        ((CosmeticStateAccess) (Object) state).mycosmetics$setLocal(player == MinecraftClient.getInstance().player);
    }
}
