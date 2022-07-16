package santas.spy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Dispenser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import santas.spy.config.Config;
import santas.spy.listeners.CommandListener;
import santas.spy.listeners.CreationEventListener;
import santas.spy.listeners.DispenseListener;
import santas.spy.newblocks.Breaker;
import santas.spy.newblocks.Interactor;
import santas.spy.newblocks.Miner;
import santas.spy.newblocks.Placer;

public class BlockInteractor extends JavaPlugin {

    public static BlockInteractor PLUGIN;
    private List<Interactor> interactors = new ArrayList<>();
    public static SantasCrafting CRAFTING;
    private Config config;

    @Override
    public void onEnable() 
    {
        getLogger().info(ChatColor.GREEN + "Starting Plugin");
        PLUGIN = this;

        getLogger().info(ChatColor.GREEN + "Loading Files");
        generateFiles();
        
        getLogger().info(ChatColor.GREEN + "Checking for SantasCrafting");
        findSantasCrafting();

        getLogger().info(ChatColor.GREEN + "Registering Listeners");
        registerListeners();

        getLogger().info(ChatColor.GREEN + "Loading Config");
        config = Config.getConfig();
        config.reload();

        getLogger().info(ChatColor.GREEN + "Loading Interactors");
        loadInteractors();

        getLogger().info(ChatColor.GREEN + "Startup Complete");
    }

    @Override
    public void onDisable() 
    {
        getLogger().info("Saving Interactors");
        saveInteractors();
        getLogger().info("Disabling BlockInteractors");
    }

    public static void debugMessage(String message, int requiredLevel)
    {
        if (PLUGIN.config.debug() >= requiredLevel)
        {
            PLUGIN.getLogger().info(message);
        }
    }

    public void addInteractor(Interactor interactor)
    {
        if (findInteractor(interactor.getLocation()) == null) {
            interactors.add(interactor);
        } else {
            debugMessage("tried to make a new interactor but there was already one there", 2);
        }
    }

    public boolean removeInteractor(Location block)
    {
        Interactor interactor = findInteractor(block);
        boolean found = false;
        if (interactor != null) {
            found = true;
            interactors.remove(interactor);
        }
        return found;
    }

    public Interactor findInteractor(Location block)
    {
        int i = 0;
        boolean found = false;
        Interactor interactor = null;
        while (!found && i < interactors.size()) {
            if (interactors.get(i).getLocation().equals(block)) {
                found = true;
                interactor = interactors.get(i);
            } else {
                i++;
            }
        }
        return interactor;
    }

    public List<Interactor> getInteractors()
    {
        return interactors;
    }

    private void registerListeners()
    {
        getServer().getPluginManager().registerEvents(new CreationEventListener(), this);
        getServer().getPluginManager().registerEvents(new DispenseListener(), this);
        this.getCommand("blockinteractor").setExecutor(new CommandListener());
    }

    private void findSantasCrafting()
    {
        Plugin santasCrafting = PLUGIN.getServer().getPluginManager().getPlugin("SantasCrafting");
        if (santasCrafting == null) {
            getLogger().info(ChatColor.YELLOW + "SantasCrafting not found. Recipies and Creation Items using Custom Items will be disabled");
            CRAFTING = null;
        } else {
            getLogger().info(ChatColor.GREEN + "Found SantasCrafting");   
            CRAFTING = (SantasCrafting)santasCrafting;
        }
    }

    private void saveInteractors()
    {
        File file = new File(PLUGIN.getDataFolder(), "interactors.txt");
        try (PrintWriter pw = new PrintWriter(file)) {
            for (Interactor interactor : interactors) {
                pw.write(interactor.saveData() + "\n");
            }
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().warning("BlockInteractors could not locate interactors.txt to save interactors");
        }
    }

    private void loadInteractors()
    {
        File file = new File(PLUGIN.getDataFolder(), "interactors.txt");
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                while (line != null) {
                    parseLine(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                debugMessage("Could not load interactors due to error: " + e.getMessage(), 0);
            }
        }
    }

    private void parseLine(String line)
    {
        String[] data = line.split(",");
        World world = Bukkit.getWorld(data[1]);
        if (world != null) {
            int x = Integer.parseInt(data[2]);
            int y = Integer.parseInt(data[3]);
            int z = Integer.parseInt(data[4]);
            Location location = new Location(world, x, y, z);
            if (location.getBlock().getType() == Material.DISPENSER) {
                Dispenser interactor = (Dispenser)location.getBlock().getState();
                switch (data[0]) {
                    case "breaker":
                        interactors.add(new Breaker(interactor));
                        break;
                    case "placer":
                        interactors.add(new Placer(interactor));
                        break;
                    case "miner":
                        interactors.add(new Miner(interactor));
                        break;
                    default:
                        debugMessage("Error in interactors save file at line " + line + ". " + data[0] + " is not a valid save type (valid options are breaker|placer|miner. This is likely the result of a bug or crash)", 1);
                        break;
                }
            } else {
                debugMessage("Interactor was saved in file as " + line + " but there was no dispenser at that location. Disregarding", 1);
            }
        } else {
            debugMessage("Interactor was saved in file as " + line + " but world " + data[1] + " was null", 1);
        }
    }

    private void generateFiles()
    {
        saveResource("config.yml", false);
    }
}