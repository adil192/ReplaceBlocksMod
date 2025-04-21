package com.adilhanney.replaceblocksmod;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public abstract class ReplaceBlocksUtils {
  public static MutableText getPlayerName(ServerPlayerEntity player) {
    if (player == null) return Text.translatable("feedback.replaceblocksmod.fallbackPlayerName");

    final var displayName = player.getDisplayName();
    if (displayName != null) return displayName.copy();

    return player.getName().copy();
  }
}
