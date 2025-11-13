package com.github.talrey.createdeco;

import com.github.talrey.createdeco.api.Coins;
import com.github.talrey.createdeco.api.CreateDecoTags;
import com.github.talrey.createdeco.items.CoinStackItem;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;

import java.util.HashMap;
import java.util.function.Function;

public class ItemRegistry {
  public static ItemEntry<Item> ANDESITE_SHEET;
  public static ItemEntry<Item> ZINC_SHEET;
  public static ItemEntry<Item> NETHERITE_SHEET;
  public static ItemEntry<Item> NETHERITE_NUGGET;
  public static ItemEntry<Item> INDUSTRIAL_IRON_NUGGET;
  public static ItemEntry<Item> INDUSTRIAL_IRON_INGOT;
  public static ItemEntry<Item> INDUSTRIAL_IRON_SHEET;

  public static HashMap<String, Function<String, Item>> METAL_TYPES = new HashMap<>();
  public static HashMap<String, Function<String, Item>> COIN_METALS = new HashMap<>();

  public static HashMap<String, Item> COINS = new HashMap<>();
  public static HashMap<String, Item> COINSTACKS = new HashMap<>();

  public static void init () {
    CreateDecoMod.LOGGER.info("Registering items for " + CreateDecoMod.NAME);

    METAL_TYPES.put("Andesite", (str) -> AllItems.ANDESITE_ALLOY.get());
    METAL_TYPES.put("Zinc", (str) -> AllItems.ZINC_INGOT.get());
    METAL_TYPES.put("Copper", (str) -> Items.COPPER_INGOT);
    METAL_TYPES.put("Brass", (str) -> AllItems.BRASS_INGOT.get());
    METAL_TYPES.put("Iron", (str) -> Items.IRON_INGOT);
    METAL_TYPES.put("Industrial Iron", (str) -> INDUSTRIAL_IRON_INGOT);

    COIN_METALS.put("Zinc", (str) -> AllItems.ZINC_INGOT.get());
    COIN_METALS.put("Copper", (str) -> Items.COPPER_INGOT);
    COIN_METALS.put("Brass", (str) -> AllItems.BRASS_INGOT.get());
    COIN_METALS.put("Iron", (str) -> Items.IRON_INGOT);
    COIN_METALS.put("Industrial Iron", (str) -> INDUSTRIAL_IRON_INGOT);
    COIN_METALS.put("Gold", (str) -> Items.GOLD_INGOT);
    COIN_METALS.put("Netherite", (str) -> Items.NETHERITE_INGOT);

    CreateDecoTags.init();
    registerSheets();
    registerNuggets();
    registerIngots();

    COIN_METALS.forEach(ItemRegistry::registerCoins);
  }

  private static void registerSheets () {
    ANDESITE_SHEET = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", "andesite_sheet"),
      new Item(new Item.Settings())
    );

    ZINC_SHEET = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", "zinc_sheet"),
      new Item(new Item.Settings())
    );

    INDUSTRIAL_IRON_SHEET = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", "industrial_iron_sheet"),
      new Item(new Item.Settings())
    );

    // TODO: Add to plate tags in CreateDecoTags
    // TODO: Add lang entries to en_us.json
  }

  private static void registerNuggets () {
    NETHERITE_NUGGET = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", "netherite_nugget"),
      new Item(new Item.Settings().fireproof())
    );

    INDUSTRIAL_IRON_NUGGET = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", "industrial_iron_nugget"),
      new Item(new Item.Settings())
    );

    // TODO: Create recipe JSON files
    // Storage: 9 nuggets ↔ 1 ingot for both metals
    // TODO: Add to nugget tags
    // TODO: Add lang entries
  }

  private static void registerIngots () {
    INDUSTRIAL_IRON_INGOT = Registry.register(
      Registries.ITEM,
      Identifier.of("createdeco", "industrial_iron_ingot"),
      new Item(new Item.Settings())
    );

    // TODO: Create recipe JSON files
    // Storage: 9 nuggets → 1 ingot, 9 ingots → 1 block, 1 block → 9 ingots
    // Smelting/Blasting: crushed_raw_iron → industrial_iron_ingot
    // TODO: Add to ingot tags
    // TODO: Add lang entry
  }

  private static void registerCoins (String metal, Function<String, Item> getter) {
    if (metal.equals("Andesite")) return;

    // Register coin items and coinstack items using factory
    COINS.put(metal, Coins.registerCoinItem(metal));
    COINSTACKS.put(metal, Coins.registerCoinStackItem(metal));

    // TODO: Create recipe JSON files:
    // - 1 coinstack item → 4 coin items (shapeless)
    // - 4 coin items → 1 coinstack item (shapeless)
  }
}
