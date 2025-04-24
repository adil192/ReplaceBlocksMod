package com.adilhanney.replaceblocksmod.test.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static com.adilhanney.replaceblocksmod.commands.RemoveGrassCommand.*;

public class RemoveGrassCommandTest {
  static final BlockPos playerPos = new BlockPos(100, 200, 300);

  @BeforeAll
  static void beforeAll() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }

  @Test
  void testRemoveGrass() {
    final var context = createCommandContext();

    final var removedBlockPositions = new ArrayList<BlockPos>();
    final var world = context.getSource().getWorld();
    Mockito.when(world.breakBlock(Mockito.any(), Mockito.anyBoolean(), Mockito.isNull(), Mockito.anyInt())).then(invocation -> {
      removedBlockPositions.add(invocation.getArgument(0));
      return null;
    });

    final var removed = removeGrass(context);
    Assertions.assertEquals(3, removed);

    for (var blockPos : removedBlockPositions) {
      Assertions.assertAll(
          () -> Assertions.assertEquals(playerPos.getY(), blockPos.getY()),
          () -> Assertions.assertEquals(playerPos.getZ(), blockPos.getZ()),
          () -> Assertions.assertTrue(playerPos.getX() - 1 <= blockPos.getX() && blockPos.getX() <= playerPos.getX() + 1)
      );
    }
    Assertions.assertEquals(removed, removedBlockPositions.size());
  }

  private CommandContext<ServerCommandSource> createCommandContext() {
    final var world = Mockito.mock(ServerWorld.class);
    mockWorldBlockStates(world);

    final var player = Mockito.mock(ServerPlayerEntity.class);
    Mockito.when(player.getWorld()).thenReturn(world);
    Mockito.when(player.getBlockPos()).thenReturn(playerPos);

    final var source = Mockito.mock(ServerCommandSource.class);
    Mockito.when(source.getPlayer()).thenReturn(player);
    Mockito.when(source.getWorld()).thenReturn(world);

    return new CommandContext<>(source, null, null, null, null, null, null, null, null, false);
  }

  private void mockWorldBlockStates(ServerWorld world) {
    Mockito.when(world.getBlockState(Mockito.any())).then(invocation -> {
      final BlockPos blockPos = invocation.getArgument(0);

      if (blockPos.getY() < playerPos.getY()) {
        return Blocks.GRASS_BLOCK.getDefaultState(); // Grass below player
      } else if (blockPos.getY() > playerPos.getY()) {
        return Blocks.AIR.getDefaultState(); // Air above player
      }

      if (blockPos.equals(playerPos)) {
        return Blocks.SHORT_GRASS.getDefaultState();
      } else if (blockPos.equals(playerPos.add(1, 0, 0)) || blockPos.equals(playerPos.add(-1, 0, 0))) {
        return Blocks.TALL_GRASS.getDefaultState();
      } else {
        return Blocks.AIR.getDefaultState();
      }
    });
  }
}
