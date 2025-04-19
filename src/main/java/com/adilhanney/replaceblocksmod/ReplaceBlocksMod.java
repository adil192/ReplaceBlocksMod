package com.adilhanney.replaceblocksmod;

import com.adilhanney.replaceblocksmod.commands.RemoveGrassCommand;
import com.adilhanney.replaceblocksmod.commands.ReplaceBlocksCommand;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceBlocksMod implements ModInitializer {
  public static final String MOD_ID = "replaceblocksmod";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing commands for " + MOD_ID);
    ReplaceBlocksCommand.initialize();
    RemoveGrassCommand.initialize();
  }
}
