package com.github.talrey.createdeco.api;

import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Locale;

/**
 * Factory API for creating and registering Window and Window Pane blocks.
 *
 * Windows are decorative glass blocks with metal frames that use Create's WindowBlock class.
 * Window panes are thin versions that connect like vanilla glass panes.
 *
 * Both types support connected textures via Create's system.
 *
 * Note: Connected textures and silk touch loot tables need separate handling.
 */
public class Windows {

  /**
   * Creates and registers a window block.
   *
   * @param metal The metal type (e.g., "Iron", "Copper")
   * @return The registered window block
   */
  public static Block createAndRegisterWindow(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_window";

    // Create window block (not translucent)
    WindowBlock block = new WindowBlock(
      AbstractBlock.Settings.copy(Blocks.GLASS)
        .nonOpaque()
        .allowsSpawning((state, world, pos, entityType) -> false)
        .solidBlock((state, world, pos) -> false)
        .suffocates((state, world, pos) -> false)
        .blockVision((state, world, pos) -> false),
      false  // translucent = false for standard windows
    );

    // Register block
    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      block
    );

    // Register item
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new BlockItem(registered, new Item.Settings())
    );

    // TODO: Register connected textures via Create's HorizontalCTBehaviour
    // TODO: Create silk touch loot table JSON

    return registered;
  }

  /**
   * Creates and registers a window pane block.
   *
   * Window panes are created from windows and connect like vanilla glass panes.
   *
   * @param metal The metal type (e.g., "Iron", "Copper")
   * @param parentWindow The parent window block used for map color
   * @return The registered window pane block
   */
  public static Block createAndRegisterWindowPane(String metal, Block parentWindow) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_window_pane";

    // Create connected glass pane block
    ConnectedGlassPaneBlock block = new ConnectedGlassPaneBlock(
      AbstractBlock.Settings.copy(Blocks.GLASS_PANE)
        .mapColor(parentWindow.getDefaultMapColor())
    );

    // Register block
    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      block
    );

    // Register item
    Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new BlockItem(registered, new Item.Settings())
    );

    // TODO: Register connected textures via Create's GlassPaneCTBehaviour
    // TODO: Create silk touch loot table JSON

    return registered;
  }
}
