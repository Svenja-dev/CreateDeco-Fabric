package com.github.talrey.createdeco.api;

import com.zurrtum.create.content.decoration.MetalLadderBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class Ladders {
  /**
   * Creates and registers a metal ladder block and its item.
   * Uses Create-Fly's MetalLadderBlock class.
   *
   * @param metal Metal type name (e.g. "Zinc", "Iron")
   * @return The registered Block
   */
  public static Block createAndRegister(String metal) {
    String blockId = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_ladder";

    // Register block (using Create-Fly's MetalLadderBlock)
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", blockId),
      new MetalLadderBlock(
        AbstractBlock.Settings.copy(Blocks.LADDER)
          .sounds(BlockSoundGroup.COPPER)
      )
    );

    // Register item
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", blockId),
      new BlockItem(block, new Item.Settings())
    );

    return block;
  }
}
