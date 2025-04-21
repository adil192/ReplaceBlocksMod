package com.adilhanney.replaceblocksmod.test;

import com.google.gson.Gson;
import com.mojang.serialization.JsonOps;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.adilhanney.replaceblocksmod.ReplaceBlocksUtils.*;

public class ReplaceBlocksUtilsTest {
  private static final Gson gson = new Gson();

  @BeforeAll
  static void beforeAll() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }

  private String serializeText(Text text) {
    return gson.toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow());
  }

  private @NotNull ServerPlayerEntity mockPlayer(@Nullable String displayName, String name) {
    final var player = Mockito.mock(ServerPlayerEntity.class);
    Mockito.when(player.getDisplayName()).thenReturn(displayName != null ? Text.literal(displayName) : null);
    Mockito.when(player.getName()).thenReturn(Text.literal(name));
    return player;
  }

  @Test
  void testPlayerNameOfNull() {
    final var playerNameText = getPlayerName(null);
    final var playerNameJson = serializeText(playerNameText);
    Assertions.assertEquals("{\"translate\":\"feedback.replaceblocksmod.fallbackPlayerName\"}", playerNameJson);
  }

  @Test
  void testPlayerNameWithDisplayName() {
    final var player = mockPlayer("testDisplayName", "testName");
    final var playerNameText = getPlayerName(player);
    final var playerNameJson = serializeText(playerNameText);
    Assertions.assertEquals("\"testDisplayName\"", playerNameJson);
  }

  @Test
  void testPlayerNameWithoutDisplayName() {
    final var player = mockPlayer(null, "testName");
    final var playerNameText = getPlayerName(player);
    final var playerNameJson = serializeText(playerNameText);
    Assertions.assertEquals("\"testName\"", playerNameJson);
  }

}
