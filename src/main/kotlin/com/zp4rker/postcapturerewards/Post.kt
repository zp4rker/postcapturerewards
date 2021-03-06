package com.zp4rker.postcapturerewards

import com.zp4rker.postcapturerewards.util.sendTitle
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.pow

class Post(val location: Location, private val radius: Int, val commands: Array<MutableList<String>> = arrayOf(mutableListOf(), mutableListOf())) {

    private var id = UUID.randomUUID().toString()
    private var team = listOf<String>()
    val teamPlayers: List<Player> get() = team.map { Bukkit.getPlayer(UUID.fromString(it))!! }

    constructor(id: String, team: List<String>, location: Location, radius: Int, commands: Array<MutableList<String>>) : this(location, radius, commands) {
        this.id = id
        this.team = team
    }

    init {
        posts.add(this)
    }

    fun inRegion(location: Location) = this.location.distanceSquared(location) <= radius.toDouble().pow(2.0)

    fun playersInRegion() = location.world!!.getNearbyEntities(location, radius.toDouble(), 128.0, radius.toDouble()).filterIsInstance<Player>()

    fun replaceTeam(winners: List<Player>) {
        // winners
        for (member in winners) commands[0].forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("@p", member.name))
            member.sendTitle("", "${ChatColor.GREEN}Your team just won the post!", 10, 60, 15)
        }
        // losers
        for (member in teamPlayers) commands[1].forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("@p", member.name))
            member.sendTitle("", "${ChatColor.RED}Your team just lost the post!", 10, 60, 15)
        }
        // set winners
        team = winners.map { it.uniqueId.toString() }
    }

    fun delete() {
        posts.remove(this)
        PostCaptureRewards.postsFile.yaml.set(id, null)
        PostCaptureRewards.postsFile.save()
    }

    fun saveToFile() {
        PostCaptureRewards.postsFile.yaml.set(id, export())
        PostCaptureRewards.postsFile.save()
    }

    private fun export(): ConfigurationSection {
        val root = PostCaptureRewards.postsFile.yaml.createSection(id)
        root.set("location", location)
        root.set("radius", radius)
        root.set("team", team)
        root.set("commands.win", commands[0])
        root.set("commands.lose", commands[1])
        return root
    }

    companion object {
        fun atLocation(location: Location) = posts.find { it.inRegion(location) }
    }

}