package com.github.talrey.createdeco;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreateDecoModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CreateDecoMod.LOGGER.info("Initializing Create Deco Client (Fabric)");

        // Client-only registrations here
        // Examples:
        // - Block Entity Renderers
        // - Particle Factories
        // - Model Predicates
        // - Keybindings
    }
}
