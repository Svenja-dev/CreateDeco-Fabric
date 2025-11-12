package com.github.talrey.createdeco.api;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockSetType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Locale;

/**
 * Factory API for creating and registering Door and Trapdoor blocks.
 *
 * Supports:
 * - Metal doors (can be opened by hand)
 * - Locked doors (require redstone power)
 * - Metal trapdoors
 *
 * All use vanilla DoorBlock/TrapdoorBlock classes.
 */
public class Doors {

  // BlockSetType for metal doors that can be opened by hand
  public static final BlockSetType OPEN_METAL_DOOR = BlockSetType.register(
    new BlockSetType(
      "createdeco_metal",
      true,  // canOpenByHand
      true,  // canOpenByWindCharge
      true,  // canButtonBeActivatedByArrows
      BlockSetType.ActivationRule.EVERYTHING,
      BlockSoundGroup.METAL,
      SoundEvents.BLOCK_IRON_DOOR_CLOSE,
      SoundEvents.BLOCK_IRON_DOOR_OPEN,
      SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
      SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
      SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
      SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
      SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
      SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
    )
  );

  /**
   * Creates and registers a metal door.
   *
   * @param metal The metal type (e.g., "Copper", "Brass")
   * @param locked If true, creates a locked door (requires redstone)
   * @return The registered door block
   */
  public static Block createAndRegisterDoor(String metal, boolean locked) {
    String regName = (locked ? "locked_" : "")
      + metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_")
      + "_door";

    // Locked doors use GOLD BlockSetType (requires redstone), normal doors use OPEN_METAL_DOOR
    BlockSetType setType = locked ? BlockSetType.GOLD : OPEN_METAL_DOOR;

    DoorBlock door = new DoorBlock(
      setType,
      AbstractBlock.Settings.copy(Blocks.IRON_DOOR)
        .nonOpaque()
        .strength(5.0f, 5.0f)
        .requiresTool()
        .sounds(BlockSoundGroup.NETHERITE)
    );

    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      door
    );

    // Register item
    Item.Settings itemSettings = new Item.Settings();
    if (metal.contains("Netherite")) {
      itemSettings.fireproof();
    }

    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new BlockItem(registered, itemSettings)
    );

    // TODO: Create loot table JSON (only drops when LOWER half is broken)
    // TODO: Create blockstate JSON files for doors

    return registered;
  }

  /**
   * Creates and registers a metal trapdoor.
   *
   * @param metal The metal type (e.g., "Copper", "Brass")
   * @return The registered trapdoor block
   */
  public static Block createAndRegisterTrapdoor(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_trapdoor";

    TrapdoorBlock trapdoor = new TrapdoorBlock(
      OPEN_METAL_DOOR,
      AbstractBlock.Settings.create()
        .nonOpaque()
        .strength(5.0f, 5.0f)
        .requiresTool()
        .sounds(BlockSoundGroup.NETHERITE)
    );

    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      trapdoor
    );

    // Register item
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new BlockItem(registered, new Item.Settings())
    );

    // TODO: Create loot table JSON (simple drop)
    // TODO: Create blockstate JSON files for trapdoors

    return registered;
  }
}
