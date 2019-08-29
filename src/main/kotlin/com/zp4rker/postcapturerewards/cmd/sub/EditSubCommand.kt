package com.zp4rker.postcapturerewards.cmd.sub

import com.zp4rker.postcapturerewards.Post
import com.zp4rker.postcapturerewards.permissionError
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EditSubCommand {
    private const val usage = "/capturepost edit (win_commands|lose_commands) (add|remove|reset) [command]"
    val subCommands = arrayOf(listOf("win_commands", "lose_commands"), listOf("add", "remove", "reset"))

    fun handle(sender: CommandSender, args: Array<out String>) {
        // sender player check
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You must be a player to run this command!")
            return
        }
        // permission check
        if (!sender.hasPermission("postcapturerewards.edit")) {
            sender.sendMessage(permissionError)
            return
        }
        // fetch post at location
        val post = Post.atLocation(sender.location)
        // post exists check
        if (post == null) {
            sender.sendMessage("${ChatColor.RED}There are no posts at your current location. Please run this command while at a post.")
            return
        }
        // args minimum check
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Invalid arguments! You didn't provide enough arguments. Usage: $usage")
            return
        }
        // arg[0] check
        if (!arrayOf("win_commands", "lose_commands").contains(args[0].toLowerCase())) {
            sender.sendMessage("${ChatColor.RED}Invalid arguments! You didn't provide a valid command set. Usage: $usage")
            return
        }
        // get command set
        val set = if (args[0].equals("win_commands", true)) 0 else 1
        // check operation
        when (val op = args[1].toLowerCase()) {
            // reset operation
            "reset" -> {
                // clear commands
                post.commands[set].clear()
                // send notification of reset
                sender.sendMessage("${ChatColor.GREEN}Successfully reset ${args[0]}!")
            }
            // add or remove operation
            "add", "remove" -> {
                // arg exists check
                if (args.size < 3) {
                    sender.sendMessage("${ChatColor.RED}Invalid arguments! You didn't provide a command. Usage: $usage")
                    return
                }
                // get command
                val command = args.drop(2).joinToString(" ")
                // add/remove command
                if (op == "add") post.commands[set].add(command) else post.commands[set].remove(command)
                // send notification of add/remove
                if (op == "add") sender.sendMessage("${ChatColor.GREEN}Successfully added command to ${args[0]}!")
                else sender.sendMessage("${ChatColor.GREEN}Successuflly removed command from ${args[0]}!")
            }
            // invalid option
            else -> {
                sender.sendMessage("${ChatColor.RED}Invalid arguments! You didn't provide a valid operation. Usage: $usage")
                return
            }
        }
        // save post to file
        post.saveToFile()
    }
}