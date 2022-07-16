Adds block interactors to the game. Wiki --> https://github.com/Santas-Spy/blockinteractors/wiki

Block Interactors can interact with the world in different way. Placers will take a block from their inventory and place it in the world infront of them. Breakers will break the block infront of the, and Miners will mine the block infront of them (This is not vanilla mining however as you will see)

**So how do you make an interactor?**

By placing a certain item in an item frame on the side of a dispenser, you will turn that dispenser into an interactor. These items are completely customizable in the config, and you can have multiple for each type of interactor.

**So what do these interactors do?**

Well when powered by redstone they will all do different things. Breakers and Placers are pretty self explanatory, they break/place the block infront of them. Miners are where it gets more interesting.

Miners function more like the miners from Factorio than mining in minecraft. You can specify a list of blocks that they can 'mine' as follows:
```yaml
diamond_ore:
    fuels: [coal,CUSTOM:superfuel]
    gives: diamond:1
```
The first line is the name of the block that is mineable, the fuels are the items that must be present in the dispenser to act as fuel (which ill explain in a bit), and gives is the result of the mining. This is a fairly vanilla mining but it could be anything, you could mine sponge using iron pickaxes as fuel and have it drop steak is you wanted.

Fuels are items that must be present inside a miner in order for it to work. These fuels are defined as follows:
```yaml
coal:
    timer: 20
CUSTOM:superfuel:
    timer: 1
```
The first line is the name of the item, and the second line is the cooldown timer in ticks. This means that if you were to use coal as a fuel in a miner, it will take the miner 20 ticks to finish mining, while superfuel will only take 1 tick.

The CUSTOM: part comes from this plugins integration with SantasCrafting (https://www.spigotmc.org/resources/santascrafting.103406/). You can use any items defined there by writing them as CUSTOM: and then their placeholder name. These plugins were designed to be used together so anywhere you can specify an item, you can use SantasCrafting items.

Spigot is not the place to reach me for help, if you want to ask questions come join my discord https://discord.gg/mNgncUnhdX
