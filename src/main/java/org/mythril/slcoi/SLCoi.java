package org.mythril.slcoi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.event.CoreProtectPreLogEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class SLCoi extends JavaPlugin implements Listener {

    public static List<String> base = new ArrayList<>();
    public static HashMap<String, Integer> ids = new HashMap<>();
    public static Plugin plugin;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        load();
        plugin = this;
        saveDefaultConfig();
        Objects.requireNonNull(this.getCommand("fingerbase")).setExecutor(new CommandExe());
        Objects.requireNonNull(this.getCommand("stopmove")).setExecutor(new StopMoveCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders().register();
        }
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.SYSTEM_CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                for (String string : ids.keySet()
                ) {
                    if (event.getPacket().getStrings().read(0) == null) return;
                    if (event.getPacket().getStrings().read(0).contains("\"text\":\"§k" + ids.get(string))) {
                        String strin;
                        if (base.contains(string) || event.getPlayer().hasPermission("base.bypass") || event.getPacket().getStrings().read(0).contains("enchanted_book") || event.getPacket().getStrings().read(0).contains("spawn_egg")){
                            strin = event.getPacket().getStrings().read(0).replace("\"text\":\"§k" + ids.get(string), "\"text\":\"" + string + "!");
                        } else {
                            strin = event.getPacket().getStrings().read(0).replace("\"text\":\"§k" + ids.get(string), "\"text\":\"Неизвестный");
                        }
                        event.getPacket().getStrings().write(0, strin);
                    }
                }
            }
        });
    }

    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
        save();
        plugin = null;
        // Plugin shutdown logic
    }

    @EventHandler
    public void detect(CoreProtectPreLogEvent event) {
        if (!(ids.containsKey(event.getUser()))) {
            ids.put(event.getUser(), random());
        }
        final Boolean[] contains = {false};
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(plugin, () -> {
        if(Bukkit.getPlayer(event.getUser()) != null) {
            Player player = Bukkit.getPlayer(event.getUser());
            if (player == null) {
                return;
            }
            for (Block block : getNearbyBlocks(player, 16)
            ) {
                if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
                    if (((Skull) block.getState()).getPlayerProfile() != null) {
                        for (ProfileProperty profileProperty : ((Skull) block.getState()).getPlayerProfile().getProperties()
                        ) {
                            if (profileProperty.getName().equals("textures")) {
                                if (profileProperty.getValue().equals("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgwNWQ1NWYyMWI0OWEwNzRjZDVlM2RjMjQ0YTVhMDcwZTU1NDRiNTRmYTkyNTRkMmRjMmUxOGYxZTY4MDJmOSJ9fX0=")) {
                                    contains[0] = true;
                                    event.setUser(event.getUser() + " (Камера)");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        });
        if(!contains[0]) {
            event.setUser("§k" + ids.get(event.getUser()));
        }
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 9) {
            return null;
        }

        return CoreProtect;
    }

    public Integer random() {
        Random random = new Random();
        int id = random.nextInt(10000, 100000);
        if (!(ids.containsValue(id))) {
            return id;
        }
        return random();
    }

    private void load() {
        File file = new File(getDataFolder(), "data.txt");
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("base:")) {
                        String[] values = line.split(":");
                        if (values.length == 2) {
                            String[] items = values[1].split(",");
                            base.addAll(Arrays.asList(items));
                        }
                    } else if (line.startsWith("ids:")) {
                        String[] values = line.split(":");
                        if (values.length == 2) {
                            String[] items = values[1].split(",");
                            for (String item : items) {
                                String[] keyValue = item.split("=");
                                if (keyValue.length == 2) {
                                    ids.put(keyValue[0], Integer.parseInt(keyValue[1]));
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
                getLogger().info("Data loaded.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void save() {
        File file = new File(getDataFolder(), "data.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("base:" + String.join(",", base));
            bufferedWriter.newLine();
            bufferedWriter.write("ids:" + ids.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(",")));
            bufferedWriter.newLine();
            bufferedWriter.close();
            getLogger().info("Data saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Block[] getNearbyBlocks(Player player, int radius) {
        // Получаем текущую локацию игрока
        Location playerLocation = player.getLocation();

        // Вычисляем границы области поиска
        int minX = playerLocation.getBlockX() - radius;
        int minY = playerLocation.getBlockY() - radius;
        int minZ = playerLocation.getBlockZ() - radius;
        int maxX = playerLocation.getBlockX() + radius;
        int maxY = playerLocation.getBlockY() + radius;
        int maxZ = playerLocation.getBlockZ() + radius;

        // Создаем массив для хранения найденных блоков
        int size = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        Block[] nearbyBlocks = new Block[size];

        int index = 0;
        // Проходим по всем блокам в области поиска и добавляем их в массив
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    nearbyBlocks[index++] = block;
                }
            }
        }

        return nearbyBlocks;
    }
}