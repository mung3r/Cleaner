package com.nijikokun.bukkit.Cleaner;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Misc
{
  public static Boolean arguments(String[] array, int amount)
  {
    return Boolean.valueOf(array.length < amount + 2);
  }

  public static Boolean argumentsAre(String[] array, int amount)
  {
    return Boolean.valueOf(array.length == amount + 1);
  }

  public static Boolean is(String text, String against)
  {
    return Boolean.valueOf(text.equalsIgnoreCase(against));
  }

  public static Boolean isEither(String text, String against, String or)
  {
    return Boolean.valueOf((text.equalsIgnoreCase(against)) || (text.equalsIgnoreCase(or)));
  }

  public static String formatCurrency(int Balance, String currency)
  {
    return insertCommas(String.valueOf(Balance)) + " " + currency;
  }

  public static String insertCommas(String str)
  {
    if (str.length() < 4) {
      return str;
    }

    return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());
  }

  public static String string(int i)
  {
    return String.valueOf(i);
  }

  public static boolean validate(String name) {
    return name.matches("([0-9a-zA-Z._-]+)");
  }

  public static String repeat(char c, int i) {
    String tst = "";
    for (int j = 0; j < i; j++) {
      tst = tst + c;
    }
    return tst;
  }

  public static Player player(String name)
  {
    if (Listener.plugin.getServer().getOnlinePlayers().length < 1) {
      return null;
    }

    Player[] online = Listener.plugin.getServer().getOnlinePlayers();
    Player player = null;

    for (Player needle : online) {
      if (needle.getName().equalsIgnoreCase(name) || needle.getName().toLowerCase().startsWith(name)) {
        player = needle;
        break;
      }
    }

    return player;
  }

  public static Player playerMatch(String name)
  {
    if (Listener.plugin.getServer().getOnlinePlayers().length < 1) {
      return null;
    }

    Player[] online = Listener.plugin.getServer().getOnlinePlayers();
    Player lastPlayer = null;
    name = name.toLowerCase();

    for (Player player : online) {
      String playerName = player.getName();

      if (playerName.toLowerCase().equals(name)) {
        lastPlayer = player;
        break;
      }

      if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
        if (lastPlayer != null) {
          return null;
        }

        lastPlayer = player;
      }
    }

    return lastPlayer;
  }
}
