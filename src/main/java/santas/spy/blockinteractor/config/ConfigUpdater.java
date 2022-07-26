package santas.spy.blockinteractor.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import santas.spy.blockinteractor.BlockInteractor;

public class ConfigUpdater 
{   
    private static final int configVersion = 1;

    public void update() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(BlockInteractor.PLUGIN.getDataFolder(), "config.yml"));

        if (config.getInt("config-version") < configVersion) {
            BlockInteractor.debugMessage("Got old version: " + config.getInt("config-version"), 2);
            depreciateConfig();
            YamlConfiguration newConfig = newConfig(config);
            try {
                BlockInteractor.debugMessage("Saving new config", 2);
                newConfig.save(new File(BlockInteractor.PLUGIN.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                BlockInteractor.PLUGIN.getLogger().severe("Could not save new config due to error: " + e.getMessage());
            }
        }
    }

    private void depreciateConfig()
    {
        BlockInteractor.debugMessage("depreciating old config", 2);
        File file = new File(BlockInteractor.PLUGIN.getDataFolder(), "config.yml");
        file.renameTo(new File(BlockInteractor.PLUGIN.getDataFolder(), "config.yml.old"));
    }

    private YamlConfiguration newConfig(YamlConfiguration oldConfig)
    {
        YamlConfiguration newConfig = oldConfig;
        if (newConfig.getConfigurationSection("config-version") == null) {
            newConfig.createSection("config-version");
            List<String> comments = new ArrayList<>();
            comments.add("IMPORTANT!! Do not change this. Could cause config file corruption");
            newConfig.setComments("config-version", comments);
        }
        newConfig.set("config-version", 1);

        if (newConfig.getConfigurationSection("fill-empty-breaker") == null) {
            newConfig.createSection("fill-empty-breaker");
            newConfig.set("fill-empty-breaker", true);
            List<String> comments = new ArrayList<>();
            comments.add("Breakers require at least one item inside them to work");
            comments.add("Should empty dispensers have an item added to them when they become a breaker");
            newConfig.setComments("fill-empty-breaker", comments);
        }

        return newConfig;
    }
}
