package com.gabriel8579.KotlinTags

import com.gabriel8579.KotlinTags.Main.Companion.INSTANCE
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Scoreboard

@Suppress("DEPRECATION")
class Tag(
    val id: String,
    private val chat: String = "",
    private val playerList: String = "",
    private val headPrefix: String = "",
    private val headSuffix: String = "",
    val permission: String = ""
) {


    fun add(player: Player) {
        val scoreboard: Scoreboard = INSTANCE.server.scoreboardManager.mainScoreboard

        if (scoreboard.getTeam(this.id) == null) {
            createTeam(scoreboard)
        }

        player.playerListName = this.playerList.replace("%player%", player.name).replace("&", "§")
        player.displayName = this.chat.replace("%player%", player.name).replace("&", "§")
        addPlayerToTeam(scoreboard, player)

    }

    fun remove(player: Player) {
        val scoreboard: Scoreboard = INSTANCE.server.scoreboardManager.mainScoreboard

        if (scoreboard.getTeam(this.id) != null && scoreboard.getTeam(this.id).hasPlayer(player)) {
            scoreboard.getTeam(this.id).removePlayer(player)
        }
        player.playerListName = player.name
        player.displayName = player.name
    }

    private fun addPlayerToTeam(scoreboard: Scoreboard, player: Player) {
        scoreboard.getTeam(this.id).addPlayer(player)
    }

    private fun createTeam(scoreboard: Scoreboard) {
        if (scoreboard.getTeam(this.id) != null) {
            updateTeam(scoreboard)
        }
        scoreboard.registerNewTeam(this.id)
        val team = scoreboard.getTeam(this.id)

        team.prefix = this.headPrefix.replace("&", "§")
        team.suffix = this.headSuffix.replace("&", "§")
        team.nameTagVisibility = NameTagVisibility.ALWAYS
    }

    fun updateTeam(scoreboard: Scoreboard) {
        if (scoreboard.getTeam(this.id) == null) {
            createTeam(scoreboard)
        }
        scoreboard.getTeam(this.id).unregister()
        createTeam(scoreboard)
    }

    fun getChat(playerName: String): String = this.chat.replace("%player%", playerName)
    fun getHead(playerName: String): String = this.headPrefix + playerName + this.headSuffix
    fun getPlayerList(playerName: String): String = this.playerList.replace("%player%", playerName)

}