package com.zp4rker.postcapturerewards.cmd.sub

import com.zp4rker.postcapturerewards.Post
import com.zp4rker.postcapturerewards.permissionError
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CreateSubCommand {
    private const val usage = "/capturepost create <radius>"

    fun handle(sender: CommandSender, args: Array<out String>) {
        // sender player check
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You must be a player to run this command!")
            return
        }
        // permission check
        if (!sender.hasPermission("postcapturerewards.create")) {
            sender.sendMessage(permissionError)
            return
        }
        // arg exists check
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Invalid arguments! You need to specify a radius. Usage: $usage")
            return
        }
        // arg format check
        val radius = args[0].toIntOrNull() ?: run {
            sender.sendMessage("${ChatColor.RED}Please use a valid number. Usage: $usage")
            return
        }
        // fetch location
        val location = sender.location
        // create post
        val post = Post(location, radius)
        // save to file
        post.saveToFile()
        // send notification of creation
        sender.sendMessage("${ChatColor.GREEN}Post successfully created!")
        // instruct to add commands
        sender.sendMessage("${ChatColor.YELLOW}To finish setting up the post, add the win and lose commands. Usage: /capturepost edit (win_commands|lose_commands) add <command>")
    }
}