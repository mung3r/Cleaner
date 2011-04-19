package com.nijikokun.bukkit.Cleaner;

import java.util.HashMap;
import org.bukkit.entity.Player;

public class Control
{
  public static HashMap<String, String> permissions = new HashMap<String, String>();

  public static void add(String controller, String groups)
  {
    permissions.put(controller, groups);
  }

  public static boolean permission(String controller, Player player) {
    if (!permissions.containsKey(controller)) {
      return false;
    }

    String groups = (String)permissions.get(controller);

    if (!groups.equals("*")) {
      String[] groupies = groups.split(",");

      for (String group : groupies) {
        if (player.getName().equals(group)) {
          return true;
        }
      }

      return false;
    }

    return true;
  }
}
