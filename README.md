# CreateDeco Fabric Port

A work-in-progress port of [CreateDeco](https://github.com/talrey/CreateDeco) from NeoForge 1.21.1 to Fabric 1.21.10.

## ⚠️ Current Status: BLOCKED

This project is currently **blocked** due to a critical architectural incompatibility:

- **CreateDeco** relies 100% on [Registrate](https://github.com/tterrag1098/Registrate) for block/item registration
- **Create-Fly** (the Fabric port of Create) has intentionally removed Registrate
- Direct porting is not possible without a complete rewrite (~400+ block registrations)

See [MIGRATION_REPORT.md](../MIGRATION_REPORT.md) for detailed analysis.

## Project Details

- **Target Minecraft Version:** 1.21.10
- **Fabric Loader:** 0.17.3
- **Fabric API:** 0.138.0+1.21.10
- **Create Dependency:** Create-Fly 6.0.8-3
- **Java Version:** 21

## What Works

- ✅ Build system configured (Fabric Loom 1.13)
- ✅ Dependencies resolved (Create-Fly JAR integrated)
- ✅ Project structure created
- ✅ Entry points adapted (ModInitializer, ClientModInitializer)
- ✅ Package names migrated (com.simibubi → com.zurrtum)

## What Doesn't Work

- ❌ **Registrate library missing** - CreateDeco uses Registrate for ALL registrations
- ❌ Compilation fails due to missing `CreateRegistrate` class
- ❌ ~400 blocks need manual rewrite to use Vanilla Registry API

## Possible Solutions

See detailed options in [MIGRATION_REPORT.md](../MIGRATION_REPORT.md):

1. **Complete rewrite** without Registrate (3-5 weeks effort)
2. **Port Registrate** to Create-Fly (2-4 weeks effort)
3. **Use older Create-Fabric** version 1.20.1 with Registrate
4. **Partial port** - Only popular blocks ("CreateDeco Lite")
5. **Community solution** - Request Registrate support from Create-Fly maintainer

## Building

```bash
./gradlew build
```

**Note:** Build will currently fail due to missing Registrate dependency.

## Credits

- **Original Mod:** [CreateDeco](https://github.com/talrey/CreateDeco) by Kayla, Talrey, Ordana, Cassian
- **Create (Fabric):** [Create-Fly](https://github.com/ZurrTum/Create-Fly) by ZurrTum
- **Port Attempt:** Svenja-dev

## License

All Rights Reserved (matching original CreateDeco license)
