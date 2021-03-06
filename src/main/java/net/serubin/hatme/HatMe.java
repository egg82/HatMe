package net.serubin.hatme;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HatMe extends JavaPlugin {

    private static HatMe plugin;
    static Logger log = Logger.getLogger("Minecraft");
    private String name;
    private String version;

    /*
     * Chat Messages
     */
    private final String hatOn = "You now have a hat! Use /unhat to remove it.";
    private final String hatOff = "You have taken off your hat!";
    private final String noSpace = "You have no space to take of your hat!";
    private final String hatAlreadyOn = "You already have a hat on! Take it off with /unhat.";
    private final String airHead = "You have just tried to put air on your head. Good job.";

    private String notAllowedMsg;
    private HatPermsHandler permsHandler;
    private HatExecutor executor;
    private boolean debug = true;

    @Override
    public void onEnable() {
        version = this.getDescription().getVersion();
        name = this.getDescription().getName();
        info("Start Plugin... (Version: " + version + ")");
        PluginManager pm = getServer().getPluginManager();

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.notAllowedMsg = getConfig()
                .getString("plugin.hatme.notAllowedMsg");
        this.permsHandler = new HatPermsHandler(this, getConfig());
        this.executor = new HatExecutor(this);
        getCommand("hat").setExecutor(this);
        getCommand("unhat").setExecutor(this);
        info("Plugin Loaded!");
    }

    @Override
    public void onDisable() {
        info("Plugin Stopping...");
        reloadConfig();
        saveConfig();
        info("Plugin Stopped.");
    }

    /*
     * Hat Messages
     */
    public String hatOnMessage() {
        return hatOn;
    }

    public String hatOffMessage() {
        return hatOff;
    }

    public String noSpaceMessage() {
        return noSpace;
    }

    public String hatAlreadyOnMessage() {
        return hatAlreadyOn;
    }
    public String airHeadMessage(){
        return airHead;
    }

    public void debug(String text) {
        if (debug)
            log.info("[" + name + "] Debug - " + text);
    }

    public void info(String text) {
        log.info("[" + name + "] " + text);
    }

    public void sendMessage(Player player, ChatColor color, String text) {
        player.sendMessage(color + "[" + name + "] " + text);
    }

    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("hat")) {
            // No arguments? Standard operation.
            if (args.length == 0 && (sender instanceof Player)) {
                Player player = (Player) sender;
                if (!permsHandler.checkHatPerms(player)) {
                    sender.sendMessage(ChatColor.RED
                            + "[HatMe] You do not have permission to use this command.");
                    return true;
                }

                if (!permsHandler.checkRestrict(player)) {
                    sender.sendMessage(ChatColor.RED + "[HatMe] "
                            + notAllowedMsg);
                    return true;
                }

                // if item is allowed, do hat
                if (!executor.hatOn(player))
                    return false;
                return true;

            } else if (args.length == 1 && (sender instanceof Player)) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("-a")) {

                    if (!permsHandler.checkHatAllPerms(player)) {
                        sender.sendMessage(ChatColor.RED
                                + "[HatMe] You do not have permission to use this command.");
                        return true;
                    }

                    if (player.getItemInHand().getTypeId() == 0) {
                        player.sendMessage(ChatColor.YELLOW
                                + "[HatMe] You just tried to put air on your head. Good job.");
                        return true;
                    }

                    if (!permsHandler.checkRestrict(player)) {
                        sender.sendMessage(ChatColor.RED + "[HatMe] "
                                + notAllowedMsg);
                        return true;
                    }

                    if (!executor.hatOnAll(player))
                        return false;
                    return true;

                } else if (checkArgInt(args[0])) {
                    if (!permsHandler.checkGivePerms(player, args[0])) {
                        sender.sendMessage(ChatColor.RED
                                + "[HatMe] You do not have permission to use this command.");
                        return true;
                    }

                    if (!permsHandler.checkItemRestrict(args[0], player)) {
                        sender.sendMessage(ChatColor.RED + "[HatMe] "
                                + notAllowedMsg);
                        return true;
                    }

                    if (!executor.giveHat(player, Integer.parseInt(args[0])))
                        return false;
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED
                        + "[HatMe] You have used this command incorrectly, or you are the console.");
                return true;
            }
        }

        if (commandLabel.equalsIgnoreCase("unhat")) {
            // if command is unhat do unhat
            if (args.length >= 1 || !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED
                        + "[HatMe] This command does not take any arguments. If you are the console, you can't use this.");
                return true;
            } else {
                Player player = (Player) sender;
                // check is hat is 0
                if (player.getInventory().getHelmet() == null) {
                    player.sendMessage(ChatColor.YELLOW
                            + "[HatMe] You have taken the air from around your head.");
                    return true;
                } else {
                    executor.hatOff(player);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkArgInt(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            // Item was not an int, do nothing
            return false;
        }
        return true;
    }
}