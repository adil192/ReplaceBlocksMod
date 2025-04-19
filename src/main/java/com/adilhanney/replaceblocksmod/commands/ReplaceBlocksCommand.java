package com.adilhanney.replaceblocksmod.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.*;

public abstract class ReplaceBlocksCommand {
  public static void initialize() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
        literal("replace")
            .requires(source -> source.hasPermissionLevel(2))
            .then(argument("sourceBlock", StringArgumentType.string())
                .then(literal("with")
                    .then(argument("targetBlock", StringArgumentType.string())
                        .executes(ReplaceBlocksCommand::execute)
                    )))));
  }

  private static final int X_RADIUS = 50;
  private static final int Y_RADIUS = 10;
  private static final int Z_RADIUS = X_RADIUS;

  /**
   * Executes the replace blocks command, replacing all the nearby source blocks
   * with the target block in the game world.
   *
   * @param context the command context containing the server command source
   * @return The number of blocks replaced
   */
  private static int execute(CommandContext<ServerCommandSource> context) {
    final var source = context.getSource();

    // Try to parse source block
    final var sourceBlockArg = StringArgumentType.getString(context, "sourceBlock");
    final var sourceBlockId = Identifier.tryParse(sourceBlockArg);
    if (sourceBlockId == null || !Registries.BLOCK.containsId(sourceBlockId)) {
      source.sendFeedback(() -> Text.literal("Invalid source block: " + sourceBlockArg), false);
      return 0;
    }
    final var sourceBlock = Registries.BLOCK.get(sourceBlockId);

    // Try to parse target block
    final var targetBlockArg = StringArgumentType.getString(context, "targetBlock");
    final var targetBlockId = Identifier.tryParse(targetBlockArg);
    if (targetBlockId == null || !Registries.BLOCK.containsId(targetBlockId)) {
      source.sendFeedback(() -> Text.literal("Invalid target block: " + targetBlockArg), false);
      return 0;
    }
    final var targetBlock = Registries.BLOCK.get(targetBlockId);

    final var world = source.getWorld();
    final var player = source.getPlayer();
    final var playerPos = player != null ? player.getBlockPos() : world.getSpawnPos();

    var replaced = 0;
    for (var dx = -X_RADIUS; dx <= X_RADIUS; dx++) {
      for (var dy = -Y_RADIUS; dy <= Y_RADIUS; dy++) {
        for (var dz = -Z_RADIUS; dz <= Z_RADIUS; dz++) {
          var blockPos = playerPos.add(dx, dy, dz);
          if (world.getBlockState(blockPos).isOf(sourceBlock)) {
            world.setBlockState(blockPos, targetBlock.getDefaultState());
            ++replaced;
          }
        }
      }
    }

    final var finalReplaced = replaced;
    source.sendFeedback(() -> Text.literal("Replaced " + finalReplaced + " blocks"), true);
    return finalReplaced;
  }

}
