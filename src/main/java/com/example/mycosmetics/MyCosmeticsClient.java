package com.example.mycosmetics;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MyCosmeticsClient implements ClientModInitializer {
    public static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        CosmeticsConfig.load();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mycosmetics.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.mycosmetics"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                client.setScreen(new CosmeticsScreen(client.currentScreen));
            }
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, helper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                helper.register(new CosmeticFeatureRenderer(playerRenderer));
            }
        });
    }
}
