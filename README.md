# Fakepixel Utilities Mod (Reconstructed)

A Quality of Life (QoL) Mod for Fakepixel Skyblock, reconstructed from the original 1.0.4 JAR release.

## 📝 About This Project
This repository contains a reconstructed source code version of the **Fakepixel Utilities** mod for Minecraft 1.8.9. 

**Disclaimer:** 
- The source code in this repository was obtained via decompilation of the original mod JAR.
- I do **not** own this source code. All credit goes to the original authors (CherryTree Team).
- This project was setup with the help of AI to restore logical accuracy, fix decompilation errors, and establish a modern build environment.

## ✨ Features
- **Room Detection:** Automatic detection of Dungeon rooms using block hashing.
- **Secret Waypoints:** 3D ESP-style markers for chests and wither essences.
- **Safe Mode:** Prevents accidental dropping or trading of important items using reflection-based slot locking.
- **Inventory Scraping:** Automated batching of item data for FPU services.
- **Custom Icons:** DRM/Identity system rendering icons above verified FPU users.

## 🛠️ Build Instructions
This project uses **Architectury Loom** to build a legacy 1.8.9 Forge mod using modern Gradle (8.x).

1. Clone the repository.
2. Run the build command:
   ```bash
   ./gradlew build
   ```
3. The production JAR (remapped to SRG) will be located in `build/libs/` after the remapping step.

## ⚖️ License
See the `LICENSE.txt` file for information regarding the use of this software, as provided in the original mod package.
