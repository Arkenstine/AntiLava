/*Edited 3.3.23*/
package AntiCast;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.ChatColor;

public final class Broadcast {

    public static final String HEADER = "" +
        ChatColor.RESET + 
        ChatColor.BOLD + 
        ChatColor.DARK_AQUA + "[" +
        ChatColor.AQUA + "SBALC" +
        ChatColor.DARK_AQUA + "]" +
        ChatColor.RESET + ChatColor.WHITE + " ";

    public static final String NORMAL   = ChatColor.GRAY.toString();
    public static final String GOOD     = ChatColor.GREEN.toString();
    public static final String WARNING  = ChatColor.YELLOW.toString();
    public static final String CRITICAL = ChatColor.RED.toString();
    
    private static void broadcast(String msg, boolean admins) {
        if(admins) {
            Bukkit.broadcast(HEADER + msg, Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        } else {
            Bukkit.broadcastMessage(HEADER + msg);
        }
    }

    public static void normal(String msg, boolean admins) {
        broadcast(NORMAL + msg, admins);
    }

    public static void good(String msg, boolean admins) {
        broadcast(GOOD + msg, admins);
    }

    public static void warning(String msg, boolean admins) {
        broadcast(WARNING + msg, admins);
    }

    public static void critical(String msg, boolean admins) {
        broadcast(CRITICAL + msg, admins);
    }
}
