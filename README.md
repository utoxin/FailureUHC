# Failure UHC: When Rabbits Attack

A simple (at the moment) mod to facilitate running UHC matches on 1.8, with the option of allowing a controlled list of mods. Also, killer rabbits.

This mod needs to be installed on both the server and the client, although the client is just there for the text assets. On the server side, you can edit the config file to control the game settings. Just launch the server once, and it will create the config, which is reasonably self explanatory. 

If you want to whitelist a mod, you just need the Mod ID for that list. If you don't know it, attempt to connect to the server with it set in whitelist mode. It will log an error listing the Mod ID. In the future, I will be adding a 'match server' and 'learning' mode for the whitelist to simplify that portion of setup.

Once you're ready to start the match, with everyone logged in, just type '/startmatch'. It will teleport everyone to their starting point, and configure the game rules to match what you selected in the config.

Currently in development, features for first release are:

* Bedrock Arena Wall
* Deadly Rabbits
* 'Spectate on Death'
* Mod Whitelist
* Chat Controls
* Game Timing
* Waiting Area structure

Future features will include:

* Disable beds
* Disable Nether Portals
* Team Support
* Pregenerate Arena command
* Many other things
* And probably stuff
