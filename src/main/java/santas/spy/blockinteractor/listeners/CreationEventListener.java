package santas.spy.blockinteractor.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import santas.spy.blockinteractor.BlockInteractor;
import santas.spy.blockinteractor.config.Config;
import santas.spy.blockinteractor.newblocks.Breaker;
import santas.spy.blockinteractor.newblocks.Miner;
import santas.spy.blockinteractor.newblocks.Placer;


public class CreationEventListener implements Listener
{
    private static BlockInteractor plugin = BlockInteractor.PLUGIN;
    private Config config = Config.getConfig();

    @EventHandler
    public void onCreateListener(PlayerInteractEntityEvent event)
    {
        if (event.getRightClicked() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame)event.getRightClicked();
            Block block = itemFrame.getLocation().getBlock().getRelative(itemFrame.getAttachedFace());
            if (block.getType() == Material.DISPENSER) {
                ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
                Dispenser interactor = (Dispenser)block.getState();
                if (itemInList(itemInHand, config.breakerItems())) {
                    if (event.getPlayer().hasPermission("blockinteractor.create.breaker")) {
                        createBreaker(interactor);
                        event.getPlayer().sendMessage("You have made a new Breaker");
                    } else { 
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("You do not have permission to create a new Breaker");
                    }
                } else {
                    if (itemInList(itemInHand, config.placerItems())) {
                        if (event.getPlayer().hasPermission("blockinteractor.create.placer")) {
                            createPlacer(interactor);
                            event.getPlayer().sendMessage("You have made a new Placer");
                        } else { 
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("You do not have permission to create a new Placer");
                        }
                    } else {
                        if (itemInList(itemInHand, config.minerItems())) {
                            if (event.getPlayer().hasPermission("blockinteractor.create.miner")) {
                                createMiner(interactor);
                                event.getPlayer().sendMessage("You have made a new Miner");
                            } else { 
                                event.setCancelled(true);
                                event.getPlayer().sendMessage("You do not have permission to create a new Miner");
                            }
                        } else {
                            BlockInteractor.debugMessage(event.getPlayer().getName() + " put an item in an itemframe on a dispenser but item was not any creation item", 2);
                        }
                    }
                }
            } else {
                BlockInteractor.debugMessage(event.getPlayer().getName() + " clicked an itemframe but block was not dispenser. It was " + block.getType(), 2);
            }
        }
    }

    @EventHandler
    public void onDestroyListener(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof ItemFrame)
        {
            ItemFrame itemFrame = (ItemFrame)event.getEntity();
            Location location = itemFrame.getLocation().getBlock().getRelative(itemFrame.getAttachedFace()).getLocation();
            if (location.getBlock().getType().equals(Material.DISPENSER))
            {
                ItemStack itemInFrame = itemFrame.getItem();
                if (config.breakerItems().contains(itemInFrame) || config.placerItems().contains(itemInFrame) || config.minerItems().contains(itemInFrame))
                {
                    if (plugin.removeInteractor(location)) {
                        event.getDamager().sendMessage("You have destroyed an interactor");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDestroyListener(BlockBreakEvent event)
    {
        if (event.getBlock().getType() == Material.DISPENSER) {
            if (plugin.removeInteractor(event.getBlock().getLocation())) {
                event.getPlayer().sendMessage("You have destroyed an interactor");
            }
        }
    }

    private void createBreaker(Dispenser breaker)
    {
        plugin.addInteractor(new Breaker(breaker));
    }

    private void createPlacer(Dispenser breaker)
    {
        plugin.addInteractor(new Placer(breaker));
    }

    private void createMiner(Dispenser breaker)
    {
        plugin.addInteractor(new Miner(breaker));
    }

    private boolean itemInList(ItemStack testItem, List<ItemStack> items)
    {
        boolean found = false;
        for (ItemStack item : items) {
            if (testItem.isSimilar(item)) {
                found = true;
            }
        }

        return found;
    }
}
