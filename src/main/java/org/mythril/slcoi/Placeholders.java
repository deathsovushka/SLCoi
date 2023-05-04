package org.mythril.slcoi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {


    @Override
    public @NotNull String getAuthor() {
        return "SUBLAND";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "slcoi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("cantmove")) {
            if (EventListener.stopList.containsKey(player.getName())) return "true";
        }
        return "false";
    }
}
