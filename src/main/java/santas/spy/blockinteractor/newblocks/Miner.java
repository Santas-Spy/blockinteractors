package santas.spy.blockinteractor.newblocks;

import org.bukkit.Bukkit;
import org.bukkit.block.data.Directional;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import santas.spy.blockinteractor.BlockInteractor;
import santas.spy.blockinteractor.config.Config;
import santas.spy.blockinteractor.config.Fuel;
import santas.spy.blockinteractor.config.Mineable;

public class Miner implements Interactor
{
    private Dispenser miner;
    private Block minerBlock;
    private boolean isMining;
    private Config config;
    private Location mineableLocation;

    public Miner(Dispenser inBlock)
    {
        miner = inBlock;
        minerBlock = inBlock.getBlock();
        isMining = false;
        setMineableLocation();
        config = Config.getConfig();
    }

    @Override
    public void interact(ItemStack dispensedItem)
    {
        if (!isMining) {
            BlockInteractor.debugMessage("interact() called on miner", 2);
            BlockInteractor.debugMessage("Trying to get mineable for block type " + mineableLocation.getBlock().getType(), 2);
            Mineable toMine = config.mineables().get(mineableLocation.getBlock().getType());
            if (toMine != null) {
                Fuel fuelType = getFuel(toMine, dispensedItem);
                if (fuelType != null) {
                    isMining = true;
                    BlockInteractor.debugMessage("Begining Mining", 2);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BlockInteractor.PLUGIN, new Runnable() {
                        @Override
                        public void run() {
                            minerBlock.getWorld().dropItemNaturally(mineableLocation.clone().add(0,0.5,0), toMine.result());
                            isMining = false;
                            if (minerBlock.getBlockPower() > 0) {
                                miner.dispense();
                            }
                        }
                    }, fuelType.timer());
                } else {
                    BlockInteractor.debugMessage("No fuel was found in the miner", 2);
                    BlockInteractor.debugMessage("Valid fuels are: ", 2);
                    for (Fuel validFuel : toMine.fuels()) {
                        BlockInteractor.debugMessage("\t" + validFuel.name(), 2);
                    }
                }
            } else {
                BlockInteractor.debugMessage("No mineable block was found infront of the miner", 2);
            }
        }
    }

    private Fuel getFuel(Mineable toMine, ItemStack dispensedItem)
    {
        Fuel fuelType = null;
        ItemStack[] inv = ((Dispenser)minerBlock.getState()).getInventory().getContents();

        int i = 0;
        boolean found = false;
        while (!found && i < inv.length) {
            BlockInteractor.debugMessage("Checking slot " + i + " which was " + inv[i], 2);
            for (Fuel fuel : toMine.fuels()) {
                //actual check
                if (inv[i] != null && inv[i].isSimilar(fuel.fuel())) {
                    found = true;
                    fuelType = fuel;
                    inv[i].setAmount(inv[i].getAmount() - 1);
                    BlockInteractor.debugMessage("Found fuel " + fuel.name() + " at slot " + i, 2);
                }
            }
            if (!found) {
                i++;
            }
        }

        if (!found) {
            for (Fuel fuel : toMine.fuels()) {
                if (dispensedItem.isSimilar(fuel.fuel())) {
                    fuelType = fuel;
                    removeItem(dispensedItem);
                    BlockInteractor.debugMessage("Dispensed Item (" + dispensedItem + ") was a valid fuel", 2);
                }
            }
        }
        //miner.getInventory().setContents(inv);
        //BlockInteractor.debugMessage("miner.update() returned " + miner.update(), 2);
        return fuelType;
    }

    public boolean getMiningStatus()
    {
        return isMining;
    }

    public Block getBlock()
    {
        return minerBlock;
    }

    public String saveData() {
        String data = "miner,";
        data += minerBlock.getWorld().getName();
        data += ",";
        data += minerBlock.getX();
        data += ",";
        data += minerBlock.getY();
        data += ",";
        data += minerBlock.getZ();
        return data;
    }

    private void setMineableLocation()
    {
        mineableLocation = minerBlock.getLocation().add(((Directional)miner.getBlockData()).getFacing().getDirection());
    }

    @Override
    public Location getLocation()
    {
        return minerBlock.getLocation();
    }

    private void removeItem(ItemStack toRemove)
    {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BlockInteractor.PLUGIN, new Runnable() {
            @Override
            public void run() {
                ((Dispenser)minerBlock.getState()).getInventory().removeItem(toRemove);
            }
        }, 1);
    }
}
