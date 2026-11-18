package com.example.mycosmetics;

/** Метка на состоянии рендера игрока: "это наш локальный игрок". */
public interface CosmeticStateAccess {
    boolean mycosmetics$isLocal();

    void mycosmetics$setLocal(boolean local);
}
