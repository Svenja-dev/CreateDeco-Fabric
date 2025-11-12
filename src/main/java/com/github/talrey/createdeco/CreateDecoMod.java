package com.github.talrey.createdeco;

import com.zurrtum.create.foundation.data.CreateRegistrate;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDecoMod implements ModInitializer {
  public static final String MOD_ID = "createdeco";
  public static final String NAME = "Create Deco";
  public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

  public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing Create Deco (Fabric)");
    init();
  }

  public static void init() {
    // Register Creative Tabs
    CreativeTabs.register();

    // Register Items and Blocks
    ItemRegistry.init();
    BlockRegistry.init();

    // Finalize Registrate
    REGISTRATE.register();
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  }
}
