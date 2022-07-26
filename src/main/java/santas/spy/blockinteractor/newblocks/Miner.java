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
    private boolean isMining;
    private Config config;
    private Location mineableLocation;

    public Miner(Dispenser inBlock)
    {
        miner = inBlock;
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
                Fuel fuelType = getFuel(toMine, miner.getInventory().getContents(), dispensedItem);
                if (fuelType != null) {
                    isMining = true;
                    BlockInteractor.debugMessage("Begining Mining", 2);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BlockInteractor.PLUGIN, new Runnable() {
                        @Override
                        public void run() {
                            miner.getWorld().dropItemNaturally(mineableLocation.clone().add(0,0.5,0), toMine.result());
                            isMining = false;
                            if (miner.getBlock().getBlockPower() > 0) {
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

    private Fuel getFuel(Mineable toMine, ItemStack[] inv, ItemStack dispensedItem)
    {
        Fuel fuelType = null;

        int i = 0;
        boolean found = false;
        while (!found && i < inv.length) {
            for (Fuel fuel : toMine.fuels()) {
                //debug info
                if (config.debug() > 1) {
                    String message = "Checking fuel " + fuel.name() + " against item in slot " + i + " which has type: ";
                    if (inv[i] == null) {
                        message += "null";
                    } else {
                        message += inv[i].getType();
                    }
                    BlockInteractor.debugMessage(message, 2);
                }

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
                    BlockInteractor.debugMessage("Dispensed Item was a valid fuel", 2);
                }
            }
        }

        return fuelType;
    }

    public boolean getMiningStatus()
    {
        return isMining;
    }

    public Block getBlock()
    {
        return miner.getBlock();
    }

    public String saveData() {
        String data = "miner,";
        data += miner.getWorld().getName();
        data += ",";
        data += miner.getX();
        data += ",";
        data += miner.getY();
        data += ",";
        data += miner.getZ();
        return data;
    }

    private void setMineableLocation()
    {
        mineableLocation = miner.getLocation().add(((Directional)miner.getBlockData()).getFacing().getDirection());
    }

    @Override
    public Location getLocation()
    {
        return miner.getLocation();
    }
}
