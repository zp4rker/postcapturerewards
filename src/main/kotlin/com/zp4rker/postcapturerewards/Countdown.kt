package com.zp4rker.postcapturerewards

import com.zp4rker.postcapturerewards.lstnr.PlayerMoveListener
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Countdown(val post: Post, val team: List<Player>) {

    private var secondsPassed = 0
    private val secondsToCount = PostCaptureRewards.instance.config.getLong("win-countdown", 90)

    private val checkTask: Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(PostCaptureRewards.instance, {
        secondsPassed += 5
        // send title every 10 seconds
        if (secondsPassed % 10 >= 1 && secondsPassed % 10 < (secondsPassed - 5) % 10) {
            val secondsLeft = secondsToCount - secondsPassed
            team.forEach { it.sendTitle("${ChatColor.GOLD}Capturing post...", "${ChatColor.GRAY}$secondsLeft seconds left", 10, 60, 15) }
        }
        // check if should cancel
        if (shouldCancel()) {
            // cancel countdowns
            cancel()
            // remove countdown
            PlayerMoveListener.countdowns.remove(this)
        }
    }, 20 * 5, 20 * 5)

    private val completionTask: Int = Bukkit.getScheduler().scheduleSyncDelayedTask(PostCaptureRewards.instance, {
        // check if should cancel
        if (shouldCancel()) {
            cancel()
            return@scheduleSyncDelayedTask
        }
        // cancel check task
        Bukkit.getScheduler().cancelTask(checkTask)
        // replace team at post
        post.replaceTeam(team)
    }, secondsToCount * 20)

    fun cancel() {
        arrayOf(checkTask, completionTask).forEach {
            if (Bukkit.getScheduler().isCurrentlyRunning(it) || Bukkit.getScheduler().isQueued(it)) Bukkit.getScheduler().cancelTask(it)
        }
        team.forEach { it.sendTitle("${ChatColor.RED}Countdown canceled", "${ChatColor.GRAY}You left the post or another team entered", 10, 60, 15) }
    }

    private fun shouldCancel(): Boolean {
        val players = post.playersInRegion()
        val teams = mutableListOf<List<Player>>()
        for (player in players) {
//            if (teams.none { it.contains(player) }) teams.add(ASkyBlockAPI.getInstance().getTeamMembers(player.uniqueId).map { Bukkit.getPlayer(it)!! })
            if (teams.none { it.contains(player) }) teams.add(listOf(player))
        }
        // check if any new teams in region or team left region
        return teams.any { !it.contains(team[0]) } || teams.none { it.contains(team[0]) }
    }

}