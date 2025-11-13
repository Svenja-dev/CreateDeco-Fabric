package com.github.talrey.createdeco;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDecoMod implements ModInitializer {
  public static final String MOD_ID = "createdeco";
  public static final String NAME = "Create Deco";
  public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

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

    LOGGER.info("Create Deco initialization complete!");
  }

  public static Identifier id(String path) {
    return Identifier.of(MOD_ID, path);
  }
}
