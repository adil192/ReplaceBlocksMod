package com.adilhanney.replaceblocksmod.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static com.adilhanney.replaceblocksmod.ReplaceBlocksUtils.*;
import static net.minecraft.server.command.CommandManager.*;

public class RemoveGrassCommand {
  public static void initialize() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
        literal("removegrass")
            .requires(source -> source.hasPermissionLevel(1))
            .executes(RemoveGrassCommand::removeGrass)));
  }

  private static final int X_RADIUS = 50;
  private static final int Z_RADIUS = X_RADIUS;
  private static final int Y_RADIUS = 10;

  /**
   * Removes short and tall grass near the player.
   * Does not remove normal grass blocks.
   *
   * @param context The command context
   * @return The number of blocks removed
   */
  public static int removeGrass(CommandContext<ServerCommandSource> context) {
    final var source = context.getSource();
    final var player = source.getPlayer();
    final var world = source.getWorld();
    final var playerPos = player != null ? player.getBlockPos() : world.getSpawnPos();

    var removed = 0;
    for (var dx = -X_RADIUS; dx <= X_RADIUS; dx++) {
      for (var dz = -Z_RADIUS; dz <= Z_RADIUS; dz++) {
        for (var dy = -Y_RADIUS; dy <= Y_RADIUS; dy++) {
          final var blockPos = playerPos.add(dx, dy, dz);
          final var oldBlockState = world.getBlockState(blockPos);
          if (oldBlockState.isOf(Blocks.SHORT_GRASS) || oldBlockState.isOf(Blocks.TALL_GRASS)) {
            final var newBlockState = Blocks.AIR.getDefaultState();
            Block.replace(oldBlockState, newBlockState, world, blockPos, Block.NOTIFY_ALL_AND_REDRAW);
            ++removed;
          } else if (dy > 0 && oldBlockState.isAir()) {
            // There probably is no grass above here, so move on to next y
            break;
          }
        }
      }
    }

    final var finalRemoved = removed;
    source.sendFeedback(() -> {
      final var playerName = getPlayerName(player);
      return Text.translatable(
          "feedback.replaceblocksmod.removedGrass",
          playerName,
          finalRemoved
      );
    }, finalRemoved > 0);
    return finalRemoved;
  }

}
