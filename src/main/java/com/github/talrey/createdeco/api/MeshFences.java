package com.github.talrey.createdeco.api;

import com.github.talrey.createdeco.blocks.MeshFenceBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class MeshFences {
  /**
   * Creates and registers a mesh fence block and its item.
   *
   * @param metal Metal type name (e.g. "Brass", "Zinc")
   * @return The registered Block
   */
  public static Block createAndRegister(String metal) {
    String blockId = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_mesh_fence";

    // Register block
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", blockId),
      new MeshFenceBlock(
        AbstractBlock.Settings.create()
          .strength(5.0f, 6.0f)
          .requiresTool()
          .sounds(BlockSoundGroup.CHAIN)
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
