package net.serubin.hatme;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HatPermsHandler {
    private boolean restrictionEnabled;
    private List<Integer> unrestrictBlocks;
    private HatMe plugin;
    
    public HatPermsHandler(HatMe plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.unrestrictBlocks = config.getIntegerList("plugin.hatme.allowed");
        this.restrictionEnabled = config.getBoolean("plugin.hatme.restrict");
    }
    
    protected boolean checkHatPerms(Player player) {
        if (player.hasPermission("hatme.hat") || 
                player.hasPermission("hatme.hat." + player.getItemInHand().getTypeId()) || player.getItemInHand().getTypeId() == 0)
            return true;
        
        return false;
    }
    
    protected boolean checkHatAllPerms(Player player) {
        if (player.hasPermission("hatme.hat.all") || 
                player.hasPermission("hatme.hat.all." + player.getItemInHand().getTypeId()))
            return true;
        
        return false;
    }
    
    protected boolean checkGivePerms(Player player, String arg) {
        if (player.hasPermission("hatme.hat.give") || 
                player.hasPermission("hatme.hat.give." + arg))
            return true;
        
        return false;
    }

    protected boolean checkRestrict(Player player) {
        int heldItem = player.getItemInHand().getTypeId();
        if(!restrictionEnabled) return true;
        if (player.hasPermission("hatme.norestrict"))
            return true;
        if (unrestrictBlocks.contains(heldItem))
            return true;
        return false;
    }

    boolean checkItemRestrict(String arg, Player player) {
        if(!restrictionEnabled) return true;
        if (player.hasPermission("hatme.norestrict"))
            return true;
        if (unrestrictBlocks.contains(Integer.parseInt(arg)))
            return true;
        return false;
    }
}
