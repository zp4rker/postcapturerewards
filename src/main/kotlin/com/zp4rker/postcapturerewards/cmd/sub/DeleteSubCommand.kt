package com.zp4rker.postcapturerewards.cmd.sub

import com.zp4rker.postcapturerewards.Post
import com.zp4rker.postcapturerewards.lstnr.PlayerMoveListener
import com.zp4rker.postcapturerewards.permissionError
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object DeleteSubCommand {
    private const val usage = "/capturepost delete"

    fun handle(sender: CommandSender) {
        // sender player check
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You must be a player to run this command!")
            return
        }
        // permission check
        if (!sender.hasPermission("postcapturerewards.delete")) {
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
        // cancel countdown
        PlayerMoveListener.countdowns.find { it.post == post }?.cancel()
        // delete post
        post.delete()
        // send notification of deletion
        sender.sendMessage("${ChatColor.GREEN}Successfully deleted post!")
    }
}