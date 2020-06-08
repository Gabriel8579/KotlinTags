package com.gabriel8579.KotlinTags

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

class Eventos : Listener {

    @EventHandler
    fun aoEntrar(aoEntrarEvento: PlayerLoginEvent) {
        val player = aoEntrarEvento.player
        if (Main.INSTANCE.jogadores.containsKey(player.uniqueId)) {
            Main.INSTANCE.jogadores.remove(player.uniqueId)
        }
        Main.INSTANCE.jogadores[player.uniqueId] = Jogador(HashSet<Tag>())
        Main.INSTANCE.tags.forEach {
            if (it.permission.isEmpty() || player.hasPermission(it.permission)) {
                Main.INSTANCE.jogadores[player.uniqueId]?.tags?.add(it)
            }
        }

    }

}