package com.github.talrey.createdeco.api;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PaneBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class Bars {
  /**
   * Creates and registers a bar block (normal bars without overlay).
   *
   * @param metal Metal type name (e.g. "Brass", "Zinc")
   * @return The registered Block
   */
  public static Block createAndRegisterBar(String metal) {
    String base = metal.replace(' ', '_').toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_bars";

    // Register block
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", base),
      new PaneBlock(
        AbstractBlock.Settings.create()
          .nonOpaque()
          .strength(5.0f, 6.0f)
          .requiresTool()
          .sounds(BlockSoundGroup.NETHERITE)
      )
    );

    // Register item
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", base),
      new BlockItem(block, new Item.Settings())
    );

    return block;
  }

  /**
   * Creates and registers a bar panel block (overlay variant).
   *
   * @param metal Metal type name (e.g. "Brass", "Zinc")
   * @return The registered Block
   */
  public static Block createAndRegisterPanel(String metal) {
    String base = metal.replace(' ', '_').toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_bars_overlay";

    // Register block
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", base),
      new PaneBlock(
        AbstractBlock.Settings.create()
          .nonOpaque()
          .strength(5.0f, 6.0f)
          .requiresTool()
          .sounds(BlockSoundGroup.NETHERITE)
      )
    );

    // Register item
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", base),
      new BlockItem(block, new Item.Settings())
    );

    return block;
  }
}
