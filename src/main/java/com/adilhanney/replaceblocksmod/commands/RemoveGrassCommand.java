package com.adilhanney.replaceblocksmod.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.literal;

public class RemoveGrassCommand {
  public static void initialize() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
        literal("removegrass")
            .requires(source -> source.hasPermissionLevel(1))
            .executes(RemoveGrassCommand::removeGrass)));
  }

  private static final int X_RADIUS = 50;
  private static final int Y_RADIUS = 10;
  private static final int Z_RADIUS = X_RADIUS;

  private static final Block air = Registries.BLOCK.get(Identifier.of("minecraft:air"));
  private static final Block shortGrass = Registries.BLOCK.get(Identifier.of("minecraft:short_grass"));
  private static final Block tallGrass = Registries.BLOCK.get(Identifier.of("minecraft:tall_grass"));

  /**
   * Removes short and tall grass near the player.
   * Does not remove normal grass blocks.
   *
   * @param context The command context
   * @return The number of blocks removed
   */
  private static int removeGrass(CommandContext<ServerCommandSource> context) {
    final var source = context.getSource();
    final var player = source.getPlayer();
    final var world = source.getWorld();
    final var playerPos = player != null ? player.getBlockPos() : world.getSpawnPos();

    var removed = 0;
    for (var dx = -X_RADIUS; dx <= X_RADIUS; dx++) {
      for (var dy = -Y_RADIUS; dy <= Y_RADIUS; dy++) {
        for (var dz = -Z_RADIUS; dz <= Z_RADIUS; dz++) {
          final var blockPos = playerPos.add(dx, dy, dz);
          final var blockState = world.getBlockState(blockPos);
          if (blockState.isOf(shortGrass) || blockState.isOf(tallGrass)) {
            world.setBlockState(blockPos, air.getDefaultState());
            ++removed;
          }
        }
      }
    }

    final var finalRemoved = removed;
    source.sendFeedback(() -> Text.literal("Removed " + finalRemoved + " weeds"), finalRemoved > 0);
    return finalRemoved;
  }

}
