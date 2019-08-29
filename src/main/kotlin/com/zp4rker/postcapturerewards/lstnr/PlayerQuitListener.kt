package com.zp4rker.postcapturerewards.lstnr

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // check if countdown for player exists
        val countdown = PlayerMoveListener.countdowns.find { it.team.contains(event.player) } ?: return
        // chcek if whole team is absent
        if (countdown.post.playersInRegion().none { countdown.team.contains(it) }) {
            // cancel and remove countdown
            PlayerMoveListener.countdowns.remove(countdown)
            countdown.cancel()
        }
    }

}