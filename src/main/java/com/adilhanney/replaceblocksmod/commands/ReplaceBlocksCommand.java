package com.adilhanney.replaceblocksmod.commands;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

public abstract class ReplaceBlocksCommand {
  public static void initialize() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
        literal("replace")
            .requires(source -> source.hasPermissionLevel(2))
            .then(argument("sourceBlock", BlockStateArgumentType.blockState(registryAccess))
                .then(literal("with")
                    .then(argument("targetBlock", BlockStateArgumentType.blockState(registryAccess))
                        .executes(context -> {
                          final var sourceBlockState = BlockStateArgumentType.getBlockState(context, "sourceBlock").getBlockState();
                          final var targetBlockState = BlockStateArgumentType.getBlockState(context, "targetBlock").getBlockState();
                          return replaceBlocks(context, sourceBlockState, targetBlockState);
                        })
                    )))));
  }

  private static final int X_RADIUS = 50;
  private static final int Z_RADIUS = X_RADIUS;
  private static final int Y_RADIUS = 10;

  /**
   * Executes the replace blocks command, replacing all the nearby source blocks
   * with the target block in the game world.
   *
   * @param context          The command context
   * @param sourceBlockState The source block to be replaced
   * @param targetBlockState The desired target block
   * @return The number of blocks replaced
   */
  private static int replaceBlocks(
      CommandContext<ServerCommandSource> context,
      BlockState sourceBlockState,
      BlockState targetBlockState
  ) {
    final var source = context.getSource();
    final var player = source.getPlayer();
    final var sourceBlock = sourceBlockState.getBlock();
    final var targetBlock = targetBlockState.getBlock();

    if (sourceBlockState.isAir()) {
      // Name and shame the player for griefing
      source.sendFeedback(() -> {
        final var playerName = player != null ? player.getName() : Text.literal("anon");
        final var targetBlockName = targetBlock.getName();
        return Text.literal("Replacing air with ")
            .append(targetBlockName)
            .append(" is not allowed, ")
            .append(playerName)
            .append("!");
      }, true);
      return 0;
    }

    final var world = source.getWorld();
    final var playerPos = player != null ? player.getBlockPos() : world.getSpawnPos();

    var replaced = 0;
    for (var dx = -X_RADIUS; dx <= X_RADIUS; dx++) {
      for (var dz = -Z_RADIUS; dz <= Z_RADIUS; dz++) {
        for (var dy = -Y_RADIUS; dy <= Y_RADIUS; dy++) {
          var blockPos = playerPos.add(dx, dy, dz);
          if (world.getBlockState(blockPos).isOf(sourceBlock)) {
            world.setBlockState(blockPos, targetBlock.getDefaultState());
            ++replaced;
          }
        }
      }
    }

    final var finalReplaced = replaced;
    source.sendFeedback(() -> {
      final var playerName = player != null ? player.getName().copy() : Text.literal("anon");
      return playerName
          .append(" replaced ")
          .append(Integer.toString(finalReplaced))
          .append(" ")
          .append(sourceBlock.getName())
          .append(" with ")
          .append(targetBlock.getName());
    }, finalReplaced > 0);
    return finalReplaced;
  }

}
