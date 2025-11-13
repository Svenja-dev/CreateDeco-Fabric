package com.github.talrey.createdeco;

import com.github.talrey.createdeco.api.*;
import com.github.talrey.createdeco.blocks.*;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.decoration.MetalLadderBlock;
import com.zurrtum.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.zurrtum.create.content.decoration.palettes.ConnectedPillarBlock;
import com.zurrtum.create.content.decoration.palettes.WindowBlock;
import com.zurrtum.create.content.decoration.placard.PlacardBlock;
import com.zurrtum.create.content.decoration.placard.PlacardRenderer;
import com.zurrtum.create.foundation.data.SharedProperties;
import com.zurrtum.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.talrey.createdeco.api.CageLamps.*;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class BlockRegistry {

	public static HashMap<DyeColor, String> BRICK_COLORS = new HashMap<>() {{
		put(DyeColor.LIGHT_BLUE, "blue");
		put(DyeColor.YELLOW, "dean");
		put(DyeColor.BLACK, "dusk");
		put(DyeColor.WHITE, "pearl");
		put(DyeColor.RED, "scarlet");
		put(DyeColor.GREEN, "verdant");
		put(DyeColor.BROWN, "umber");
		put(null, "red");
	}};

	// TODO: Convert Bricks to Factory pattern (196 blocks total)
	// public static HashMap<DyeColor, HashMap<String, Block>> BRICKS = new HashMap<>();
	// public static HashMap<DyeColor, HashMap<String, Block>> STAIRS = new HashMap<>();
	// public static HashMap<DyeColor, HashMap<String, Block>> SLABS = new HashMap<>();
	// public static HashMap<DyeColor, HashMap<String, Block>> WALLS = new HashMap<>();

	public static HashMap<String, net.minecraft.block.Block> DECALS = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> YELLOW_CAGE_LAMPS = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block>    RED_CAGE_LAMPS = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block>  GREEN_CAGE_LAMPS = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block>   BLUE_CAGE_LAMPS = new HashMap<>();

	public static HashMap<String, Block> WINDOWS      = new HashMap<>();
	public static HashMap<String, Block> WINDOW_PANES = new HashMap<>();
	public static HashMap<String, Block> DOORS          = new HashMap<>();
	public static HashMap<String, Block> LOCK_DOORS     = new HashMap<>();
	public static HashMap<String, Block> TRAPDOORS  = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> BARS       = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> BAR_PANELS = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> MESH_FENCES   = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> SHEET_METAL_PILLARS = new HashMap<>();

	public static HashMap<String, Block> CATWALKS                = new HashMap<>();
	public static HashMap<String, Block> CATWALK_STAIRS     = new HashMap<>();
	public static HashMap<String, Block> CATWALK_RAILINGS = new HashMap<>();

	public static HashMap<String, net.minecraft.block.Block> LADDERS = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> HULLS          = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> SUPPORTS    = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> WEDGES = new HashMap<>();
	public static HashMap<String, net.minecraft.block.Block> FACADES      = new HashMap<>();

	// TODO: Convert Placards to Factory pattern (requires BlockEntity support)
	// public static HashMap<DyeColor, Block> PLACARDS = new HashMap<>();
	// public static BlockEntityType<DyedPlacardBlock.Entity> PLACARD_ENTITIES;

	public static HashMap<String, Block> COIN_BLOCKS  = new HashMap<>();

	// TODO: Convert Shipping Containers to Factory pattern (requires BlockEntity support)
	// public static HashMap<DyeColor, Block> SHIPPING_CONTAINERS = new HashMap<>();
	// public static HashMap<DyeColor, BlockEntityType<ShippingContainerBlock.Entity>> CONTAINER_ENTITIES = new HashMap<>();

	public static DyeColor fromName (String color) {
		for (DyeColor dye : BRICK_COLORS.keySet()) {
			if (BRICK_COLORS.get(dye).equals(color)) return dye;
		}
		return null;
	}

	public static void init() {
		// load the class and register everything
		CreateDecoMod.LOGGER.info("Registering blocks for " + CreateDecoMod.NAME);

		// Register all converted block types
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerBars);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerFences);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerCatwalks);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerLadders);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerHulls);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerSupports);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerCageLamps);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerSheetMetal);
		ItemRegistry.METAL_TYPES.forEach(BlockRegistry::registerDoors);
		registerDecals();
		ItemRegistry.COIN_METALS.forEach(BlockRegistry::registerCoins);

		// TODO: Convert and enable these registrations
		// registerShippingContainers(); // Requires BlockEntity conversion
		// registerPlacards(); // Requires BlockEntity conversion
		// registerBricks(); // Requires conversion of 196 blocks
	}

	private static void registerBars (String metal, Function<String, Item> getter) {
		// Register bar panels (overlay variant) for all metals
		BAR_PANELS.put(metal, Bars.createAndRegisterPanel(metal));

		// Skip regular bars for Iron (vanilla already has iron_bars)
		if (metal.equals("Iron")) return;

		// Register regular bars for non-Iron metals
		BARS.put(metal, Bars.createAndRegisterBar(metal));

		// TODO: Create recipe JSON files for bars
		// - Stonecutting: ingot -> 4 bars
		// - Crafting: 6 ingots (2x3) -> 16 bars
		// - Panels: 6 plates (2x3) -> 16 bar panels
	}

	private static void registerFences (String metal, Function<String, Item> getter) {
		// Register mesh fence using vanilla registry API
		MESH_FENCES.put(metal, MeshFences.createAndRegister(metal));

		// TODO: Create recipe JSON files for mesh fences
		// Pattern: plate + string + plate (2 rows) → 16 fences
		// Also stonecutting: ingot → 4 fences
	}

	private static void registerDecals () {
		// Register all decal blocks using vanilla registry API
		var decals = Decals.createAndRegisterAll();
		for (int i = 0; i < decals.size(); i++) {
			DECALS.put(Decals.TYPES.get(i), decals.get(i));
		}
		// TODO: Create stonecutting recipe JSON files for decals (1 iron sheet → 1 decal)
	}


	private static void registerCageLamps (String metal, Function<String, Item> getter) {
		// Register all 4 cage lamp colors using vanilla registry API
		YELLOW_CAGE_LAMPS.put(metal, CageLamps.createAndRegister(metal, net.minecraft.util.DyeColor.YELLOW));
		RED_CAGE_LAMPS.put(metal, CageLamps.createAndRegister(metal, net.minecraft.util.DyeColor.RED));
		GREEN_CAGE_LAMPS.put(metal, CageLamps.createAndRegister(metal, net.minecraft.util.DyeColor.GREEN));
		BLUE_CAGE_LAMPS.put(metal, CageLamps.createAndRegister(metal, net.minecraft.util.DyeColor.BLUE));

		// TODO: Create recipe JSON files for cage lamps
		// Recipes will be in data/createdeco/recipes/
		// Pattern: nugget + torch + plate for each variant
	}

	private static void registerCatwalks (String metal, Function<String, Item> getter) {
		// Register catwalks using factory pattern
		CATWALKS.put(metal, Catwalks.createAndRegisterCatwalk(metal));
		CATWALK_STAIRS.put(metal, Catwalks.createAndRegisterCatwalkStair(metal));
		CATWALK_RAILINGS.put(metal, Catwalks.createAndRegisterCatwalkRailing(metal));

		// TODO: Create recipe JSON files:
		// - Catwalk crafting: 4 plates + 1 bar in cross pattern → 4 catwalks
		// - Catwalk stonecutting: 1 ingot → 4 catwalks
		// - Stair crafting: 1 catwalk + 1 bar → 2 stairs
		// - Stair stonecutting: 1 ingot → 2 stairs
		// - Railing crafting: 3 plates + 4 bars in fence pattern → 8 railings
		// - Railing stonecutting: 1 ingot → 8 railings
		// Register support wedge using vanilla registry API
		WEDGES.put(metal, Wedges.createAndRegister(metal));

		// TODO: Create recipe JSON files for wedges
		// Crafting: 3 plates in triangle pattern → 3 wedges
		// Stonecutting: 1 ingot → 4 bars
		// Register facade using vanilla registry API
		FACADES.put(metal, Facades.createAndRegister(metal));

		// TODO: Create recipe JSON file for facades
		// Stonecutting: 1 ingot → 4 facades
	}

	private static void registerSheetMetal (String metal, Function<String, Item> getter) {
		// Register sheet metal using vanilla registry API
		SHEET_METAL_PILLARS.put(metal, SheetMetal.createAndRegister(metal));

		// TODO: Create recipe JSON file for sheet metal
		// Crafting: 4 plates in 2x2 pattern → 4 sheet metal blocks
	}

	public static final Block
			ANDESITE_WINDOW = Windows.createAndRegisterWindow("Andesite"),
			COPPER_WINDOW = Windows.createAndRegisterWindow("Copper"),
			IRON_WINDOW = Windows.createAndRegisterWindow("Iron"),
			INDUSTRIAL_IRON_WINDOW = Windows.createAndRegisterWindow("Industrial Iron"),
			BRASS_WINDOW = Windows.createAndRegisterWindow("Brass"),
			ZINC_WINDOW = Windows.createAndRegisterWindow("Zinc");

	public static final Block
			ANDESITE_WINDOW_PANE = Windows.createAndRegisterWindowPane("Andesite", ANDESITE_WINDOW),
			COPPER_WINDOW_PANE = Windows.createAndRegisterWindowPane("Copper", COPPER_WINDOW),
			IRON_WINDOW_PANE = Windows.createAndRegisterWindowPane("Iron", IRON_WINDOW),
			INDUSTRIAL_IRON_WINDOW_PANE = Windows.createAndRegisterWindowPane("Industrial Iron", INDUSTRIAL_IRON_WINDOW),
			BRASS_WINDOW_PANE = Windows.createAndRegisterWindowPane("Brass", BRASS_WINDOW),
			ZINC_WINDOW_PANE = Windows.createAndRegisterWindowPane("Zinc", ZINC_WINDOW);

	// TODO: Create recipe JSON files for windows and panes
	// - Window crafting: 2 ingots + 1 glass block → 2 windows
	// - Pane crafting: 3×2 pattern of windows → 16 panes

	private static void registerDoors (String metal, Function<String, Item> getter) {
		// Skip metals that have vanilla doors
		if (metal.equals("Iron") || metal.equals("Gold") || metal.equals("Netherite")) {
			return;
		}

		// Copper only has locked door (uses vanilla copper_door as base)
		if (metal.equals("Copper")) {
			LOCK_DOORS.put(metal, Doors.createAndRegisterDoor(metal, true));
			// TODO: Create recipe JSON - vanilla copper_door + redstone torch → locked door
			return;
		}

		// Other metals get full set: normal door, locked door, trapdoor
		DOORS.put(metal, Doors.createAndRegisterDoor(metal, false));
		LOCK_DOORS.put(metal, Doors.createAndRegisterDoor(metal, true));
		TRAPDOORS.put(metal, Doors.createAndRegisterTrapdoor(metal));

		// TODO: Create recipe JSON files:
		// - Door crafting: 6 ingots (2×3 pattern) → 3 doors
		// - Locked door crafting: 1 door + 1 redstone torch → 1 locked door (shapeless)
		// - Trapdoor crafting: 4 ingots (2×2 pattern) → 1 trapdoor
	}

	private static void registerHulls (String metal, Function<String, Item> getter) {
		// Register hull using vanilla registry API
		HULLS.put(metal, Hulls.createAndRegister(metal));

		// TODO: Create recipe JSON files for hulls
		// Crafting: 4 plates + 1 block → 2 hulls
		// Stonecutting: 1 block → 1 hull
	}

	private static void registerLadders (String metal, Function<String, Item> getter) {
		// Skip metals that don't have ladders (Copper, Andesite, Brass)
		if (metal.contains("opper") || metal.contains("ndesite") || metal.contains("rass")) return;

		// Register ladder using vanilla registry API
		LADDERS.put(metal, Ladders.createAndRegister(metal));

		// TODO: Create recipe JSON file for ladder
		// Stonecutting: 1 ingot → 2 ladders
	}

	private static void registerSupports (String metal, Function<String, Item> getter) {
		// Register support using vanilla registry API
		SUPPORTS.put(metal, Supports.createAndRegister(metal));

		// TODO: Create recipe JSON file for support
		// Crafting: 4 ingots in cross pattern → 4 supports
	}

	private static void registerShippingContainers () {
		for (DyeColor color : DyeColor.values()) {
			SHIPPING_CONTAINERS.put(color, ShippingContainers.build(CreateDecoMod.REGISTRATE, color)
					.recipe( (ctx, prov)-> {
						ShippingContainers.recipeCrafting(color, ctx, prov);
						ShippingContainers.recipeDyeing(color, ctx, prov);
					})
					.register()
			);
			CONTAINER_ENTITIES.put(color, CreateDecoMod.REGISTRATE.blockEntity(
							"shipping_container_" + color.getName().toLowerCase(Locale.ROOT).replaceAll(" ", "_"),
							ShippingContainerBlock.Entity::new)
					.validBlocks(SHIPPING_CONTAINERS.get(color))
					.register()
			);
		}
	}

	private static void registerPlacards () {
		for (DyeColor color : DyeColor.values()) {
			if (color == DyeColor.WHITE) { // Create's is the default
				continue;
			}
			String regName = color.name().toLowerCase(Locale.ROOT)
					.replaceAll(" ", "_") + "_placard";

			PLACARDS.put(color, CreateDecoMod.REGISTRATE.block(regName, DyedPlacardBlock::new)
					.initialProperties(SharedProperties::copperMetal)
					.transform(pickaxeOnly())
					.blockstate((ctx,prov)->BlockStateGenerator.placard(CreateDecoMod.REGISTRATE, color, ctx, prov))
					.simpleItem()
					.recipe( (ctx, prov)-> {
						Placards.recipeCrafting(color, ctx, prov);
						Placards.recipeDyeing(color, ctx, prov);
					})
					.onRegisterAfter(Registries.ITEM, placard -> {
						// none of this works. TODO ask about tooltips
						TooltipModifier original = TooltipModifier.REGISTRY.get(AllBlocks.PLACARD.asItem());
						if (original == null) {
							CreateDecoMod.LOGGER.info("placard tooltip was null"); // why is it null?
						} else if (original.equals(TooltipModifier.EMPTY)) {
							CreateDecoMod.LOGGER.info("placard tooltip was empty");
						}
						//TODO - this fully crashes on Create 6.0
//						TooltipModifier.REGISTRY.register(placard.asItem(),
//								TooltipModifier.REGISTRY.get(AllBlocks.PLACARD.asItem())
//						);
					})
					.register());
		}



		@SuppressWarnings("unchecked")
		BlockEntry<? extends PlacardBlock>[] validPlacards = new BlockEntry[PLACARDS.size()];
		int color = 0;
		for (BlockEntry<? extends PlacardBlock> block : PLACARDS.values()) {
			validPlacards[color] = block;
		}
		PLACARD_ENTITIES = CreateDecoMod.REGISTRATE.blockEntity("dyed_placard", DyedPlacardBlock.Entity::new)
				.renderer(()-> PlacardRenderer::new)
				.validBlocks(PLACARDS.values().toArray(validPlacards))
				.register();
	}

	private static void registerCoins (String metal, Function<String, Item> getter) {
		if (metal.equals("Andesite")) return;

		// Register coinstack block using factory
		COIN_BLOCKS.put(metal, Coins.registerCoinStackBlock(metal));

		// TODO: Create blockstate JSON files for coinstack blocks (8 layer variants)
		// TODO: Create loot table JSON with conditional drops (1-8 items based on LAYERS)
	}

	private static void registerBricks () {
		BRICK_COLORS.forEach( (color, name)-> {
			ArrayList<BlockBuilder<Block, ?>>     blocks;
			ArrayList<BlockBuilder<StairBlock,?>> stairs;
			ArrayList<BlockBuilder<SlabBlock,?>>  slabs;
			ArrayList<BlockBuilder<WallBlock,?>>  walls;

			blocks = Bricks.buildBlock(CreateDecoMod.REGISTRATE, name);
			blocks.forEach(bb -> {
				BRICKS.putIfAbsent(color, new HashMap<>());
				BRICKS.get(color).put(bb.getName(), bb.register());
			});
			stairs = Bricks.buildStair(CreateDecoMod.REGISTRATE, name);
			stairs.forEach(bb -> {
				STAIRS.putIfAbsent(color, new HashMap<>());
				STAIRS.get(color).put(bb.getName(), bb.register());
			});
			slabs = Bricks.buildSlab(CreateDecoMod.REGISTRATE, name);
			slabs.forEach(bb -> {
				SLABS.putIfAbsent(color, new HashMap<>());
				SLABS.get(color).put(bb.getName(), bb.register());
			});
			walls = Bricks.buildWall(CreateDecoMod.REGISTRATE, name);
			walls.forEach(bb -> {
				WALLS.putIfAbsent(color, new HashMap<>());
				WALLS.get(color).put(bb.getName(), bb.register());
			});
		});
	}

//	private static TagKey<Block> of (String namespace, String path) {
//		return BlockTags.create(
//			ResourceLocation.fromNamespaceAndPath(namespace, path)
//		);
//	}
//
//	private static TagKey<Block> common (String path) {
//		return of("c", path);
//	}
}
