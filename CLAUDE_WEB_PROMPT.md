# CreateDeco Fabric Port - Complete Rewrite Task

## Mission
Port CreateDeco from Registrate-based registration (NeoForge) to Vanilla Minecraft Registry API (Fabric 1.21.10). This is a complete architectural rewrite of the registration system.

## Critical Context

**Repository:** https://github.com/Svenja-dev/CreateDeco-Fabric
**Branch:** main
**Status:** Currently BLOCKED - won't compile due to missing Registrate

**The Problem:**
- CreateDeco (NeoForge) uses `CreateRegistrate` for 100% of block/item registration
- Create-Fly (Fabric) has intentionally removed Registrate
- ~400 blocks need manual rewrite from Builder pattern to Vanilla Registry API

**What You Need to Do:**
Rewrite all registration code from Registrate Builder pattern to Vanilla Minecraft Registry API, following Create-Fly's patterns.

---

## Step 1: Understand the Architecture

### Current (Broken) Pattern - Registrate

```java
// CreateDecoMod.java
public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

// BlockRegistry.java
private static void registerBars(String metal, Function<String, Item> getter) {
    BAR_PANELS.put(metal, Bars.build(CreateDecoMod.REGISTRATE, metal, "overlay", false)
        .recipe((ctx, prov) -> {
            Bars.recipeStonecutting(() -> getter.apply("ingot"), ctx, prov);
        })
        .register());
}

// api/Bars.java
public static BlockBuilder<IronBarsBlock, ?> build(
    CreateRegistrate reg,
    String metal,
    String suffix,
    boolean doPost
) {
    return reg.block(regName + suffix + "_bars", IronBarsBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p.strength(5f, 6f))
        .transform(pickaxeOnly())
        .item()
        .build();
}
```

### Target Pattern - Vanilla Registry (from Create-Fly)

Study Create-Fly source code for examples. Typical pattern:

```java
// In a Registry class
public static final Block EXAMPLE_BLOCK = Registry.register(
    Registries.BLOCK,
    Identifier.of("createdeco", "example_block"),
    new IronBarsBlock(
        AbstractBlock.Settings.create()
            .strength(5.0f, 6.0f)
            .sounds(BlockSoundGroup.METAL)
            .requiresTool()
    )
);

public static final Item EXAMPLE_BLOCK_ITEM = Registry.register(
    Registries.ITEM,
    Identifier.of("createdeco", "example_block"),
    new BlockItem(EXAMPLE_BLOCK, new Item.Settings())
);

public static void register() {
    // Called from mod initializer to trigger static initialization
}
```

---

## Step 2: Files to Modify

You must modify these 20 files in `src/main/java/com/github/talrey/createdeco/`:

### Core Files (3)
1. **CreateDecoMod.java** - Remove REGISTRATE, change init() method
2. **BlockRegistry.java** - 18,005 lines! Complete rewrite needed
3. **ItemRegistry.java** - Complete rewrite needed

### API Builder Files (18) - Convert to Static Factories
All files in `api/` directory:
- Bars.java
- Bricks.java
- CageLamps.java
- Catwalks.java
- Coins.java
- CreateDecoTags.java
- Decals.java
- Doors.java
- Facades.java
- Hulls.java
- Ladders.java
- MeshFences.java
- Placards.java
- SheetMetal.java
- ShippingContainers.java
- Supports.java
- Wedges.java
- Windows.java

---

## Step 3: Reference Implementation - Study Create-Fly

Before starting, examine Create-Fly source code (available in libs/create-fly-1.21.10-6.0.8-3-sources.jar):

**Key files to study:**
- `com/zurrtum/create/content/kinetics/BlocksKinetics.java` - Block registration examples
- `com/zurrtum/create/content/decoration/BlocksDecoration.java` - Decoration blocks (most relevant!)
- Any file with `Registry.register()` calls

**Extract and read these files:**
```bash
cd libs
unzip -j create-fly-1.21.10-6.0.8-3-sources.jar \
  'com/zurrtum/create/content/decoration/*.java' \
  -d ../reference/
```

---

## Step 4: Conversion Strategy

### For Each Block Type

