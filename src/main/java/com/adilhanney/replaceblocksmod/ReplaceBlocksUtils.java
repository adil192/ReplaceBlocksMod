package com.adilhanney.replaceblocksmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class ReplaceBlocksUtils {
  public static MutableText getPlayerName(ServerPlayerEntity player) {
    if (player == null) return Text.translatable("feedback.replaceblocksmod.fallbackPlayerName");

    final var displayName = player.getDisplayName();
    if (displayName != null) return displayName.copy();

    return player.getName().copy();
  }

  public static BlockState createBlockState(Block block, BlockPos blockPos, ServerPlayerEntity player) {
    return block.getPlacementState(new ItemPlacementContext(player, Hand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(
            //#if MC>=12101
            blockPos.toBottomCenterPos(),
            //#else
            //$$blockPos.toCenterPos(),
            //#endif
            Direction.UP, blockPos, false)
    ));
  }
}
