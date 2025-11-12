package com.github.talrey.createdeco.api;

import com.github.talrey.createdeco.CreateDecoMod;
import com.github.talrey.createdeco.blocks.CageLampBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.util.Locale;

public class CageLamps {

  public static final Identifier YELLOW_ON  = CreateDecoMod.id( "block/palettes/cage_lamp/light_default");
  public static final Identifier YELLOW_OFF = CreateDecoMod.id( "block/palettes/cage_lamp/light_default_off");
  public static final Identifier RED_ON     = CreateDecoMod.id( "block/palettes/cage_lamp/light_redstone");
  public static final Identifier RED_OFF    = CreateDecoMod.id( "block/palettes/cage_lamp/light_redstone_off");
  public static final Identifier GREEN_ON   = CreateDecoMod.id( "block/palettes/cage_lamp/light_green");
  public static final Identifier GREEN_OFF  = CreateDecoMod.id( "block/palettes/cage_lamp/light_green_off");
  public static final Identifier BLUE_ON    = CreateDecoMod.id( "block/palettes/cage_lamp/light_soul");
  public static final Identifier BLUE_OFF   = CreateDecoMod.id( "block/palettes/cage_lamp/light_soul_off");

  /**
   * Creates and registers a cage lamp block and its item.
   *
   * @param metal Metal type name (e.g. "Brass", "Iron")
   * @param color DyeColor for the lamp
   * @return The registered Block
   */
  public static Block createAndRegister(String metal, DyeColor color) {
    String cleanMetal = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_");
    String colorName = color.getName().toLowerCase(Locale.ROOT);
    String blockId = colorName + "_" + cleanMetal + "_lamp";

    // Determine particle color based on dye color
    Vector3f particleColor = switch (color) {
      case YELLOW -> new Vector3f(1.0f, 1.0f, 0.0f);      // Yellow
      case RED -> new Vector3f(1.0f, 0.0f, 0.0f);         // Red
      case GREEN -> new Vector3f(0.0f, 1.0f, 0.0f);       // Green
      case BLUE -> new Vector3f(0.0f, 0.5f, 1.0f);        // Soul blue
      default -> new Vector3f(0.3f, 0.3f, 0.0f);
    };

    // Register block
    Block block = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", blockId),
      new CageLampBlock(
        AbstractBlock.Settings.create()
          .nonOpaque()
          .strength(0.5f)
          .sounds(BlockSoundGroup.LANTERN)
          .luminance(state -> state.get(Properties.LIT) ? 15 : 0),
        particleColor
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
