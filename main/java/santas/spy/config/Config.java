package santas.spy.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import santas.spy.BlockInteractor;
import santas.spy.helpers.ItemGrabber;

public class Config {
    private static Config instance;
    private int debug;
    private List<ItemStack> breakerItems;
    private List<ItemStack> placerItems;
    private List<ItemStack> minerItems;
    private Map<String, Fuel> fuels;
    private Map<Material, Mineable> mineables;
    private YamlConfiguration info;
    private ItemGrabber grabber;
    private ErrorLogger errors;
    private boolean breakerRequiresFuel;
    private boolean placerRequiresFuel;
    File configFile;

    public static Config getConfig()
    {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config()
    {
        // private constructor for singleton
        grabber = new ItemGrabber();
        errors = new ErrorLogger();
    }

    public int debug()                          { return debug; }
    public List<ItemStack> breakerItems()       { return breakerItems; }
    public List<ItemStack> placerItems()        { return placerItems; }
    public List<ItemStack> minerItems()         { return minerItems; }
    public Map<String, Fuel> fuels()            { return fuels; }
    public Map<Material,Mineable> mineables()   { return mineables; }
    public boolean placerRequiresFuel()         { return placerRequiresFuel; }
    public boolean breakerRequiresFuel()        { return breakerRequiresFuel; }

    public void reload()
    {
        configFile = new File(BlockInteractor.PLUGIN.getDataFolder(), "config.yml");
        info = YamlConfiguration.loadConfiguration(configFile);
        debug = info.getInt("debug-level");
        Bukkit.getLogger().info("[BlockInteractors] Set debug to " + debug);
        breakerRequiresFuel = info.getBoolean("breaker-requires-fuel");
        placerRequiresFuel = info.getBoolean("placer-requires-fuel");
        breakerItems = loadItems("block-breaker-items");
        placerItems = loadItems("block-placer-items");
        minerItems = loadItems("block-miner-items");
        loadFuels();
        loadMineables();
    }

    private List<ItemStack> loadItems(String path)
    {
        List<ItemStack> items = new ArrayList<>();
        List<String> raw = info.getStringList(path);
        for (String s : raw) {
            BlockInteractor.debugMessage("Trying to load " + s, 2);
            ItemStack item = grabber.grab(s);
            if (item == null) {
                BlockInteractor.debugMessage("Error in config. Item " + s + " does not match any item", 0);
                errors.add("Item " + s + " does not match any item", path);
            } else {
                BlockInteractor.debugMessage("Added " + s + " to list " + path, 1);
                items.add(item);
            }
        }
        return items;
    }

    private void loadFuels()
    {
        BlockInteractor.debugMessage("Loading Fuels", 1);
        fuels = new HashMap<>();
        Set<String> fuelNames = info.getConfigurationSection("fuel-sources").getKeys(false);
        if (fuelNames.isEmpty()) { 
            BlockInteractor.debugMessage("No fuel sources were found", 1); 
            errors.add("No fuel sources were found", "fuel-sources");
        }
        for (String s : fuelNames) {
            BlockInteractor.debugMessage("Loading fuel " + s, 2);
            ItemStack type = grabber.grab(s);
            if (type != null) {
                int timer = info.getInt("fuel-sources." + s + ".timer");
                BlockInteractor.debugMessage("Got type " + type.getType() + " for fuel", 2);
                fuels.put(s, new Fuel(type, timer, s));
                BlockInteractor.debugMessage("Added " + s + " to fuels", 1);
            } else {
                BlockInteractor.debugMessage("Error in config. Fuel " + s + "'s type (" + info.getString("fuel-sources." + s + ".type") + ") does not match any item", 0);
                errors.add("Fuel " + s + "'s type (" + info.getString("fuel-sources." + s + ".type") + ") does not match any item", "fuel-sources." + s);
            }
        }
    }

    private void loadMineables()
    {
        BlockInteractor.debugMessage("Loading Mineables", 1);
        mineables = new HashMap<>();
        Set<String> blocks = info.getConfigurationSection("mineable-blocks").getKeys(false);
        if (blocks.isEmpty()) { 
            BlockInteractor.debugMessage("No mineable blocks were found", 1); 
            errors.add("No mineable blocks were found", "mineable-blocks");
        }
        for (String s : blocks) {
            //get block type
            BlockInteractor.debugMessage("Loading item " + s, 2);
            Material type = Material.getMaterial(s.toUpperCase());

            //get fuel types
            List<String> fuelNames = info.getStringList("mineable-blocks." + s + ".fuels");
            List<Fuel> fuelTypes = new ArrayList<>();
            for (String name : fuelNames) {
                Fuel fuel = fuels.get(name);
                if (fuel == null) {
                    BlockInteractor.debugMessage("Error in Config. " + name + " did not match any fuel", 0);
                    errors.add(name + " did not match any fuel", "mineable-blocks." + s + ".fuels");
                } else {
                    fuelTypes.add(fuels.get(name));
                }
            }

            //get result
            String rawResult = info.getString("mineable-blocks." + s + ".gives");
            String[] data = rawResult.split(":");
            ItemStack result = grabber.grab(data[0]);
            if (data.length < 2) {
                result.setAmount(1);
            } else {
                try {
                    result.setAmount(Integer.parseInt(data[1]));
                } catch (NumberFormatException e) {
                    BlockInteractor.debugMessage("Error in config. " + data[1] + " is not a valid number", 0);
                    errors.add(data[1] + " is not a valid number", "mineable-blocks." + s + ".gives");
                    result.setAmount(1);
                }
            }

            //combine into mineable
            mineables.put(type, new Mineable(type, fuelTypes, result));
            BlockInteractor.debugMessage("Added " + s + " to mineables", 2);
        }
    }

    public String getErrors()
    {
        return errors.list();
    }
}
