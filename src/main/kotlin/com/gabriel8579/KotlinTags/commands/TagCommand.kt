package com.gabriel8579.KotlinTags.commands

import com.gabriel8579.KotlinTags.Jogador
import com.gabriel8579.KotlinTags.Main
import com.gabriel8579.KotlinTags.Tag
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.NoSuchElementException

class TagCommand : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (command?.name == "tag") {
            if (sender !is Player) {
                sender?.sendMessage("Você não é um jogador!")
                return false
            }

            if (args?.size == 0) {
                sender.sendMessage("§7Suas tags:")
                Main.INSTANCE.jogadores[sender.uniqueId]?.tags?.forEach { sender.sendMessage(it.getChat(it.id)) }
                return true
            }

            if (args?.get(0) == "off") {
                Main.INSTANCE.tags.forEach { it.remove(sender) }
                sender.sendMessage("§aVocê removeu sua tag com sucesso!")
            } else if (args?.get(0) == "reload") {
                if (args.size == 1 && !sender.hasPermission("tag.reloadAllPlayersTags")) {
                    sender.sendMessage("§4Você não pode fazer isso!")
                }
                if (args.size == 2 && !sender.hasPermission("tag.reloadPlayerTags")) {
                    sender.sendMessage("§4Você não pode fazer isso!")
                }
                if (args.size == 1) {
                    Main.INSTANCE.jogadores.clear()
                    for (player in Main.INSTANCE.server.onlinePlayers) {
                        Main.INSTANCE.jogadores[player.uniqueId] = Jogador(HashSet())
                    }
                    Main.INSTANCE.jogadores.forEach { (t, u) -> u.tags.removeAll(u.tags);
                        Main.INSTANCE.tags.forEach {
                            if (it.permission.isEmpty() || Main.INSTANCE.server.getPlayer(t).hasPermission(it.permission)) {
                                u.tags.add(it)
                            }
                        }
                    }
                    sender.sendMessage("§aTags atualizadas com sucesso!")
                } else if (args.size == 2) {
                    val player = Main.INSTANCE.server.getPlayer(args[1])
                    if (player is Player) {
                        Main.INSTANCE.jogadores.remove(player.uniqueId)
                        Main.INSTANCE.jogadores[player.uniqueId] = Jogador(HashSet())
                        Main.INSTANCE.jogadores[player.uniqueId]?.tags?.removeAll(Main.INSTANCE.jogadores[player.uniqueId]?.tags!!)
                        Main.INSTANCE.tags.forEach {
                            if (it.permission.isEmpty() || player.hasPermission(it.permission)) {
                                Main.INSTANCE.jogadores[player.uniqueId]?.tags?.add(it)
                            }
                        }
                        sender.sendMessage("§aTags atualizadas com sucesso!")
                    }
                }

            } else if (args?.get(0) == "update") {
                if (!sender.hasPermission("tag.updateTags")) {
                    sender.sendMessage("§4Você não pode fazer isso!")
                    return false
                }
                Main.INSTANCE.reloadConfig()
                Main.INSTANCE.tags.clear()
                Main.INSTANCE.carregaTags()
                Main.INSTANCE.tags.forEach { it.updateTeam(Main.INSTANCE.server.scoreboardManager.mainScoreboard) }
                sender.sendMessage("§aTags atualizadas com sucesso! Atualize as tags dos jogadores usando /tag reload")
            } else {
                var tag: Tag? = null
                try {
                    tag = Main.INSTANCE.jogadores[sender.uniqueId]?.tags?.single { it.id == args?.get(0) }
                } catch (e: NoSuchElementException) {
                    Main.INSTANCE.server.consoleSender.sendMessage("${sender.uniqueId} Tentou acessar a tag inexistente ${args?.get(0)}")
                } finally {
                    if (tag == null || (tag.permission.isNotEmpty() && !sender.hasPermission(tag.permission))) {
                        sender.sendMessage("§cParece que a tag ${args?.get(0)} não existe...")
                        return true
                    }
                    tag.add(sender)
                    sender.sendMessage("§aVocê está usando a tag ${tag.getChat(tag.id)} §aagora!")

                }
            }

            return true
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender?,
        command: Command?,
        alias: String?,
        args: Array<out String>?
    ): List<String>? {
        if (sender !is Player) {
            return null
        }
        val tab = ArrayList<String>()
        Main.INSTANCE.jogadores[sender.uniqueId]?.tags?.forEach { tab.add(it.id) }
        tab.add("off")
        return tab
    }

}