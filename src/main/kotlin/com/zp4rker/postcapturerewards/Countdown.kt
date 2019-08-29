package com.zp4rker.postcapturerewards

import com.wasteofplastic.askyblock.ASkyBlockAPI
import com.zp4rker.postcapturerewards.lstnr.PlayerMoveListener
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Countdown(val post: Post, val team: List<Player>) {

    private var secondsPassed = 0
    private val secondsToCount = PostCaptureRewards.instance.config.getLong("win-countdown", 15)

    private val checkTask: Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(PostCaptureRewards.instance, {
        // get seconds left
        val secondsLeft = (secondsToCount - secondsPassed).toInt()
        // send title every 10 seconds
        if (secondsPassed == 0 || secondsLeft % 10 == 0 || secondsLeft < 10) {
            val timeLeftString = "${ChatColor.GRAY}$secondsLeft seconds left"
            // send countdown to capturing team
            team.forEach { it.sendTitle("${ChatColor.GOLD}Capturing post...", timeLeftString, 10, 60, 15) }
            // send countdown to losing team
            post.teamPlayers.forEach {
                if (it.isOnline) {
                    it.sendTitle("${ChatColor.RED}Losing post...", "${ChatColor.GRAY}An enemy team is capturing your post\n$timeLeftString", 10, 60, 15)
                }
            }
        }
        // check if should cancel
        if (shouldCancel()) {
            // cancel countdowns
            cancel()
            // remove countdown
            PlayerMoveListener.countdowns.remove(this)
        }
        // increment time
        secondsPassed++
    }, 0, 20)

    private val completionTask: Int = Bukkit.getScheduler().scheduleSyncDelayedTask(PostCaptureRewards.instance, {
        // check if should cancel
        if (shouldCancel()) {
            cancel()
            return@scheduleSyncDelayedTask
        }
        // cancel check task
        Bukkit.getScheduler().cancelTask(checkTask)
        // replace team at post
        post.replaceTeam(team).also { post.saveToFile() }
        posts.replaceAll { if (it.inRegion(post.location)) post else it }
        PlayerMoveListener.countdowns.replaceAll { if (it.post.inRegion(post.location)) this else it }
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
            if (teams.none { it.contains(player) }) teams.add(ASkyBlockAPI.getInstance().getTeamMembers(player.uniqueId).map { Bukkit.getPlayer(it)!! })
        }
        // check if any new teams in region or team left region
        return teams.any { !it.contains(team[0]) } || teams.none { it.contains(team[0]) }
    }

}