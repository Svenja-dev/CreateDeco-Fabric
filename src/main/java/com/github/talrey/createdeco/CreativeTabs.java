package com.github.talrey.createdeco;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CreativeTabs {
  public static final String MAIN_KEY = "main";
  public static final String PALETTES_KEY = "palettes";

  public static ItemGroup MAIN_TAB;
  public static ItemGroup PALETTES_TAB;

  public static void register() {
    CreateDecoMod.LOGGER.info("Registering Creative Tabs for " + CreateDecoMod.NAME);

    // Main tab with props and decorations
    MAIN_TAB = Registry.register(
      Registries.ITEM_GROUP,
      Identifier.of(CreateDecoMod.MOD_ID, MAIN_KEY),
      FabricItemGroup.builder()
        .icon(() -> new ItemStack(BlockRegistry.BRASS_CATWALK))
        .displayName(Text.translatable("itemGroup." + CreateDecoMod.MOD_ID + "." + MAIN_KEY))
        .entries((context, entries) -> {
          // Items
          entries.add(ItemRegistry.ANDESITE_SHEET);
          entries.add(ItemRegistry.ZINC_SHEET);
          entries.add(ItemRegistry.INDUSTRIAL_IRON_SHEET);
          entries.add(ItemRegistry.NETHERITE_NUGGET);
          entries.add(ItemRegistry.INDUSTRIAL_IRON_NUGGET);
          entries.add(ItemRegistry.INDUSTRIAL_IRON_INGOT);

          // Coins
          ItemRegistry.COIN_METALS.forEach((metal, getter) -> {
            if (!metal.equals("Andesite")) {
              entries.add(getter.apply("coin"));
              entries.add(getter.apply("coinstack"));
            }
          });

          // Cage Lamps (group by color)
          for (String color : new String[]{"yellow", "red", "green", "blue"}) {
            ItemRegistry.METAL_TYPES.forEach((metal, getter) -> {
              entries.add(BlockRegistry.getCageLamp(metal, color));
            });
          }

          // Windows & Panes
          entries.add(BlockRegistry.ANDESITE_WINDOW);
          entries.add(BlockRegistry.ANDESITE_WINDOW_PANE);
          entries.add(BlockRegistry.COPPER_WINDOW);
          entries.add(BlockRegistry.COPPER_WINDOW_PANE);
          entries.add(BlockRegistry.IRON_WINDOW);
          entries.add(BlockRegistry.IRON_WINDOW_PANE);
          entries.add(BlockRegistry.INDUSTRIAL_IRON_WINDOW);
          entries.add(BlockRegistry.INDUSTRIAL_IRON_WINDOW_PANE);
          entries.add(BlockRegistry.BRASS_WINDOW);
          entries.add(BlockRegistry.BRASS_WINDOW_PANE);
          entries.add(BlockRegistry.ZINC_WINDOW);
          entries.add(BlockRegistry.ZINC_WINDOW_PANE);

          // Doors & Trapdoors
          BlockRegistry.DOORS.values().forEach(entries::add);
          BlockRegistry.LOCK_DOORS.values().forEach(entries::add);
          BlockRegistry.TRAPDOORS.values().forEach(entries::add);

          // Catwalks
          BlockRegistry.CATWALKS.values().forEach(entries::add);
          BlockRegistry.CATWALK_STAIRS.values().forEach(entries::add);
          BlockRegistry.CATWALK_RAILINGS.values().forEach(entries::add);

          // Bars & Fences
          BlockRegistry.BARS.values().forEach(entries::add);
          BlockRegistry.BAR_PANELS.values().forEach(entries::add);
          BlockRegistry.MESH_FENCES.values().forEach(entries::add);

          // Structural
          BlockRegistry.SHEET_METAL_PILLARS.values().forEach(entries::add);
          BlockRegistry.LADDERS.values().forEach(entries::add);
          BlockRegistry.HULLS.values().forEach(entries::add);
          BlockRegistry.SUPPORTS.values().forEach(entries::add);
          BlockRegistry.WEDGES.values().forEach(entries::add);
          BlockRegistry.FACADES.values().forEach(entries::add);

          // Decals
          BlockRegistry.DECALS.values().forEach(entries::add);
        })
        .build()
    );

    // Palettes tab (future use for bricks, etc.)
    PALETTES_TAB = Registry.register(
      Registries.ITEM_GROUP,
      Identifier.of(CreateDecoMod.MOD_ID, PALETTES_KEY),
      FabricItemGroup.builder()
        .icon(() -> new ItemStack(BlockRegistry.ANDESITE_WINDOW))
        .displayName(Text.translatable("itemGroup." + CreateDecoMod.MOD_ID + "." + PALETTES_KEY))
        .entries((context, entries) -> {
          // Reserved for future brick blocks
          // Currently empty
        })
        .build()
    );
  }
}
