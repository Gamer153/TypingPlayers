package dev.gamer153.typingplayers

import com.comphenix.protocol.*
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import dev.gamer153.typingplayers.listeners.TypingListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.HoverEvent
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class TypingPlayers : JavaPlugin() {
    companion object {
        val playersTyping = hashMapOf<Player, String?>()
        val playerTypingRemoveTasks = hashMapOf<Player, BukkitTask?>()
        lateinit var protocolManager: ProtocolManager
        lateinit var instance: TypingPlayers
    }

    override fun onEnable() {
        instance = this
        protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(
            object : PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
                override fun onPacketReceiving(event: PacketEvent?) {
                    val player = event?.player ?: return
                    player.typing(event.packet?.strings?.readSafely(0))
                }
            }
        )
        val typingCmd = getCommand("typing")!!
        typingCmd.setExecutor { sender: CommandSender, _: Command, _: String, _: Array<String> ->
            val players = playersTyping.map { it.key }
            val components = arrayListOf<Component>()
            components.add("${ChatColor.AQUA}Typing players: ".component())
            playersTyping.forEach { (player, content) ->
                components.add(player.name.component().hoverEvent(HoverEvent.showText((content ?: "Loading...").component())))
                if (players.last() != player) components.add(", ".component())
            }
            if (playersTyping.isEmpty()) components.add("none".component())
            sender.sendMessage(Component.join(JoinConfiguration.noSeparators(), components))
            return@setExecutor true
        }
        typingCmd.setTabCompleter { _, _, _, _ -> listOf<String>() }
        server.pluginManager.registerEvents(TypingListener(), this)
    }
}

fun Player.typing(typing: String?) {
    TypingPlayers.playersTyping[this] = typing
    TypingPlayers.playerTypingRemoveTasks[this]?.cancel()
    TypingPlayers.playerTypingRemoveTasks[this] = object : BukkitRunnable() {
        override fun run() {
            TypingPlayers.playersTyping.remove(this@typing)
        }
    }.runTaskLaterAsynchronously(TypingPlayers.instance, 2 * 20)
}

private fun String.component(): Component {
    return Component.text(this)
}
