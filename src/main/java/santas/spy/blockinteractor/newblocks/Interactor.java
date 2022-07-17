package santas.spy.blockinteractor.newblocks;

import org.bukkit.Location;

public interface Interactor {
    public abstract void interact();
    public abstract Location getLocation();
    public abstract String saveData();
}
