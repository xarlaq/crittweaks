# CritTweaks

A lightweight, **client-side** Fabric mod that gives you full control over critical-hit and
enchanted-hit particles in combat. Hide them on yourself or other players to cut visual
clutter, or force them to render even when your particle setting is on **Minimal**.

> Note: this is purely a client-side visual mod. It changes nothing on the server and gives
> no gameplay advantage — it only decides which particles *your* client draws.

## Features

CritTweaks splits its options into two categories, configurable in-game via
[Mod Menu](https://modrinth.com/mod/modmenu) + [Cloth Config](https://modrinth.com/mod/cloth-config).

A master **Mod Enabled** toggle turns the whole mod on/off (off = pure vanilla). The rest is
split into two mirrored sections — **Criticals** and **Sharpness** (enchanted-hit particles).

### Criticals
| Option | Default | What it does |
| --- | --- | --- |
| **Force Crit Particles** | on | Renders crit particles even on the *Minimal* particle setting. |
| **Always Crit Particles** | off | Spawns crit particles on every hit you land, even when it isn't a real critical. |
| **Hide Other Player Crits** | on | Only shows crits from hits *you* land; hides other players' crits. |
| **Hide Crits On Yourself** | on | Hides crit particles that appear on you when you're hit. |
| **Crit Particle Multiplier** | 100% | Particle amount per crit, 10–1000% of vanilla (100 = x1.0). Applies while *Force Crit Particles* is on. |

### Sharpness
The same five options for enchanted-hit ("sharpness") particles. **Force** and the
**Multiplier** default the same way; **Always Sharpness Particles** is off by default.

"Hits you land" is tracked for a short window (1 second) after you attack an entity, so your
own particles on a target still show even when other players' are hidden.

If Cloth Config isn't installed, CritTweaks runs with these defaults and skips the config screen.

## Requirements

- **Fabric Loader** `>=0.16.0`
- **Fabric API**
- **Java 21** (Minecraft 1.21.x)
- *Recommended:* Cloth Config & Mod Menu (for the in-game settings screen)

## Supported versions

| Minecraft | Jar | Mappings | Java |
| --- | --- | --- | --- |
| 1.21.1 – 1.21.11 | `crittweaks-<ver>+1.21.x.jar` | Yarn (obfuscated) | 21 |
| 26.1+ | `crittweaks-<ver>+26.1.x.jar` | Mojang official (unobfuscated) | 25 |

Because Minecraft 26.1 is the first *unobfuscated* release, it uses a different mapping
namespace than the 1.21.x line, so the two eras ship as **separate jars** — one universal
jar cannot span both. Both are fully supported: mapping-agnostic code is shared in
`src/main/java`, while the mixins and Mod Menu hook (the only mapping-specific classes) have
per-era copies in `src/yarn/java` and `src/mojang/java`.

## Building

The project is single-source, multi-target. Pick a Minecraft version with `-Pmc_version`;
each version profile lives in [`versions/`](versions/).

```bash
# 1.21.x jar (build against the newest of the line)
./gradlew build -Pmc_version=1.21.11

# 26.1.x jar
./gradlew build -Pmc_version=26.1.2
```

Output jars land in `build/libs/`. They're labelled with the minor-version **range** the
jar covers rather than the exact build target — `crittweaks-<modver>+<range>.jar`, e.g.
`crittweaks-1.0.0+1.21.x.jar` and `crittweaks-1.0.0+26.1.x.jar`. If no `-Pmc_version` is
given, the default from [`gradle.properties`](gradle.properties) is used.

Toolchain: Fabric Loom 1.15, Gradle 9.4. The build selects the Yarn/remapping setup for
obfuscated targets and the non-remapping setup for 26.1+ automatically.

## License

Released under the [GNU General Public License v3.0](LICENSE). You're free to use, study,
modify, and share it — but any distributed forks must also stay open source under the GPL.
