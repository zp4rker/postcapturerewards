package com.zp4rker.postcapturerewards.cmd

import com.zp4rker.postcapturerewards.cmd.sub.CreateSubCommand
import com.zp4rker.postcapturerewards.cmd.sub.DeleteSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil

object MainCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (args[0].equals("create", true)) CreateSubCommand.handle(sender, args)
        if (args[0].equals("delete", true)) DeleteSubCommand.handle(sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()
        val subCommands = mutableListOf("create", "delete")
        StringUtil.copyPartialMatches(args[0], subCommands, completions)
        completions.sort()
        return completions
    }
}