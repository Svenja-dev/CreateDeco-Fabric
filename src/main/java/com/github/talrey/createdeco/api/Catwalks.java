package com.github.talrey.createdeco.api;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.blocks.CatwalkRailingBlock;
import com.github.talrey.createdeco.blocks.CatwalkStairBlock;
import com.github.talrey.createdeco.items.CatwalkBlockItem;
import com.github.talrey.createdeco.items.CatwalkStairBlockItem;
import com.github.talrey.createdeco.items.RailingBlockItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

/**
 * Factory API for creating and registering Catwalk blocks.
 *
 * Catwalks are industrial walkways with three variants:
 * - Catwalk: Main platform block with connected textures
 * - CatwalkStair: Angled stairs that can have railings attached
 * - CatwalkRailing: Modular railing system that connects on 4 sides
 *
 * Each metal type creates all three blocks.
 *
 * Note: Loot tables for railings are complex (conditional drops based on states)
 * and must be created as JSON files in data/createdeco/loot_tables/blocks/
 */
public class Catwalks {

  /**
   * Creates and registers a catwalk block with its custom item.
   *
   * @param metal The metal type (e.g., "Iron", "Copper")
   * @return The registered catwalk block
   */
  public static Block createAndRegisterCatwalk(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_catwalk";

    // Create block
    CatwalkBlock block = new CatwalkBlock(
      AbstractBlock.Settings.create()
        .strength(5.0f, 6.0f)
        .requiresTool()
        .nonOpaque()
        .sounds(BlockSoundGroup.NETHERITE)
    );

    // Register block
    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      block
    );

    // Register custom item with placement helper
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new CatwalkBlockItem(
        block,
        new Item.Settings().fireproof() // All catwalks are fireproof for consistency
      )
    );

    // TODO: Register connected textures via Create's system
    // TODO: Create loot table JSON (simple - drops 1 block)

    return registered;
  }

  /**
   * Creates and registers a catwalk stair block with its custom item.
   *
   * @param metal The metal type (e.g., "Iron", "Copper")
   * @return The registered catwalk stair block
   */
  public static Block createAndRegisterCatwalkStair(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_catwalk_stairs";

    // Create block
    CatwalkStairBlock block = new CatwalkStairBlock(
      AbstractBlock.Settings.create()
        .strength(5.0f, 6.0f)
        .requiresTool()
        .nonOpaque()
        .sounds(BlockSoundGroup.NETHERITE),
      metal
    );

    // Register block
    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      block
    );

    // Register custom item with placement helper
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new CatwalkStairBlockItem(
        block,
        new Item.Settings()
      )
    );

    // TODO: Create complex loot table JSON:
    // - Always drops 1 stair
    // - Conditionally drops 0-2 railings based on RAILING_LEFT/RIGHT properties

    return registered;
  }

  /**
   * Creates and registers a catwalk railing block with its custom item.
   *
   * @param metal The metal type (e.g., "Iron", "Copper")
   * @return The registered catwalk railing block
   */
  public static Block createAndRegisterCatwalkRailing(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_catwalk_railing";

    // Create block
    CatwalkRailingBlock block = new CatwalkRailingBlock(
      AbstractBlock.Settings.create()
        .strength(5.0f, 6.0f)
        .requiresTool()
        .nonOpaque()
        .sounds(BlockSoundGroup.NETHERITE)
    );

    // Register block
    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      block
    );

    // Register custom item with placement helper
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new RailingBlockItem(
        block,
        new Item.Settings()
      )
    );

    // TODO: Create complex loot table JSON:
    // - Drops 0-4 railings based on which NORTH/SOUTH/EAST/WEST_FENCE properties are true
    // - Uses additive SetItemCountFunction with conditions per direction

    return registered;
  }
}
