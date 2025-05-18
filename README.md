![Land-Lord-4-1-2025](https://github.com/user-attachments/assets/945b46bf-c7c7-4672-9328-e68cc8d5a7db)

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
![GitHub issues](https://img.shields.io/github/issues/s5y-ux/LandLord)
![GitHub stars](https://img.shields.io/github/stars/s5y-ux/LandLord?style=social)

## Overview

**LandLord** is a Bukkit plugin that introduces a property management system. Players can spawn "renters" (villagers) into designated properties, evict them with a special item, and manage them using tools like the "Renter Remover Wand."

---

## Features

- **Renter Spawn Egg** – Spawn renters (villagers) in a designated property using a custom egg item.
- **Property Range Check** – Renters can only be spawned within the bounds of a specific property.
- **Renter Remover Wand** – Right-click a renter to evict them instantly.
- **Anti-Damage** – Renters are immune to player or environmental damage.
- **Event Handling** – Custom events for spawning and despawning renters.

---

## Installation

1. Download the latest release from the [Releases](https://modrinth.com/plugin/morebosses) page.
2. Place the `.jar` file in your server's **plugins** folder.
3. Restart your server to load the plugin.

---

## Commands

```bash
/property - Get the Property Wand
/removerenter - Get the Renter Remover Wand
/managehouses - Open the Deed Menu to manage properties
```

---

## Permissions

```bash
landlord.getwand - Allows use of /property
landlord.getremover - Allows use of /removerenter
landlord.managehouses - Allows use of /managehouses
```

---

## How to Use

### Spawning Renters

1. Get the **Renter Spawn Egg** from your inventory via command or plugin feature.  
2. Throw the egg inside a designated property to spawn a renter.  
3. The renter is automatically linked to the property; a success message will confirm it.

### Removing Renters

1. Right-click a renter with the **Renter Remover Wand**.  
2. You’ll receive confirmation when the renter has been evicted.

### Renter Protection

- Renters are immune to damage, preventing accidental harm or griefing.

---

## Configuration

Property data is stored in JSON format. You can customize properties, ranges, and renter behavior in the config files to suit your server’s needs.

---

## Contributing

Suggestions, issues, or pull requests are welcome! Make sure your code follows the plugin’s standards and includes necessary documentation/comments.

---

## License

This plugin is licensed under the MIT License – see the [LICENSE](http://LICENSE) file for full details.

---

## Credits

- **Plugin Developer**: s5y
- **Contributer(s)**: Get a pull request merged and appear here!

