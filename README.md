# WeaponEffects

WeaponEffects is a simple but yet powerful plugin to customize game experience of [Minecraft](https://minecraft.net).
It simply adds [Status effects](https://minecraft.gamepedia.com/Status_effect) (also known as Potion effects) to items.
Holding them in your hand therefore gives you the specified status effects.

## Installation on Spigot

Simply run `mvn package` to build the plugin jar. A download link will be added soon™.
Drop the jar file and the [Item-NBT-API](https://www.spigotmc.org/resources/item-entity-tile-nbt-api.7939/) in your
plugin folder and restart the server.

## Dependencies

To get the plugin run on your server, you need to install 
[Item-NBT-API](https://www.spigotmc.org/resources/item-entity-tile-nbt-api.7939/).
This helps to keep the plugin compatible with different versions.

Internally, the plugin uses Aikar's 
[Annotation Command Framework](https://www.spigotmc.org/threads/acf-beta-annotation-command-framework.234266/) to handle
commands in a smooth and intelligent way.