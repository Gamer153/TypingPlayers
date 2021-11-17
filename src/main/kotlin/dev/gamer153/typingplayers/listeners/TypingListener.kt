package dev.gamer153.typingplayers.listeners

import dev.gamer153.typingplayers.TypingPlayers
import dev.gamer153.typingplayers.typing
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent

class TypingListener : Listener {
    @EventHandler
    fun onCommandsSend(evt: PlayerCommandSendEvent) {
        evt.player.typing("Started typing...")
    }

    @EventHandler
    fun onCommandPreprocess(evt: PlayerCommandPreprocessEvent) {
        TypingPlayers.playersTyping.remove(evt.player)
    }
}