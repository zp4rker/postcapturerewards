package com.zp4rker.postcapturerewards.lstnr

import com.zp4rker.postcapturerewards.Countdown
import com.zp4rker.postcapturerewards.posts
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener : Listener {

    companion object {
        val countdowns = mutableListOf<Countdown>()
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // make sure location and world is not null
        val location = event.to ?: return
        // get post
        val post = posts.find { it.inRegion(location) }
        // post exists check
        post ?: return
        // check for other players at post
        val players = post.playersInRegion()
        // get teams
        val teams = mutableListOf<List<Player>>()
        for (player in players) {
            if (teams.none { it.contains(player) }) {
//                teams.add(ASkyBlockAPI.getInstance().getTeamMembers(player.uniqueId).map { Bukkit.getPlayer(it)!! }.ifEmpty { listOf(player) })
                teams.add(listOf(player))
            }
        }
        // check present teams
        if (teams.size > 1) {
            countdowns.filter { it.post.inRegion(post.location) }.forEach {
                // cancel all countdowns
                it.cancel()
                // remove countdown
                countdowns.remove(it)
            }
        } else if (teams.size == 1) {
            // check if countdown isnt already running
            if (countdowns.find { it.team.contains(teams[0][0]) } == null) {
                // start new countdown if present team is foreign
                if (posts.find { it.teamPlayers.contains(teams[0][0]) } == null) countdowns.add(Countdown(post, teams[0]))
            }
        }
    }

}