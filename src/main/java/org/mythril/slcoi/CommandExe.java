package org.mythril.slcoi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import static org.mythril.slcoi.SLCoi.base;
import static org.mythril.slcoi.SLCoi.plugin;

public class CommandExe implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender.hasPermission("*") || commandSender instanceof ConsoleCommandSender){
            if(strings.length == 2){
                if(strings[0].equals("add")){
                    if(SLCoi.base.size() >= plugin.getConfig().getInt("basesise", 20)){
                        base.remove(19);
                    }
                    if(base.contains(strings[1])){
                        return true;
                    }
                    base.add(strings[1]);
                    commandSender.sendMessage("Отпечатки внесены в базу данных!");
                }
                if(strings[0].equals("remove")){
                    base.remove(strings[1]);
                }
                if(strings[0].equals("clear")){
                    base.clear();
                }
                if(strings[0].equals("list")){
                    commandSender.sendMessage(base.toString());
                }
            } else {
                commandSender.sendMessage("Нужно два аргумента для выполнения команды!");
            }
        }
        return true;
    }
}