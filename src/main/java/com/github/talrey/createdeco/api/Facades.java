package com.github.talrey.createdeco.api;

import com.github.talrey.createdeco.blocks.FacadeBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class Facades {
  /**
   * Creates and registers a facade block and its item.
   * Facades are multiface blocks that can be placed on multiple sides.
   *
   * @param metal Metal type name (e.g. "Brass", "Zinc")
   * @return The registered Block
   */
  public static Block createAndRegister(String metal) {
    String blockId = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_facade";

    // Register block
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", blockId),
      new FacadeBlock(
        AbstractBlock.Settings.create()
          .strength(5.0f, 6.0f)
          .requiresTool()
          .sounds(BlockSoundGroup.NETHERITE)
          .nonOpaque()
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
