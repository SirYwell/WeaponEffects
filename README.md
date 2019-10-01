# WeaponEffects

WeaponEffects is a simple but yet powerful plugin to customize game experience of [Minecraft](https://minecraft.net).
It simply adds [Status effects](https://minecraft.gamepedia.com/Status_effect) (also known as Potion effects) to items.
Holding them in your hand thereby gives you the specified status effects.

This plugin works on Spigot/Paper 1.14.4. 

## Installation on Spigot

Simply run `mvn package` to build the plugin jar. A download link will be added soon™.
Drop the jar file and the [Item-NBT-API](https://www.spigotmc.org/resources/item-entity-tile-nbt-api.7939/) in your
plugin folder and restart the server.

## Configuration

You can adjust the behaviour of the plugin by editing the `config.yml` file in the plugin's directory. 
The different settings are explained there.

Edit the `messages.yml` if you want to change the messages. Colors are supported.

**Please note**: If you want to edit messages like the command help, you'll need to edit files inside of the .jar file. 
Those messages are provided by the Annotation Command Framework.

## How to use

Please check the [wiki](https://github.com/SirYwell/WeaponEffects/wiki/) to see if your question is answered there. Important topics:

- [Commands](https://github.com/SirYwell/WeaponEffects/wiki/Commands)

## Dependencies

To get the plugin run on your server, you need to install 
[Item-NBT-API](https://www.spigotmc.org/resources/item-entity-tile-nbt-api.7939/).
This helps to keep the plugin compatible with different versions.

Internally, the plugin uses Aikar's 
[Annotation Command Framework](https://www.spigotmc.org/threads/acf-beta-annotation-command-framework.234266/) to handle
commands in a smooth and intelligent way.

## Support
If you need help, feel free to reach me out on Discord: `Max Mustermann#0815`.

#### License

This plugin is licensed under the terms of the MIT License.
