package org.mythril.slcoi;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventListener  implements Listener {

    public static final HashMap<String, Location> stopList = new HashMap<>();

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent event) {
        String nickname = event.getPlayer().getName();
        if (!stopList.containsKey(nickname)) return;
        Location coords = stopList.get(nickname);
        String oldXYZ = coords.getBlockX() + " " + coords.getBlockY() + " " + coords.getBlockZ();
        String newXYZ = event.getTo().getBlockX() + " " + event.getTo().getBlockY() + " " + event.getTo().getBlockZ();
        if (!oldXYZ.equals(newXYZ)) {
            event.getPlayer().teleport(coords);
        }
    }



}
