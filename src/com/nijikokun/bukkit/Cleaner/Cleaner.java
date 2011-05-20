package com.nijikokun.bukkit.Cleaner;

import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Cleaner extends JavaPlugin
{
  public static String name = "Cleaner";
  public static String codename = "Mess";
  public static String version = "1.8";

  protected static final Logger log = Logger.getLogger("Minecraft");

  public Listener l = new Listener(this);

  public static String main_directory = "Cleaner" + File.separator;
  public static HashMap<String, String> items;
  public static iProperty Settings;
  public static iProperty Items;
  public static Misc Misc = new Misc();

  public static Permissions Permissions = null;
  public static boolean debugging;

  public void onDisable()
  {
    log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " (" + codename + ") disabled");
  }

  public void onEnable() {
    setup();
    setupCommands();
    setupPermissions();
    setupItems();
    registerEvents();
    log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " (" + codename + ") loaded");
  }
  private void registerEvents() {
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    try {
      if ((sender instanceof Player)) {
        Player player = (Player)sender;
        String split = "/" + command.getName().toLowerCase();

        for (int i = 0; i < args.length; i++) {
          split = split + " " + args[i];
        }

        this.l.onPlayerCommand(player, split.split(" "));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  public void setup()
  {
    Items = new iProperty("items.db");
    version = getDescription().getVersion();
  }

  public void setupCommands()
  {
  }

  public void setupPermissions()
  {
    Plugin test = getServer().getPluginManager().getPlugin("Permissions");

    if (Permissions == null)
      if (test != null) {
        Permissions = (Permissions)test;
      } else {
        log.info(Messaging.bracketize(name) + " Permission system not enabled. Disabling plugin.");
        getServer().getPluginManager().disablePlugin(this);
      }
  }

  public void setupItems()
  {
    Map<String, String> mappedItems = null;
    items = new HashMap<String, String>();
    try
    {
      mappedItems = Items.returnMap();
    } catch (Exception ex) {
      log.info(Messaging.bracketize(new StringBuilder().append(name).append(" Flatfile").toString()) + " could not grab item list!");
    }
    
    if (mappedItems != null)
      for (String id: mappedItems.keySet()) {
        String itemName = (String)mappedItems.get(id);

        if (debugging) {
          log.info("Item #[" + id + "] loaded as [" + itemName + "]");
        }

        items.put(id, itemName);
      }
  }
}
