![Land-Lord-4-1-2025](https://github.com/user-attachments/assets/945b46bf-c7c7-4672-9328-e68cc8d5a7db)

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
![GitHub issues](https://img.shields.io/github/issues/s5y-ux/LandLord)
![GitHub stars](https://img.shields.io/github/stars/s5y-ux/LandLord?style=social)


## Overview

[Watch this video](https://www.youtube.com/watch?v=videoID)
**LandLord** is a Bukkit plugin that introduces a property management system where players can spawn "renters" (villagers) into designated properties within the game. Renters can be evicted using a special item and must be tied to a property. Players can also use a special "Renter Remover Wand" to remove renters from their properties.

## Features

- **Renter Spawn Egg**: Spawn renters (villagers) in a designated property using an egg item.
- **Property Range Check**: Renters can only be spawned within the bounds of a specified property.
- **Renter Remover Wand**: Special item used to evict renters by right-clicking on them.
- **Anti-Damage**: Renters are immune to damage to keep them safe from players.
- **Event Handling**: Handles spawning and despawning of renters.

## Installation

1. Download the latest release from the [Releases](https://github.com/yourusername/LandLord/releases) page.
2. Place the `.jar` file into the `plugins` folder of your Minecraft server.
3. Restart the server to load the plugin.

## Commands

- `/renterremoverwand`: Gives the player a special "Renter Remover Wand" to evict renters.
  
## How to Use

### Spawning Renters
1. Get the **Renter Spawn Egg** from your inventory by using the command or plugin features.
2. Throw the egg at the ground within a property to spawn a renter (villager).
3. Renters will be automatically tied to the property, and you will be notified upon successful placement.

### Removing Renters
1. Right-click a renter using the **Renter Remover Wand** to evict them.
2. The renter will be removed, and you will be notified that the renter has been evicted.

### Renter Protection
- Renters are immune to damage by default, ensuring they won't be harmed by other players.

## Configuration

This plugin stores property data in JSON format. Properties and their ranges can be customized within the config files. Modify the properties for your specific needs.

## Contributing

Contributions are welcome! If you have suggestions, bugs, or improvements, feel free to open an issue or a pull request. Ensure that all code adheres to the coding standards and includes necessary comments.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- **Plugin Developer**: Your Name
- **Contributors**: List of contributors if applicable

