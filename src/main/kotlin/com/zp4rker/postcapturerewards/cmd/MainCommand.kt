package com.zp4rker.postcapturerewards.cmd

import com.zp4rker.postcapturerewards.cmd.sub.CreateSubCommand
import com.zp4rker.postcapturerewards.cmd.sub.DeleteSubCommand
import com.zp4rker.postcapturerewards.cmd.sub.EditSubCommand
import org.bukkit.command.*
import org.bukkit.util.StringUtil

object MainCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        // create sub command
        if (args[0].equals("create", true)) CreateSubCommand.handle(sender, args.drop(1).toTypedArray())
        // delete sub command
        if (args[0].equals("delete", true)) DeleteSubCommand.handle(sender)
        // edit sub command
        if (args[0].equals("edit", true)) EditSubCommand.handle(sender, args.drop(1).toTypedArray())
        // perm and arg errors handled manually
        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()
        // return if no arguments or sender is console
        if (args.isEmpty() || sender is ConsoleCommandSender) return completions
        // main sub commands
        val subCommands = listOf("create", "delete", "edit")
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], subCommands, completions) // create, delete, edit
        }
        // edit sub command arguments
        if (args[0].toLowerCase() == "edit") {
            // first argument
            if (args.size == 2) StringUtil.copyPartialMatches(args[1], EditSubCommand.subCommands[0], completions)
            // second argument
            if (args.size == 3 && EditSubCommand.subCommands[0].contains(args[1].toLowerCase())) StringUtil.copyPartialMatches(args[2], EditSubCommand.subCommands[1], completions)
        }
        // sort and return completions
        return completions.apply { sort() }
    }
}