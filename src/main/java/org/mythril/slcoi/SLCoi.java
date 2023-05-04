package org.mythril.slcoi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.coreprotect.event.CoreProtectPreLogEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.jetbrains.annotations.NotNull;

import javax.print.attribute.DateTimeSyntax;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public final class SLCoi extends JavaPlugin implements Listener {

    public static List<String> base = new ArrayList<>();
    public static HashMap<String, Integer> ids = new HashMap<>();
    public static Plugin plugin;

    @Override
    public void onEnable() {
        load();
        plugin = this;
        saveDefaultConfig();
        Objects.requireNonNull(this.getCommand("fingerbase")).setExecutor(new CommandExe());
        Bukkit.getPluginManager().registerEvents(this, this);
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.SYSTEM_CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                for (String string: ids.keySet()
                     ) {
                    if(event.getPacket().getStrings().read(0) == null) return;
                    if (event.getPacket().getStrings().read(0).contains("\"text\":\"§k" + ids.get(string))){
                        String strin;
                        if(base.contains(string) || event.getPlayer().hasPermission("base.bypass")){
                            strin = event.getPacket().getStrings().read(0).replace("\"text\":\"§k" + ids.get(string), "\"text\":\"" + string);
                        } else {
                            strin = event.getPacket().getStrings().read(0).replace("\"text\":\"§k" + ids.get(string), "\"text\":\"Неизвестный");
                        }
                        event.getPacket().getStrings().write(0, strin);
                    }
                }
            }
        });
    }
    private ProtocolManager protocolManager;

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
    public void detect(CoreProtectPreLogEvent event){
        if(!(ids.containsKey(event.getUser()))){
            ids.put(event.getUser(), random());
        }
        event.setUser("§k" + ids.get(event.getUser()));
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

    public Integer random(){
        Random random = new Random();
        int id = random.nextInt(10000, 100000);
        if(!(ids.containsValue(id))){
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
}
