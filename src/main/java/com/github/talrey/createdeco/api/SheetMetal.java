package com.github.talrey.createdeco.api;

import com.zurrtum.create.content.decoration.palettes.ConnectedPillarBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class SheetMetal {
  /**
   * Creates and registers a sheet metal block and its item.
   * Sheet metal uses Create's ConnectedPillarBlock for connected textures.
   *
   * @param metal Metal type name (e.g. "Brass", "Zinc")
   * @return The registered Block
   */
  public static Block createAndRegister(String metal) {
    String blockId = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_sheet_metal";

    // Register block using Create's ConnectedPillarBlock
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", blockId),
      new ConnectedPillarBlock(
        AbstractBlock.Settings.create()
          .strength(5.0f, 6.0f)
          .requiresTool()
          .sounds(BlockSoundGroup.NETHERITE)
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