**Before (Registrate):**
```java
YELLOW_CAGE_LAMPS.put(metal, CageLamps.build(
    CreateDecoMod.REGISTRATE,
    metal,
    DyeColor.YELLOW,
    cage,
    YELLOW_ON,
    YELLOW_OFF
).recipe(CageLamps.recipe(metal, () -> Items.TORCH, material))
  .register());
```

**After (Vanilla Registry):**
```java
// 1. Register the block
String blockId = metal.toLowerCase().replace(" ", "_") + "_yellow_cage_lamp";
Block block = Registry.register(
    Registries.BLOCK,
    Identifier.of("createdeco", blockId),
    new CageLampBlock(
        AbstractBlock.Settings.create()
            .strength(5.0f, 6.0f)
            .sounds(BlockSoundGroup.METAL)
            .requiresTool()
            .luminance(state -> state.get(CageLampBlock.LIT) ? 15 : 0),
        DyeColor.YELLOW
    )
);

// 2. Register the item
Item item = Registry.register(
    Registries.ITEM,
    Identifier.of("createdeco", blockId),
    new BlockItem(block, new Item.Settings())
);

// 3. Store in map
YELLOW_CAGE_LAMPS.put(metal, block);

// 4. Recipes now need JSON files (see Step 5)
```

### Import Changes Needed

Remove:
```java
import com.zurrtum.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.*;
import com.tterrag.registrate.util.*;
```

Add:
```java
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
```

---

## Step 5: Recipe System Changes

**Old System:** Recipes defined in Java code via Registrate datagen

**New System:** Recipes as JSON files in `src/main/resources/data/createdeco/recipes/`

**For each recipe, create JSON file:**

Example: `data/createdeco/recipes/brass_yellow_cage_lamp.json`
```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "I I",
    "ITI",
    "I I"
  ],
  "key": {
    "I": {
      "item": "create:brass_ingot"
    },
    "T": {
      "item": "minecraft:torch"
    }
  },
  "result": {
    "item": "createdeco:brass_yellow_cage_lamp"
  }
}
```

**Strategy for recipes:**
1. Analyze existing recipe code in `api/*.java` files
2. Extract recipe patterns
3. Generate corresponding JSON files
4. Delete recipe() calls from registration code

---

## Step 6: Block-by-Block Conversion Plan

### Priority Order (Do in this sequence):

#### 1. Cage Lamps (56 variants) - HIGHEST PRIORITY
- 4 colors (YELLOW, GREEN, REDSTONE, SOUL) × 14 metals
- File: `api/CageLamps.java`
- Custom block class: `blocks/CageLampBlock.java` (already exists)
- Properties: luminance, redstone powered

#### 2. Bars & Panels (87 variants)
- File: `api/Bars.java`
- Block class: `IronBarsBlock` (Vanilla)
- Properties: connection behavior

#### 3. Catwalks (42 variants)
- File: `api/Catwalks.java`
- Custom classes: `CatwalkBlock.java`, `CatwalkRailingBlock.java`, `CatwalkStairBlock.java`
- Properties: Connected textures (keep CT system intact)

#### 4. Bricks (75 variants)
- File: `api/Bricks.java`
- 7 colors × various types (normal, cracked, mossy, etc.)

#### 5. Shipping Containers (16 variants)
- File: `api/ShippingContainers.java`
- Custom class: `ShippingContainerBlock.java`
- Properties: Connected textures, all 16 DyeColors

#### 6. Remaining Block Types
- Decals, Doors, Facades, Hulls, Ladders, Mesh Fences, Placards, Sheet Metal, Supports, Wedges, Windows

---

## Step 7: Testing Strategy

After each major block type conversion:

1. **Compile check:**
```bash
./gradlew compileJava
```

2. **If compilation succeeds, try build:**
```bash
./gradlew build
```

3. **Check for errors:**
- Missing imports
- Incorrect property syntax
- Identifier issues

---

## Step 8: Detailed Example - Complete Cage Lamp Conversion

**Study this complete example before starting:**

### Before: `api/CageLamps.java`
```java
public static BlockBuilder<CageLampBlock, CreateRegistrate> build(
    CreateRegistrate reg,
    String metal,
    DyeColor color,
    String cage,
    String on,
    String off
) {
    String regName = metal.toLowerCase().replaceAll(" ", "_");
    String colorName = color.getName();

    return reg.block(regName + "_" + colorName + "_cage_lamp",
        p -> new CageLampBlock(p, color))
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .strength(5f, 6f)
            .lightLevel(state -> state.getValue(CageLampBlock.LIT) ? 15 : 0))
        .blockstate((ctx, prov) -> customState(ctx, prov, metal, cage, on, off))
        .item()
        .build();
}
```

