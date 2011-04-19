package com.nijikokun.bukkit.Cleaner;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Listener extends PlayerListener
{
  public HashMap<String, ItemStack[]> saved = new HashMap<String, ItemStack[]>();
  public static Cleaner plugin;

  public Listener(Cleaner instance)
  {
    plugin = instance;
  }

  private void Cleanse(Player player, boolean force) {
    PlayerInventory inventory = player.getInventory();

    for (int i = 0; i < 40; i++) {
      if (inventory.getItem(i) == null)
        continue;
      if ((i < 9) && (!force)) {
        continue;
      }
      inventory.clear(i);
    }
  }

  public void onPlayerCommand(Player player, String[] split)
  {
    String base = split[0];
    int itemInHand = player.getItemInHand().getTypeId();
    String playerName = player.getName();
    PlayerInventory inventory = player.getInventory();
    Messaging.save(player);

    if (Misc.is(base, "/wipe").booleanValue()) {
      if (!Permissions.Security.permission(player, "cleaner.wipe")) {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
        return;
      }

      boolean force = false;

      if ((Misc.argumentsAre(split, 1).booleanValue()) && 
        (Misc.is(split[1], "-f").booleanValue())) {
        force = true;
      }

      for (Player currently : plugin.getServer().getOnlinePlayers()) {
        Cleanse(currently, force);
      }

      Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Cleaned all player's inventory" + (force ? " and quickbar" : "") + "!"));
      return;
    }

    if (Misc.is(base, "/clean").booleanValue()) {
      if (!Permissions.Security.permission(player, "cleaner.clean")) {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
        return;
      }

      Player current = null;
      boolean force = false;

      if ((Misc.argumentsAre(split, 1).booleanValue()) || (Misc.argumentsAre(split, 2).booleanValue()))
      {
        current = Misc.player(split[1]);

        if (Misc.is(split[1], "-f").booleanValue()) {
          force = true;
        } else {
          if ((Misc.argumentsAre(split, 2).booleanValue()) && 
            (Misc.is(split[2], "-f").booleanValue())) {
            force = true;
          }

          if (current == null) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not online!"));
            return;
          }

          if (!Permissions.Security.permission(player, "cleaner.clean.player")) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
            return;
          }
        }
      }

      if (force) {
        if (current == null) {
          if (!Permissions.Security.permission(player, "cleaner.clean.force")) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
            return;
          }
        }
        else if (!Permissions.Security.permission(player, "cleaner.clean.player.force")) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
          return;
        }

      }

      Cleanse(current == null ? player : current, force);

      if (current == null)
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Inventory" + (force ? " and quickbar" : "") + " cleaned!"));
      else {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Cleaned <white>" + current.getName() + "'s<yellow> inventory" + (force ? " and quickbar" : "") + "!"));
      }

      return;
    }

    if (Misc.is(base, "/preview").booleanValue()) {
      if (!Permissions.Security.permission(player, "cleaner.preview")) {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
        return;
      }

      if (Misc.argumentsAre(split, 1).booleanValue()) {
        Player current = Misc.player(split[1]);

        if (current == null) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not online!"));
          return;
        }

        PlayerInventory currently = current.getInventory();

        if (current.getName().equalsIgnoreCase(playerName)) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you cannot preview your own inventory!"));
          return;
        }

        if (!this.saved.containsKey(player.getName())) {
          this.saved.put(player.getName(), inventory.getContents());
        }

        Cleanse(player, true);

        for (int i = 0; i < 40; i++) {
          ItemStack item = currently.getItem(i);

          if ((item == null) || (item.getTypeId() == 0))
            continue;
          inventory.setItem(i, currently.getItem(i));
        }

        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Now viewing <white>" + current.getName() + "'s<yellow> inventory!"));
      } else {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you must declare a player at least!"));
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: <white>/preview <player>"));
      }

      return;
    }

    if (Misc.is(base, "/revert").booleanValue()) {
      if (!Permissions.Security.permission(player, "cleaner.preview.restore")) {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
        return;
      }

      if (this.saved.containsKey(player.getName()))
      {
        Cleanse(player, true);

        for (ItemStack item : (ItemStack[])this.saved.get(player.getName())) {
          if ((item != null) && (item.getTypeId() != 0)) {
            inventory.setItem(inventory.firstEmpty(), item);
          }
        }
        this.saved.remove(player.getName());

        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Inventory restored!"));
      } else {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, no inventory is stored for you!"));
      }

      return;
    }

    if (Misc.is(base, "/replace").booleanValue()) {
      if (!Permissions.Security.permission(player, "cleaner.replace")) {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
        return;
      }

      ArrayList<String> players = new ArrayList<String>();

      if (Misc.argumentsAre(split, 1).booleanValue()) {
        Player current = Misc.player(split[1]);
        PlayerInventory currently = current.getInventory();

        if (current == null) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not online!"));
          return;
        }

        if (!Permissions.Security.permission(player, "cleaner.replace.player")) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
          return;
        }

        if (current.getName().equalsIgnoreCase(playerName)) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you cannot replace your own inventory!"));
          return;
        }

        for (int i = 0; i < 40; i++) {
          ItemStack item = inventory.getItem(i);

          if (item == null)
          {
            continue;
          }
          currently.remove(i);
          int amount = item.getAmount();
          currently.setItem(i, item);
        }

        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] " + current.getName() + " had their inventory replaced with yours!"));
        return;
      }

      if ((Misc.argumentsAre(split, 2).booleanValue()) || (Misc.argumentsAre(split, 3).booleanValue())) {
        Player current = null;
        int id = 1;
        int with = 1;

        if (Misc.argumentsAre(split, 2).booleanValue()) {
          if (!Permissions.Security.permission(player, "cleaner.replace.items")) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
            return;
          }

          id = Items.validate(split[1]);
          with = Items.validate(split[2]);

          if (id == -1) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not a valid item id!"));
            return;
          }

          if (with == -1) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[2] + "<rose> is not a valid item id!"));
            return;
          }

          for (Player currently : plugin.getServer().getOnlinePlayers()) {
            boolean replaced = false;
            if (currently != null) {
              inventory = currently.getInventory();

              for (int i = 0; i < 40; i++) {
                ItemStack item = inventory.getItem(i);
                if ((item == null) || 
                  (item.getTypeId() != id))
                  continue;
                int amount = item.getAmount();
                inventory.remove(i);
                inventory.setItem(i, new ItemStack(with, amount));
                replaced = true;
              }

              if (replaced) {
                players.add("&f" + currently.getName() + "&f");
              }
            }
          }
          if (players.isEmpty()) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] No players had <white>" + Items.name(id) + "<yellow> to replace!"));
            return;
          }

          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Replaced <white>" + Items.name(id) + "<yellow> with <white>" + Items.name(with) + "<yellow> on players:"));
          Messaging.send(players.toString());
          return;
        }

        if (Misc.argumentsAre(split, 3).booleanValue()) {
          boolean replaced = false;
          current = Misc.player(split[1]);
          id = Items.validate(split[2]);
          with = Items.validate(split[3]);

          if (current == null) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not online!"));
            return;
          }

          if (!Permissions.Security.permission(player, "cleaner.replace.player.items")) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
            return;
          }

          if (id == -1) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not a valid item id!"));
            return;
          }

          if (with == -1) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[2] + "<rose> is not a valid item id!"));
            return;
          }

          inventory = current.getInventory();

          for (int i = 0; i < 40; i++) {
            ItemStack item = inventory.getItem(i);
            if ((item == null) || 
              (item.getTypeId() != id))
              continue;
            int amount = item.getAmount();
            inventory.remove(i);
            inventory.setItem(i, new ItemStack(with, amount));
            replaced = true;
          }

          if (!replaced) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] " + current.getName() + " did not have any <white>" + Items.name(id) + "<yellow> to replace!"));
            return;
          }

          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] " + current.getName() + " had <white>" + Items.name(id) + "<yellow> replaced with <white>" + Items.name(with) + "<yellow>!"));
          return;
        }

      }

      Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Invalid arguments given."));
      Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: /replace [player]."));
      Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: /replace [player] [item] [with]."));
      Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: /replace [item] [with]."));
      return;
    }

    if (Misc.is(base, "/scan").booleanValue()) {
      if (!Permissions.Security.permission(player, "cleaner.scan")) {
        Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
        return;
      }

      ArrayList<String> players = new ArrayList<String>();
      boolean remove = false;
      boolean fully = false;
      boolean verbose = false;
      Player current = null;
      int id = 1;
      int amount = 1;

      if ((Misc.argumentsAre(split, 1).booleanValue()) || (Misc.argumentsAre(split, 2).booleanValue()) || (Misc.argumentsAre(split, 3).booleanValue()) || (Misc.argumentsAre(split, 4).booleanValue())) {
        current = Misc.player(split[1]);

        if (current == null) {
          if (!Permissions.Security.permission(player, "cleaner.scan.items")) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
            return;
          }

          id = Items.validate(split[1]);

          if (id == -1) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[1] + "<rose> is not a valid item id!"));
            return;
          }

          if (Misc.argumentsAre(split, 2).booleanValue()) {
            if (Misc.isEither(split[2], "remove", "-r").booleanValue()) {
              remove = true;
            } else if (Misc.isEither(split[2], "force", "-f").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.remove.force")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              fully = true;
            } else {
              amount = Integer.valueOf(split[2]).intValue();

              if (amount < 1) {
                amount = 1;
              }
            }
          }

          if (Misc.argumentsAre(split, 3).booleanValue())
            if (Misc.isEither(split[3], "remove", "-r").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.remove")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              remove = true;
              remove = false;
            } else if (Misc.isEither(split[3], "force", "-f").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.remove.force")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              fully = true;
              remove = false;
            } else {
              amount = Integer.valueOf(split[3]).intValue();

              if (amount < 1)
                amount = 1;
            }
        }
        else
        {
          if (!Permissions.Security.permission(player, "cleaner.scan.player")) {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
            return;
          }

          if (Misc.argumentsAre(split, 2).booleanValue()) {
            id = Items.validate(split[2]);

            if (id == -1) {
              Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>" + split[2] + "<rose> is not a valid item id!"));
              return;
            }
          } else {
            verbose = true;
          }

          if (Misc.argumentsAre(split, 3).booleanValue()) {
            if (Misc.isEither(split[3], "remove", "-r").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.player.remove")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              remove = true;
            } else if (Misc.isEither(split[3], "force", "-f").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.player.remove.force")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              fully = true;
            } else {
              amount = Integer.valueOf(split[3]).intValue();

              if (amount < 1) {
                amount = 1;
              }
            }
          }

          if (Misc.argumentsAre(split, 4).booleanValue()) {
            if (Misc.isEither(split[4], "remove", "-r").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.player.remove")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              remove = true;
              fully = false;
            } else if (Misc.isEither(split[4], "force", "-f").booleanValue()) {
              if (!Permissions.Security.permission(player, "cleaner.scan.player.remove.force")) {
                Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
                return;
              }

              fully = true;
              remove = false;
            } else {
              amount = Integer.valueOf(split[4]).intValue();

              if (amount < 1) {
                amount = 1;
              }
            }
          }
        }
      }

      if (current == null) {
        if (!Permissions.Security.permission(player, "cleaner.scan.items")) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
          return;
        }

        for (Player currently : plugin.getServer().getOnlinePlayers()) {
          if (currently != null) {
            inventory = currently.getInventory();

            int has = Items.hasAmount(currently, id);

            if (has >= amount) {
              players.add("&f" + currently.getName() + "&f (&c" + has + "&f)");

              if (remove) {
                Items.remove(currently, id, amount);
              }

              if (fully) {
                Items.remove(currently, id, has);
              }
            }
          }
        }
        if (players.isEmpty()) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] No players have <white>" + Items.name(id) + "<yellow> min-amount <white>[<yellow>" + amount + "<white>]!"));
          return;
        }

        if ((!remove) && (!fully))
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Players containing <white>[<yellow>" + amount + "<white>] <white>" + Items.name(id) + "<yellow>:"));
        else if (remove)
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed  <white>[<yellow>" + amount + "<white>] <white>" + Items.name(id) + "<yellow> from players:"));
        else if (fully) {
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed <white>" + Items.name(id) + "<yellow> from players:"));
        }

        Messaging.send(players.toString());
      }
      else
      {
        inventory = current.getInventory();
        if (verbose) {
          ArrayList<Integer> went = new ArrayList<Integer>();

          for (ItemStack item : inventory.getContents())
            if (item != null) {
              id = item.getTypeId();
              if (!went.contains(Integer.valueOf(id))) {
                int has = Items.hasAmount(current, id);

                boolean isWater = (id == 8) || (id == 9);
                boolean isLava = (id == 10) || (id == 11);
                boolean isTNT = id == 46;
                boolean isAdminium = id == 7;

                players.add("&" + ((isTNT) || (isWater) || (isLava) || (isAdminium) ? "c" : "f") + Items.name(id) + "&f (&c" + has + "&f)");
                went.add(Integer.valueOf(id));
              }
            }
          Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Verbose scanning of <white>" + current.getName() + "'s<yellow> inventory:"));

          if (!players.isEmpty())
            Messaging.send(players.toString());
          else
            Messaging.send(Messaging.colorize("Inventory is empty."));
        }
        else {
          int has = Items.hasAmount(current, id);
          if (has >= amount) {
            if (remove) {
              Items.remove(current, id, amount);
            }

            if (fully) {
              Items.remove(current, id, has);
            }

            if ((!remove) && (!fully))
              Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] " + current.getName() + " has <white>" + Items.name(id) + "<yellow> min <white>[<yellow>" + amount + "<white>] total <white>[<yellow>" + has + "<white>]!"));
            else if (remove)
              Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed <white>[<yellow>" + amount + "<white>] <white>" + Items.name(id) + "<yellow> from " + current.getName() + " !"));
            else if (fully)
              Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed <white>" + Items.name(id) + "<yellow> from " + current.getName() + " !"));
          }
          else {
            Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] " + current.getName() + " does not have any <white>" + Items.name(id) + "<yellow>!"));
          }
        }
      }

      return;
    }
  }
}
