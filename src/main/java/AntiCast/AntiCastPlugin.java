package AntiCast;

import java.util.LinkedList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
/*
 * AntiCast java plugin
 * updated by Lyla
 * @author Sam Belliveau (sam.belliveau@gmail.com)
 */
public class AntiCastPlugin extends JavaPlugin implements Listener  {
    FileConfiguration config = getConfig();

    public void onEnable() {
        
        config.addDefault("bounds.x.min", -500);
        config.addDefault("bounds.x.max", 500);
        config.addDefault("bounds.y.min", -500);
        config.addDefault("bounds.y.max", 500);

        config.addDefault("casts.sampletime", 15.0); 
        config.addDefault("casts.max.warn", 16);
        config.addDefault("casts.max.stop", 24);       

        config.addDefault("notifs.useAdminChannel", true);

        config.options().copyDefaults(true);
        saveConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
    }

    /*********************/
    /*** LISTENER PART ***/
    /*********************/

    // Booleans used to make sure console doesnt get spammed with messages
    private boolean cancelled = false;
    private int lastX = -1, lastY = -1;
    private LinkedList<Long> casts = new LinkedList<>();

    @EventHandler
    public void onCobbleFormation(BlockFormEvent e) {
        // The current time of this call
        long now = System.currentTimeMillis();

        // Get Config Values
        final int MIN_X = config.getInt("bounds.x.min");
        final int MAX_X = config.getInt("bounds.x.max");
        final int MIN_Y = config.getInt("bounds.y.min");
        final int MAX_Y = config.getInt("bounds.y.max");

        // Get the information of the Block
        Block block = e.getBlock();
        Material type = block.getType();
        int x = block.getX();
        int y = block.getY();

        // Check if x and y coords are valid
        boolean xvalid = MIN_X <= x && x <= MAX_Y;
        boolean yvalid = MIN_Y <= y && y <= MAX_Y;

        if (xvalid && yvalid && (type == Material.LAVA || type == Material.WATER)) {
            // Add time of cast to lists
            casts.addLast(now);
            lastX = x; 
            lastY = y;

            refresh();

            if(cancelled) {
                // Cancel the formation of cobble or any other block
                e.setCancelled(true);
                block.setType(Material.AIR);
            }
        }
    }

    // Check things like old casts, and remove an
    public void refresh() {
        // The current time of this call
        long now = System.currentTimeMillis();

        // Get Config Values
        final int SAMPLE_TIME = (int)(config.getDouble("casts.sampletime") * 1000);
        final int MAX_WARN = config.getInt("casts.max.warn");
        final int MAX_STOP = config.getInt("casts.max.stop");
        final boolean USE_ADMIN = config.getBoolean("notifs.useAdminChannel");

        // Ignore any casts that are too old
        while(0 < casts.size() && SAMPLE_TIME < (now - casts.getFirst())) {
            casts.removeFirst();
        }

        // Get number of casts in sample time
        int size = casts.size();

        // Check to see how many casts have happened
        if(size > MAX_STOP) {
            cancelled = true;

        } else if (size > MAX_WARN) {
            // Only write warning if the cast hasnt been cancelled
            if(!cancelled) {
                long age = (now - casts.getFirst()) / 1000;
                String msg = String.format("%d casts at spawn in past %d secs [%d, %d]", size, age, lastX, lastY);
                Broadcast.warning(msg, USE_ADMIN);
            } 
        } else {
            // Uncancel once the value gets low enough
            if(cancelled) {
                Broadcast.good("Enabled Lava & Water Interactions At Spawn!", false);
                cancelled = false;
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sbalc")) {
            refresh();

            final int CASTS  = casts.size();
            final double SAMPLE_TIME = config.getDouble("casts.sampletime");

            sender.sendMessage(Broadcast.HEADER + Broadcast.GOOD + "SBALC v1.2:");
            sender.sendMessage(Broadcast.HEADER + Broadcast.NORMAL + "    - Current Interactions: ");

            if(cancelled) {
                sender.sendMessage(Broadcast.HEADER + Broadcast.NORMAL + "        - Lava & Water: Disabled");
            } else {
                sender.sendMessage(Broadcast.HEADER + Broadcast.NORMAL + "        - Lava & Water: Enabled");
            }

            sender.sendMessage(Broadcast.HEADER + Broadcast.NORMAL + "        - " + CASTS + " casts over " + SAMPLE_TIME + "s");
            sender.sendMessage(Broadcast.HEADER + Broadcast.NORMAL + "        - Coords of last cast: [" + lastX + ", " + lastY + "]");

            return true;
        }

        return false;
    }
}
