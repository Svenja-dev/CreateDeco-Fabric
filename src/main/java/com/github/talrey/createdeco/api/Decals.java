package com.github.talrey.createdeco.api;

import com.github.talrey.createdeco.blocks.DecalBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Decals {
  public static List<String> TYPES = Arrays.asList(
      "warning", "creeper", "skull", "flow", "ice", "radioactive",
      "top_left", "up", "top_right", "left", "cross", "right", "down_left", "down", "down_right",
      "fluid", "fire", "electrical",
      "fire_diamond", "no_entry"
  );

  private static List<String> CAPITALS = Arrays.asList(
      "Warning", "Creeper", "Skull", "Flow", "Ice", "Radioactive",
      "Up Left Arrow", "Up Arrow", "Up Right Arrow", "Left Arrow", "Cross", "Right Arrow", "Down Left Arrow", "Down Arrow", "Down Right Arrow",
      "Fluid", "Fire", "Electrical",
      "Fire Diamond", "No Entry"
  );

  /**
   * Creates and registers all decal blocks and their items.
   * Decals are wall-mounted decorative blocks.
   *
   * @return List of all registered decal blocks
   */
  public static List<Block> createAndRegisterAll() {
    List<Block> decals = new ArrayList<>();

    for (int i = 0; i < TYPES.size(); i++) {
      String type = TYPES.get(i);
      String name = CAPITALS.get(i);
      String blockId = "decal_" + type;

      // Register block
      Block block = Registry.register(
        Registries.BLOCK,
        Identifier.of("createdeco", blockId),
        new DecalBlock(
          AbstractBlock.Settings.create()
            .strength(2.0f, 2.0f)
            .sounds(BlockSoundGroup.LANTERN)
            .nonOpaque()
            .breakInstantly()
        )
      );

      // Register item
      Registry.register(
        Registries.ITEM,
        Identifier.of("createdeco", blockId),
        new BlockItem(block, new Item.Settings())
      );

      decals.add(block);
    }

    return decals;
  }
}
