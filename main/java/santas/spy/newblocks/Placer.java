package santas.spy.newblocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

import santas.spy.BlockInteractor;

public class Placer implements Interactor {

    private Dispenser placer;

    public Placer(Dispenser placer) {
        this.placer = placer;
    }

    @Override
    public void interact()
    {
        Directional direction = (Directional)placer.getBlockData();
        Location location = placer.getLocation().add(direction.getFacing().getDirection());
        if (location.getBlock().getType().equals(Material.AIR)) {
            Material type = toPlace();
            if (type != null) {
                location.getBlock().setType(type);
            }
        }
    }

    private Material toPlace()
    {
        boolean found = false;
        int i = 0;
        ItemStack[] inv = placer.getInventory().getContents();
        Material type = null;

        while (!found && i < inv.length) {
            if (inv[i] != null && inv[i].getType().isBlock()) {
                found = true;
                type = inv[i].getType();
                BlockInteractor.debugMessage(i + ": " + type + " FOUND", 2);
                inv[i].setAmount(inv[i].getAmount() -1);
            } else {
                if (inv[i] == null) {
                    BlockInteractor.debugMessage(i + ": null", 2);
                } else {
                    BlockInteractor.debugMessage(i + ": " + inv[i].getType(), 2);
                }
                i++;
            }
        }

        return type;
    }

    @Override
    public Location getLocation()
    {
        return placer.getLocation();
    }

    @Override
    public String saveData()
    {
        String data = "placer,";
        data += placer.getWorld().getName();
        data += ",";
        data += placer.getX();
        data += ",";
        data += placer.getY();
        data += ",";
        data += placer.getZ();
        return data;
    }
}