### After: Modified approach

**Option A: Direct Registration in BlockRegistry**
```java
// In BlockRegistry.java
public static final Map<String, Block> YELLOW_CAGE_LAMPS = new HashMap<>();
public static final Map<String, Block> GREEN_CAGE_LAMPS = new HashMap<>();
public static final Map<String, Block> REDSTONE_CAGE_LAMPS = new HashMap<>();
public static final Map<String, Block> SOUL_CAGE_LAMPS = new HashMap<>();

private static void registerCageLamps() {
    for (String metal : ItemRegistry.METAL_TYPES) {
        String cleanMetal = metal.toLowerCase().replace(" ", "_");

        // Yellow Cage Lamp
        Block yellowLamp = Registry.register(
            Registries.BLOCK,
            Identifier.of("createdeco", cleanMetal + "_yellow_cage_lamp"),
            new CageLampBlock(
                AbstractBlock.Settings.create()
                    .strength(5.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()
                    .luminance(state -> state.get(CageLampBlock.LIT) ? 15 : 0),
                DyeColor.YELLOW
            )
        );
        YELLOW_CAGE_LAMPS.put(metal, yellowLamp);

        Item yellowLampItem = Registry.register(
            Registries.ITEM,
            Identifier.of("createdeco", cleanMetal + "_yellow_cage_lamp"),
            new BlockItem(yellowLamp, new Item.Settings())
        );

        // Repeat for GREEN, REDSTONE, SOUL...
    }
}
```

**Option B: Keep api/CageLamps.java as factory (cleaner)**
```java
// api/CageLamps.java - Convert from Builder to Factory
public class CageLamps {

    public static Block createAndRegister(
        String metal,
        DyeColor color,
        String cage,
        String on,
        String off
    ) {
        String regName = metal.toLowerCase().replaceAll(" ", "_");
        String colorName = color.getName();
        String blockId = regName + "_" + colorName + "_cage_lamp";

        // Register block
        Block block = Registry.register(
            Registries.BLOCK,
            Identifier.of("createdeco", blockId),
            new CageLampBlock(
                AbstractBlock.Settings.create()
                    .strength(5.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()
                    .luminance(state -> state.get(CageLampBlock.LIT) ? 15 : 0),
                color
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

    // Remove all Registrate/Builder references
    // Keep recipe helper methods if useful
}
```

---

## Step 9: CreativeTabs Conversion

**Before:**
```java
public static void register() {
    CreateDecoMod.REGISTRATE.defaultCreativeTab(PROPS_KEY, METAL_KEY);
}
```

**After:**
```java
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public static final RegistryKey<ItemGroup> PROPS_KEY = RegistryKey.of(
    RegistryKeys.ITEM_GROUP,
    Identifier.of("createdeco", "props")
);

public static final ItemGroup PROPS_GROUP = FabricItemGroup.builder()
    .icon(() -> new ItemStack(BlockRegistry.BRASS_YELLOW_CAGE_LAMP))
    .displayName(Text.translatable("itemGroup.createdeco.props"))
    .entries((context, entries) -> {
        // Add all blocks manually
        entries.add(BlockRegistry.BRASS_YELLOW_CAGE_LAMP);
        entries.add(BlockRegistry.BRASS_GREEN_CAGE_LAMP);
        // ... etc
    })
    .build();

public static void register() {
    Registry.register(Registries.ITEM_GROUP, PROPS_KEY, PROPS_GROUP);
}
```

---

## Step 10: Build System Check

After completing conversion, verify `build.gradle` dependencies:

```gradle
dependencies {
    minecraft "com.mojang:minecraft:1.21.10"
    mappings "net.fabricmc:yarn:1.21.10+build.2:v2"
    modImplementation "net.fabricmc:fabric-loader:0.17.3"
    modImplementation "net.fabricmc.fabric-api:fabric-api:0.138.0+1.21.10"

    // Create-Fly dependency
    implementation files("libs/create-fly-1.21.10-6.0.8-3.jar")
}
```

