package com.nijikokun.bukkit.Cleaner;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Items
{
  public static String name(int id)
  {
    if (Cleaner.items.containsKey(Misc.string(id))) {
      return (String)Cleaner.items.get(Misc.string(id));
    }

    for (Material item : Material.values()) {
      if (item.getId() == id) {
        return item.toString();
      }
    }

    return Misc.string(id);
  }

  public static void setName(String id, String name)
  {
    Cleaner.items.put(id, name);
    Cleaner.Items.setString(id, name);
  }

  public static boolean has(Player player, int itemId, int amount)
  {
    PlayerInventory inventory = player.getInventory();
    ItemStack[] items = inventory.getContents();
    int total = 0;

    for (ItemStack item : items) {
      if ((item != null) && (item.getTypeId() == itemId) && (item.getAmount() > 0)) {
        total += item.getAmount();
      }
    }

    return total >= amount;
  }

  public static int hasAmount(Player player)
  {
    PlayerInventory inventory = player.getInventory();
    ItemStack[] items = inventory.getContents();
    int amount = 0;

    for (ItemStack item : items) {
      if (item != null) {
        amount += item.getAmount();
      }
    }

    return amount;
  }

  public static int hasAmount(Player player, int itemId)
  {
    PlayerInventory inventory = player.getInventory();
    ItemStack[] items = inventory.getContents();
    int amount = 0;

    for (ItemStack item : items) {
      if ((item != null) && (item.getTypeId() == itemId)) {
        amount += item.getAmount();
      }
    }

    return amount;
  }

  public static void remove(Player player, int item, int amount)
  {
    PlayerInventory inventory = player.getInventory();
    ItemStack[] items = inventory.getContents();
    int counter = amount;
    int leftover = 0;

    for (int i = 0; i < 120; i++) {
      ItemStack current = inventory.getItem(i);

      if ((current == null) || (current.getAmount() <= 0))
      {
        continue;
      }
      if (current.getTypeId() != item)
      {
        continue;
      }
      if (current.getAmount() > counter) {
        leftover = current.getAmount() - counter;
      }

      if (leftover != 0) {
        inventory.remove(i);

        if (inventory.firstEmpty() == -1)
          player.getWorld().dropItem(player.getLocation(), new ItemStack(item, leftover));
        else {
          inventory.setItem(inventory.firstEmpty(), new ItemStack(item, leftover));
        }

        counter = 0;
        break;
      }
      counter -= current.getAmount();
      inventory.remove(i);
    }
  }

  public static int validate(String item)
  {
    int itemId = -1;
    try
    {
      itemId = Integer.valueOf(item).intValue();
    } catch (NumberFormatException e) {
      for (String id : Cleaner.items.keySet()) {
        if (((String)Cleaner.items.get(id)).equalsIgnoreCase(item)) {
          if (id.contains(","))
            itemId = Integer.valueOf(id.split(",")[0]).intValue();
          else {
            itemId = Integer.valueOf(id).intValue();
          }
        }
      }

      if (itemId == -1) {
        return -1;
      }
    }

    if (!checkID(itemId)) {
      return -1;
    }
    return itemId;
  }

  public static int validateGrabType(String item)
  {
    int itemId = -1;
    int itemType = -1;
    try
    {
      itemId = Integer.valueOf(item).intValue();
    } catch (NumberFormatException e) {
      for (String id : Cleaner.items.keySet()) {
        if ((((String)Cleaner.items.get(id)).equalsIgnoreCase(item)) && 
          (id.contains(","))) {
          itemId = Integer.valueOf(id.split(",")[0]).intValue();
          itemType = Integer.valueOf(id.split(",")[1]).intValue();
        }

      }

      if (itemId == -1) {
        return -1;
      }
    }

    if (!checkID(itemId))
      return -1;
    if (!validateType(itemId, itemType)) {
      return -1;
    }
    return itemType;
  }

  public static boolean validateType(int id, int type)
  {
    if (type == -1) {
      return true;
    }

    int itemId = -1;

    if (((id == 35) || (id == 351) || (id == 63)) && 
      (type >= 0) && (type <= 15)) {
      return true;
    }

    if ((id == 17) && 
      (type >= 0) && (type <= 2)) {
      return true;
    }

    if (((id == 91) || (id == 86) || (id == 67) || (id == 53) || (id == 77) || (id == 71) || (id == 64)) && 
      (type >= 0) && (type <= 3)) {
      return true;
    }

    if ((id == 66) && 
      (type >= 0) && (type <= 9)) {
      return true;
    }

    return (id == 68) && 
      (type >= 2) && (type <= 5);
  }

  public static boolean checkID(int id)
  {
    for (Material item : Material.values()) {
      if (item.getId() == id) {
        return true;
      }
    }

    return false;
  }
}
