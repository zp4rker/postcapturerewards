package com.zp4rker.postcapturerewards.cmd

import com.zp4rker.postcapturerewards.cmd.sub.CreateSubCommand
import com.zp4rker.postcapturerewards.cmd.sub.DeleteSubCommand
import com.zp4rker.postcapturerewards.cmd.sub.EditSubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil

object MainCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (args[0].equals("create", true)) CreateSubCommand.handle(sender, args.drop(1).toTypedArray())
        if (args[0].equals("delete", true)) DeleteSubCommand.handle(sender)
        if (args[0].equals("edit", true)) EditSubCommand.handle(sender, args.drop(1).toTypedArray())
        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()
        val subCommands = mutableListOf("create", "delete", "edit")

        StringUtil.copyPartialMatches(args[0], subCommands, completions)
        StringUtil.copyPartialMatches(args[1], EditSubCommand.subCommands[0], completions)
        StringUtil.copyPartialMatches(args[2], EditSubCommand.subCommands[1], completions)
        completions.sort()
        return completions
    }
}