**No Registrate dependency should remain!**

---

## Step 11: Known Challenges & Solutions

### Challenge 1: Connected Textures
**Solution:** Keep existing `connected/` classes intact - they should work with Vanilla blocks

### Challenge 2: Blockstate/Model Generation
**Old:** Registrate auto-generated via `.blockstate()` calls
**New:** Use existing JSON files in `assets/createdeco/blockstates/` and `models/`
**Action:** Verify JSON files match new block IDs

### Challenge 3: Recipes
**Old:** Defined in Java via `.recipe()` calls
**New:** JSON files in `data/createdeco/recipes/`
**Action:** Create JSON files for all recipes (can be automated with script)

### Challenge 4: Tags
**File:** `api/CreateDecoTags.java`
**Action:** Create JSON tag files in `data/createdeco/tags/blocks/` and `data/createdeco/tags/items/`

---

## Step 12: Execution Checklist

Work through this checklist systematically:

- [ ] Extract Create-Fly source examples to `reference/` folder
- [ ] Study Create-Fly registration patterns (30 min minimum)
- [ ] Modify `CreateDecoMod.java` - remove REGISTRATE
- [ ] Convert `api/CageLamps.java` to factory pattern
- [ ] Rewrite cage lamp registration in `BlockRegistry.java`
- [ ] Test compile - fix any errors
- [ ] Repeat for remaining 17 API files
- [ ] Convert `CreativeTabs.java` to Fabric API
- [ ] Generate recipe JSON files (or write script)
- [ ] Generate tag JSON files
- [ ] Final compile check
- [ ] Full build attempt
- [ ] Document any remaining issues in GitHub issue

---

## Step 13: Success Criteria

**Minimum Viable Success:**
1. ✅ Code compiles without errors (`./gradlew compileJava`)
2. ✅ Build succeeds (`./gradlew build`)
3. ✅ No Registrate imports remain
4. ✅ All blocks registered via Vanilla Registry API
5. ✅ At least cage lamps fully functional

**Full Success:**
1. All 400+ blocks converted
2. All items registered
3. All recipes created as JSON
4. Creative tabs working
5. Tags functional
6. Mod loads in Minecraft without crashes

---

## Step 14: Additional Resources

**Documentation:**
- Fabric Wiki: https://fabricmc.net/wiki/
- Minecraft Registry: https://minecraft.wiki/w/Java_Edition_data_values
- Create-Fly GitHub: https://github.com/ZurrTum/Create-Fly

**Key Yarn Mappings (1.21.10):**
- `Registry.register()` - Main registration method
- `Registries.BLOCK`, `Registries.ITEM` - Registry instances
- `Identifier.of()` - Create namespaced IDs
- `AbstractBlock.Settings` - Block properties builder

---

## Step 15: Prompt for Claude Code Web

**Copy this entire document and add:**

"Using the repository at https://github.com/Svenja-dev/CreateDeco-Fabric, perform the complete Registrate-to-Vanilla Registry conversion as described above. Work systematically through each step, starting with cage lamps. Show me your progress after each major section. If you encounter ambiguities, study the Create-Fly source JAR for reference implementations before making decisions."

---

## Statistics

**Scope of work:**
- 20 Java files to modify
- ~400 blocks to register
- ~200 items to register
- ~600 recipes to create as JSON
- 18,005 lines in BlockRegistry.java alone
- Estimated time for human: 3-5 weeks
- Estimated time for Claude Code Web: ???

**Current status:**
- ✅ Build system configured
- ✅ Dependencies resolved
- ✅ Project structure correct
- ❌ Code won't compile (missing Registrate)
- Target: Make it compile and build successfully

---

## Final Notes

**This is a large task but entirely mechanical.** The pattern is repetitive:
1. Remove Registrate builder pattern
2. Replace with Registry.register() calls
3. Move recipes to JSON
4. Repeat 400+ times

**Claude Code Web advantages:**
- Can process large files (18k lines)
- Pattern recognition for repetitive tasks
- Can reference Create-Fly source for examples
- Consistent formatting across all conversions

**Success depends on:**
- Following Create-Fly patterns exactly
- Understanding Yarn mappings
- Proper Identifier syntax
- Correct block property mapping

Good luck!
