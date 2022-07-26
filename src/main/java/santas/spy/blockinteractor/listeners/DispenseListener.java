package santas.spy.blockinteractor.listeners;

import org.bukkit.event.block.BlockDispenseEvent;

import santas.spy.blockinteractor.BlockInteractor;
import santas.spy.blockinteractor.newblocks.Interactor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DispenseListener implements Listener
{
    private static BlockInteractor plugin = BlockInteractor.PLUGIN;

    @EventHandler
    public void onDispense(BlockDispenseEvent event)
    {
        Interactor interactor = plugin.findInteractor(event.getBlock().getLocation());
        if (interactor != null) {
            BlockInteractor.debugMessage("Dispensed item: " + event.getItem(), 2);
            event.setCancelled(true);
            interactor.interact(event.getItem());
        }
    }
}
