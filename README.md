# Langbly Translate Plugin for OmegaT

Machine translation plugin that connects OmegaT to the [Langbly API](https://langbly.com). Translate segments in 100+ languages directly inside OmegaT.

## Features

- 100+ language pairs
- EU data residency option (eu.langbly.com)
- Simple configuration: just your API key
- Automatic caching of translations within OmegaT

## Requirements

- OmegaT 6.0.0 or later
- Java 11 or later
- A Langbly API key ([sign up free](https://langbly.com/signup))

## Installation

1. Download the latest `omegat-plugin-langbly-x.x.x.jar` from [Releases](https://github.com/Langbly/omegat-plugin-langbly/releases)
2. Copy the JAR to your OmegaT plugins directory:
   - **Windows**: `C:\Users\<user>\AppData\Roaming\OmegaT\plugins\`
   - **macOS**: `~/Library/Preferences/OmegaT/plugins/`
   - **Linux**: `~/.omegat/plugins/`
3. Restart OmegaT

## Configuration

1. Open **Options > Preferences > Machine Translation**
2. Enable **Langbly Translate**
3. Click **Configure** and enter your API key
4. Select your region (Global or EU)

## Building from Source

```bash
./gradlew jar
```

The plugin JAR will be in `build/libs/`.

## License

This plugin is licensed under the [GNU General Public License v3](COPYING).
