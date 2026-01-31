# AGENTS.md

This file provides guidance to agentic coding assistants working in this repository.

## Build Commands

| Command | Description |
|---------|-------------|
| `./gradlew build` | Build the mod JAR |
| `./gradlew runClient` | Run in development environment (client) |
| `./gradlew runServer` | Run dedicated server |
| `./gradlew runData` | Generate data (recipes, tags, etc.) |
| `./gradlew runGameTestServer` | Run all game tests |
| `./gradlew --refresh-dependencies` | Refresh Gradle dependencies |

### Running a Single Test

Use the Minecraft `/test` command in-game with the game test namespace:
```
/test examplemod:MyTestClass
```

Or run all tests from a specific namespace:
```
/test examplemod
```

The `runGameTestServer` command will run all registered gametests in the `examplemod` namespace and exit.

## Code Style Guidelines

### Imports
- Organize imports with blank lines between groups: (java.*, com.lowdragmc.*, net.minecraft.*, com.example.*)
- Use wildcard imports sparingly; prefer explicit imports
- Order: Standard library → Third-party (LDLib2) → Minecraft/NeoForge → Project imports

### Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line length**: No strict limit, but prefer readability
- **Braces**: K&R style - opening brace on same line
- **Blank lines**: One blank line between methods, two between class members where appropriate

### Naming Conventions
- **Classes**: PascalCase (e.g., `Tutorial1Screen`, `SimpleBlockEntity`)
- **Methods**: camelCase (e.g., `createModularUI`, `tick`)
- **Variables**: camelCase (e.g., `player`, `modularUI`, `counter`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_THIRST`, `MODID`)
- **Packages**: lowercase, dot-separated (e.g., `com.example.examplemod.gui.tutorial`)
- **Files**: PascalCase matching class name (e.g., `Tutorial1UIContainer.java`)

### Type System
- Target Java 21
- Use `@NotNull` and `@Nullable` annotations (org.jetbrains.annotations)
- Prefer `var` for local variables when type is obvious
- Use `DeferredRegister` for centralized registration of Blocks, Items, MenuTypes, etc.

### Error Handling
- Check for null player references before operations: `if (player == null) return InteractionResult.FAIL;`
- Use Minecraft's `InteractionResult` enum for item interaction results
- Log errors with `LOGGER.error()` and info with `LOGGER.info()`

### Comments
- Use Javadoc (`/** ... */`) for public methods and classes
- Prefer English comments for code documentation
- Mixed English/Chinese comments acceptable in educational context
- Keep comments concise and purpose-focused

### ModularUI Patterns (LDLib2)
- **Always prefer ModularUI over vanilla Minecraft UI**
- Create UI root: `var root = UI.create(this.pos, MCtextures.BACKGROUND, 176, 166);`
- Layout: Chain lambda recommended: `root.layout(layout -> layout.flexDirection(YogaFlexDirection.ROW).paddingAll(7));`
- Server sync: `ModularUI.of(root, player)` vs client-only: `ModularUI.of(root)`
- Screen: `{Feature}Screen.java`, Menu: `{Feature}Menu.java`, Component: `{Feature}UIContainer.java`

### Registration Pattern
- Centralize all registrations in `ExampleMod.java` using `DeferredRegister`
- Register to mod event bus in constructor: `BLOCKS.register(modEventBus);`
- Block entities: Use `ModularBlockEntityTypes` and implement `ISyncPersistRPCBlockEntity`

### Player Data (Attachments)
- Use NeoForge's `AttachmentType` system
- Wrap data classes in Attachment classes (e.g., `ThirstDataAttachment` wraps `ThirstData`)
- Register in mod constructor: `ThirstDataAttachment.ATTACHMENT_TYPES.register(modEventBus);`

### RPC Communication
- Use `@RPCPacket` annotation for client-server communication
- Methods called on receiving side (server or client)
- Use `RPCSender` parameter to access context

## Project Structure
- `gui/tutorial/` - 9 GUI tutorials demonstrating LDLib2 features
- `thirst/` - Player data example with Attachments + RPC
- `block/` - Block entities with `ISyncPersistRPCBlockEntity`
- `item/tutorial/` - Tutorial items that trigger UIs
- `doc/LDlib2/` - Extensive LDLib2 documentation

## Key Dependencies
- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.217
- **LDLib2**: 2.1.5.a (from `https://maven.firstdark.dev/snapshots`)
- **Yoga Layout**: 1.0.0 (FlexBox UI layout engine)
