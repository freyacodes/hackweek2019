# FredBoat Improved Edition
![HW 2019](https://img.shields.io/badge/Discord%20Hack%20Week-2019-%23000000.svg
 "Hack Week 2019")
 
FredBoat Improved Edition is the Discord music bot that is arguably 400% as good as any other bot, given that it can play 400% as much music as other bots using our new advanced audio mixing technology (which I totally didn't copy from GitHub).

See more: https://hackweek.fredboat.com/

## I'm brave enough to selfhost this thing
*(We'll see about that.)*

Requirements:
* You'll need JDK 11 to compile this thing.
* We use a native audio sender to escape the evil grasp of the garbage collector.
This might not work on certain systems due to missing binaries, though I believe x86_64 Linux and Windows should be fine.

### Compile the thing
Clone this repository and enter its directory with your terminal, then run this command:

```bash
./gradlew shadowJar
```

> Or `gradlew.bat shadowJar` if you use Windows

Now watch as Gradle downloads itself and automagically teleports all dependencies to your device.
A standalone executable jar will appear at `build/libs/fip-1.0.jar`

### Run the thing
The bot token is read from the `DISCORD_TOKEN` environment variable. There are no other configuration options.
Run it like this:

```bash
DISCORD_TOKEN="NTkzMTMz..." java -jar fip-1.0.jar
```