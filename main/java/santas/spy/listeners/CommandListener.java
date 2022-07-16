package santas.spy.listeners;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import santas.spy.BlockInteractor;
import santas.spy.config.Config;
import santas.spy.newblocks.Interactor;

public class CommandListener implements CommandExecutor
{
    private Config config = Config.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
        boolean success = true;
        if (args.length == 0) {
            help(sender);
        } else {
            switch (args[0]) {
                case "reload":
                    if (checkPerms("blockinteractor.reload", sender)) {
                        sender.sendMessage("reloading config");
                        config.reload(); 
                    } else {
                        success = false;
                    }
                    break;
                case "list":
                    if (checkPerms("blockinteractor.list", sender)) {
                        if (BlockInteractor.PLUGIN.getInteractors().size() == 0) {
                            sender.sendMessage("There are no interactors to list");
                        } else {
                            for (Interactor interactor : BlockInteractor.PLUGIN.getInteractors()) {
                                Location location = interactor.getLocation();
                                sender.sendMessage(interactor.getClass().getSimpleName() + " @ " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " in world '" + location.getWorld().getName() + "'");
                            }
                        }
                    } else {
                        success = false;
                    }
                    break;
                case "configdebug":
                    if (checkPerms("blockinteractor.configdebug", sender)) {
                        sender.sendMessage(config.getErrors());
                    } else {
                        success = false;
                    }
                    break;
                case "help":
                    help(sender);
                    break;
                default:
                    sender.sendMessage("Unknown Command");
                    break;
            }
        }
        return success;
    }

    private void help(CommandSender sender) {
        sender.sendMessage("/blockinteractors reload: reloads config");
        sender.sendMessage("/blockinteractors list: lists the location of all interactors");
        sender.sendMessage("/blockinteractors configdebug: lists the errors found in the config file");
        sender.sendMessage("/blockinteractors help: show this list");
    }

    private boolean checkPerms(String perm, CommandSender sender)
    {
        boolean hasPerm = true;
        if (sender instanceof Player) {
            if (!((Player)sender).hasPermission(perm)) {
                hasPerm = false;
            }
        }

        return hasPerm;
    }
}
