package santas.spy.listeners;

import org.bukkit.event.block.BlockDispenseEvent;

import santas.spy.BlockInteractor;
import santas.spy.newblocks.Interactor;

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
            event.setCancelled(true);
            interactor.interact();
        }
    }
    
}
