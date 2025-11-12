package com.github.talrey.createdeco.api;

import com.github.talrey.createdeco.blocks.CoinStackBlock;
import com.github.talrey.createdeco.items.CoinStackItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Locale;

/**
 * Factory API for creating and registering Coin items and blocks.
 *
 * Coins system has three components:
 * 1. Coin Item - basic decorative item (4 coins = 1 coinstack item)
 * 2. CoinStack Item - placeable item that creates coinstack blocks
 * 3. CoinStack Block - layered block (1-8 layers like snow)
 *
 * Each metal type creates all three components.
 *
 * Note: Complex loot tables (layer-based drops) must be created as JSON files.
 */
public class Coins {

  /**
   * Registers a coin item.
   *
   * @param metal The metal type (e.g., "Iron", "Gold", "Netherite")
   * @return The registered coin item
   */
  public static Item registerCoinItem(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_coin";

    Item.Settings settings = new Item.Settings();
    if (metal.contains("Netherite")) {
      settings.fireproof();
    }

    Item coin = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      new Item(settings)
    );

    // TODO: Create recipe JSON - 1 coinstack → 4 coins (shapeless)

    return coin;
  }

  /**
   * Registers a coinstack item that can place coinstack blocks.
   *
   * @param metal The metal type (e.g., "Iron", "Gold", "Netherite")
   * @return The registered coinstack item
   */
  public static Item registerCoinStackItem(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_coinstack";

    Item.Settings settings = new Item.Settings();
    if (metal.contains("Netherite")) {
      settings.fireproof();
    }

    CoinStackItem coinstack = new CoinStackItem(settings, metal);

    Item registered = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", regName),
      coinstack
    );

    // TODO: Create recipe JSON - 4 coins → 1 coinstack (shapeless)

    return registered;
  }

  /**
   * Registers a coinstack block (layered like snow, 1-8 layers).
   *
   * @param metal The metal type (e.g., "Iron", "Gold", "Netherite")
   * @return The registered coinstack block
   */
  public static Block registerCoinStackBlock(String metal) {
    String regName = metal.toLowerCase(Locale.ROOT).replaceAll(" ", "_") + "_coinstack";

    CoinStackBlock block = new CoinStackBlock(
      AbstractBlock.Settings.create()
        .nonOpaque()
        .strength(0.5f)
        .sounds(BlockSoundGroup.CHAIN),
      metal
    );

    Block registered = Registry.register(
      Registries.BLOCK,
      Identifier.of("createdeco", regName),
      block
    );

    // Note: No BlockItem is registered - CoinStackItem handles placement

    // TODO: Create complex loot table JSON:
    // - Drops 1-8 coinstack items based on LAYERS property
    // - Uses conditional SetItemCountFunction per layer value

    return registered;
  }

  /**
   * Convenience method to register all three coin components for a metal.
   *
   * @param metal The metal type
   * @return Array containing [coin item, coinstack item, coinstack block]
   */
  public static Object[] registerAll(String metal) {
    Item coin = registerCoinItem(metal);
    Item coinstack = registerCoinStackItem(metal);
    Block coinstackBlock = registerCoinStackBlock(metal);

    return new Object[] { coin, coinstack, coinstackBlock };
  }
}
