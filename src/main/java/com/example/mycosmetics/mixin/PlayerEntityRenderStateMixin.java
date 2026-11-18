package com.example.mycosmetics.mixin;

import com.example.mycosmetics.CosmeticStateAccess;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements CosmeticStateAccess {
    @Unique
    private boolean mycosmetics$local;

    @Override
    public boolean mycosmetics$isLocal() {
        return mycosmetics$local;
    }

    @Override
    public void mycosmetics$setLocal(boolean local) {
        this.mycosmetics$local = local;
    }
}
