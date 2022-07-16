package santas.spy.newblocks;

import org.bukkit.Bukkit;
import org.bukkit.block.data.Directional;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import santas.spy.BlockInteractor;
import santas.spy.config.Config;
import santas.spy.config.Fuel;
import santas.spy.config.Mineable;

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
    public void interact()
    {
        if (!isMining) {
            BlockInteractor.debugMessage("interact() called on miner", 2);
            //if facing mineable AND has fuel
            //take fuel and schedule drop mineable result in fuel timer time
            Block block = mineableLocation.getBlock();
            BlockInteractor.debugMessage("Trying to get mineable for block type " + block.getType(), 2);
            Mineable toMine = config.mineables().get(block.getType());
            Fuel fuelType = null;
            if (toMine != null) {
                ItemStack[] inv = miner.getInventory().getContents();
                boolean found = false;
                int i = 0;
                while (!found && i < inv.length) {
                    for (Fuel fuel : toMine.fuels()) {
                        if (inv[i] != null && inv[i].isSimilar(fuel.fuel())) {
                            found = true;
                            fuelType = fuel;
                        }
                    }
                    if (!found) {
                        i++;
                    }
                }

                if (found) {
                    inv[i].setAmount(inv[i].getAmount() -1);
                    isMining = true;
                    BlockInteractor.debugMessage("Begining Mining", 2);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(BlockInteractor.PLUGIN, new Runnable() {
                        @Override
                        public void run() {
                            miner.getWorld().dropItemNaturally(mineableLocation.clone().add(0,0.5,0), toMine.result());
                            isMining = false;
                            if (miner.getBlock().getBlockPower() > 0) {
                                interact();
                            }
                        }
                    }, fuelType.timer());
                } else {
                    BlockInteractor.debugMessage("No fuel was found in the miner", 2);
                    BlockInteractor.debugMessage("Valid fuels are: ", 2);
                    for (Fuel fuel : toMine.fuels()) {
                        BlockInteractor.debugMessage("\t" + fuel.name(), 2);
                    }
                }
            } else {
                BlockInteractor.debugMessage("No mineable block was found infront of the miner", 2);
            }
        }
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
