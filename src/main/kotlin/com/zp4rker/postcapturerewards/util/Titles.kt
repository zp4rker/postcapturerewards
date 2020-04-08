package com.zp4rker.postcapturerewards.util

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendTitle(title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
    val titleComponent = IChatBaseComponent.ChatSerializer.a("""{"text": "$title"}""")
    val subtitleComponent = IChatBaseComponent.ChatSerializer.a("""{"text": "$subtitle"}""")

    val titlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent)
    val subtitlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleComponent)
    val lengthPacket = PacketPlayOutTitle(fadeIn, stay, fadeOut)

    with((this as CraftPlayer).handle.playerConnection) {
        sendPacket(titlePacket)
        sendPacket(subtitlePacket)
        sendPacket(lengthPacket)
    }
}