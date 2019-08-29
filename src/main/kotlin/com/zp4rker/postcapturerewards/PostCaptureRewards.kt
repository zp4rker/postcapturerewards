package com.zp4rker.postcapturerewards

import com.zp4rker.postcapturerewards.cmd.MainCommand
import com.zp4rker.postcapturerewards.lstnr.PlayerMoveListener
import com.zp4rker.postcapturerewards.util.YamlFile
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

val permissionError = "${ChatColor.RED}You do not have permission to do that!"

val posts = mutableListOf<Post>()

class PostCaptureRewards : JavaPlugin() {

    companion object {
        lateinit var postsFile: YamlFile
        lateinit var instance: PostCaptureRewards
    }

    override fun onEnable() {
        instance = this
        postsFile = YamlFile(File(dataFolder, "posts.yml"))
        loadPosts()

        registerCommands()
        server.pluginManager.registerEvents(PlayerMoveListener(), this)
        logger.info("Successfully enabled!")
    }

    private fun registerCommands() {
        getCommand("capturepost")?.setExecutor(MainCommand)
    }

    private fun loadPosts() {
        for (postId in postsFile.yaml.getKeys(false)) {
            val post = postsFile.yaml.getConfigurationSection(postId) ?: return

            val location = post.get("location") as Location
            val radius = post.getInt("radius")
            val team = post.getStringList("team").map { Bukkit.getPlayer(UUID.fromString(it))!! }.toMutableList()
            val commands = arrayOf(post.getStringList("commands.win"), post.getStringList("commands.lose"))

            Post(postId, team, location, radius, commands)
        }
    }
}