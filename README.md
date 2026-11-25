# Loot Beams Backport

A backport of shiroroku's Loot Beams to **Minecraft 1.7.10 / Forge**. Dropped
items emit a colored beam so they are easier to find.

Current version: **V1.0.7**. This fork fixes item-name color parsing, adds color
and item filters, and fixes missing or off-center beams in GT New Horizons.

[中文配置与验证说明](TESTING_zh_CN.md)

## Installation

1. Close Minecraft and remove the previous Beaming Drops / Loot Beams Backport
   JAR from the client's `mods` directory. Keep only one version installed.
2. Install `beamingdrops-V1.0.7.jar`. The `-dev` and `-sources` JARs are for
   development, not installation.
3. A GTNH-compatible Mixin loader is required; GTNH 2.9.1 already includes
   UniMixins. Keep the loader supplied by the pack.
4. Start the game. Existing configuration is preserved and new options are
   added automatically. Restart Minecraft after editing the configuration.

The mod ID remains `beamingdrops`. Configuration is stored at:

```text
.minecraft/config/beamingdrops/BeamingDropMixinsEarly.cfg
```

## Changes in V1.0.7

- Parse consecutive name formatting codes correctly. Style codes and trailing
  reset codes no longer hide the intended color. Unformatted names fall back to
  the item's rarity color.
- Allow every color, including white/common items, by default. Add independent
  color and item blacklist/whitelist settings.
- Support registry IDs with all metadata, an explicit metadata value, or an
  explicit `*`. Fix ore dictionary name matching.
- Draw beams after entity rendering, covering 3D blocks, icons, and custom item
  renderers through one hook. Fix missing beams on blocks and the offset/orbiting
  beam seen with GregTech item renderers.
- Center beams on the dropped entity, restore OpenGL state after drawing, and
  interpolate rotation and floating between ticks.
- Express beam height and diameter in block units, independent of item model
  scaling. Existing settings may produce taller or wider beams than V1.0.6;
  adjust `beamHeight` and `beamDiameter` if needed.
- Keep the JAR filename, Forge metadata, and in-game version synchronized, and
  restore the Gradle build with automated regression tests.

## Configuration

Default settings:

```text
"beam settings" {
    B:needRenderBeam=true
    B:useHighResBeam=true
    B:useRotatingBeam=true
    B:useFloatingBeam=true
    S:beamDiameter=0.25
    S:beamHeight=4.0

    B:isWhitelist=false
    S:itemBlackList <
     >

    B:isColorWhitelist=false
    S:colorBlackList <
     >

    B:useDepthMask=false
}
```

| Setting | Meaning |
| --- | --- |
| `needRenderBeam` | Enable or disable all beams. |
| `useHighResBeam` | Use the 256×256 texture; `false` selects the 16×16 texture. |
| `useRotatingBeam` | Rotate the beam core over time. |
| `useFloatingBeam` | Move the beam slightly up and down, independently of rotation. |
| `beamDiameter` | Beam width in blocks, from `0.05` to `3.0`. |
| `beamHeight` | Beam height in blocks, from `0.5` to `32.0`. |
| `itemBlackList` / `isWhitelist` | Item rules and whether they act as a whitelist. |
| `colorBlackList` / `isColorWhitelist` | Color rules and whether they act as a whitelist. |
| `useDepthMask` | Write beams to the depth buffer; usually leave `false` for transparency. Blocks still occlude beams in either mode. |

Both the item filter and the color filter must allow an item. An empty blacklist
allows everything; an empty whitelist allows nothing. The original item filter
option names are retained for compatibility with existing configuration files.

### Beam colors

The beam uses the effective color of the first visible, non-whitespace character
in the display name. Formatting codes are processed in order: styles such as
bold do not change the color, `§r` resets it to white, and formatting after that
first character does not change the selected beam color. An unformatted name
uses the rarity color. A multicolored name produces one beam color.

Color filter entries accept Minecraft color names, formatting codes (`0`–`f`,
or `§9`), or RGB hex values (`#5555FF`, `0x5555FF`, or `5555FF`). Color names and
hex values are case-insensitive. Invalid entries match nothing.

Available names: `black`, `dark_blue`, `dark_green`, `dark_aqua`, `dark_red`,
`dark_purple`, `gold`, `gray`, `dark_gray`, `blue`, `green`, `aqua`, `red`,
`light_purple`, `yellow`, `white`. The legacy `minecoin_gold` / `g` extension is
also retained. `gold` is `#FFAA00`; `yellow` is `#FFFF55`.

For example, hide white/common beams by replacing the corresponding options
inside the existing category:

```text
B:isColorWhitelist=false
S:colorBlackList <
    white
 >
```

To show only blue and bright purple beams, use:

```text
B:isColorWhitelist=true
S:colorBlackList <
    blue
    light_purple
 >
```

### Item IDs and metadata

| Rule | Matches |
| --- | --- |
| `modid:item` | All metadata values of that registry ID. |
| `modid:item:*` | All metadata values of that registry ID. |
| `modid:item:123` | Only metadata `123`. |
| `ore:oreCoal` | Items registered under the ore dictionary name `oreCoal`. |

Registry IDs and ore dictionary names are case-sensitive. GregTech uses metadata
for many item variants: include the metadata to filter one variant. Bare IDs now
match **all metadata**, whereas V1.0.6 treated them as metadata `0`; add `:0` to an
old rule if that narrower behavior is desired.

For example, hide cobblestone and red wool:

```text
B:isWhitelist=false
S:itemBlackList <
    minecraft:cobblestone
    minecraft:wool:14
 >
```

## Validation and reporting

The rendering fixes were tested in game by the maintainer on GTNH 2.9.1, with
working beams and correct positioning reported. Fourteen automated tests cover
name colors, filter rules, configuration migration, and beam geometry. The
V1.0.7 build packages the same rendering code as the tested
`V1.0.6-renderfix.2` build.

Optional smoke checks and the information needed for bug reports are listed in
the [Chinese guide](TESTING_zh_CN.md). Shader packs, other modpacks, and every
custom renderer have not been exhaustively tested.

## Building

Use JDK 17 or 21 to run the Gradle wrapper:

```powershell
.\gradlew.bat build
```

On Linux/macOS, run `./gradlew build`. The build selects its compiler toolchain
as needed and targets Java 8 bytecode. JARs are written to `build/libs/`; the test
report is at `build/reports/tests/test/index.html`.

The GTNH settings plugin is pinned to `1.0.30`. The old optional NEI development
dependency and ItemPhysic-specific compile dependency are no longer needed;
ItemPhysic uses the shared entity render hook.

## Credits

Original mod: **shiroroku**. Backport contributors include **Veritaris**,
**quentin452**, **Kotmatross**, and the other code contributors credited by the
project. This fork builds on their work.

<details><summary>Original preview GIF</summary>

![Example](./media/lootbeamsbackport_30.gif)

</details>
