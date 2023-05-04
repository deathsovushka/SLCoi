package org.mythril.slcoi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StopMoveCommand implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!sender.hasPermission("fingerbase.use")) {
            sender.sendMessage("§4Недостаточно прав!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§4Укажите ник и длительность замирания игрока в секундах!");
            return true;
        }

        String nickname = args[0];
        int seconds;

        try {
            seconds = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§4Укажите время замирания игрока в секундах!");
            return true;
        }
        Player player = Bukkit.getPlayer(nickname);
        if (player == null) {
            sender.sendMessage("§4Игрок не найден!");
            return true;
        }

        Location coords = player.getLocation().clone();

        EventListener.stopList.put(nickname, coords);
        sender.sendMessage("§2Игрок " + nickname + " замер на " + seconds + " секунд!");

        Bukkit.getScheduler().runTaskLater(SLCoi.plugin, () -> {
            if (!EventListener.stopList.containsKey(nickname)) return;
            EventListener.stopList.remove(nickname);
        }, 20L * seconds);

        return true;
    }
}
