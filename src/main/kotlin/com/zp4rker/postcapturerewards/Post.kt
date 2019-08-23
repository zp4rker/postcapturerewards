package com.zp4rker.postcapturerewards

import com.wasteofplastic.askyblock.ASkyBlockAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

class Post(private val location: Location, private val radius: Int, private val commands: Array<MutableList<String>> = arrayOf(mutableListOf(), mutableListOf())) {

    private var id = UUID.randomUUID().toString()
    private var team = mutableListOf<Player>()

    private val top = location.add(radius.toDouble(), 0.0, radius.toDouble())
    private val bottom = location.subtract(radius.toDouble(), 0.0, radius.toDouble())

    constructor(id: String, team: MutableList<Player>, location: Location, radius: Int, commands: Array<MutableList<String>>) : this(location, radius, commands) {
        this.id = id
        this.team = team
    }

    init {
        posts.add(this)
    }

    fun inRegion(player: Player): Boolean {
        val location = player.location
        if (location.x > top.x || location.z > top.z) return false
        if (location.x < bottom.x || location.z < bottom.z) return false
        return true
    }

    fun replaceTeam(player: Player) {
        val winners = ASkyBlockAPI.getInstance().getTeamMembers(player.uniqueId).map { Bukkit.getPlayer(it) ?: return }.toMutableList()
        // winners
        for (member in winners) commands[0].forEach { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("@p", member.name)) }
        // losers
        for (member in team) commands[1].forEach { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("@p", member.name)) }
        // set winners
        team = winners
    }

    fun saveToFile() {
        PostCaptureRewards.postsFile.set(id, export())
        PostCaptureRewards.postsFile.save()
    }

    private fun export(): ConfigurationSection {
        val root = PostCaptureRewards.postsFile.createSection(id)
        root.set("location", location.serialize())
        root.set("radius", radius)
        root.set("team", team.map { it.uniqueId.toString() }.toMutableList())
        root.set("commands.win", commands[0])
        root.set("commands.lose", commands[1])
        return root
    }

}