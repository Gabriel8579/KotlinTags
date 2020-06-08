package com.gabriel8579.KotlinTags

import com.gabriel8579.KotlinTags.commands.TagCommand
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Suppress("unused")
class Main: JavaPlugin() {

    var tags = HashSet<Tag>()
    var jogadores = HashMap<UUID, Jogador>()

    override fun onEnable() {
        INSTANCE = this

        val file = File(dataFolder.path + "config.yml")

        if (!file.exists()) {
            config.addDefault("tags.default.chat", "&7%player%&r")
            config.addDefault("tags.default.playerList", "&7%player%")
            config.addDefault("tags.default.headPrefix", "&7")
            config.addDefault("tags.default.headSuffix", "&r")
            config.addDefault("tags.default.permission", "tag.default")
            config.options().copyDefaults(true)
            saveConfig()
        }

        logger.info("Iniciando!!")
        getCommand("tag").executor = TagCommand()
        getCommand("tag").tabCompleter = TagCommand()

        server.pluginManager.registerEvents(Eventos(), this)
        super.onEnable()
    }

    override fun onLoad() {
        super.onLoad()
        carregaTags()
    }

    fun carregaTags() {
        val configurationSection = this.config.getConfigurationSection("tags")
        if (this.config.get("tags") != null) {
            for (id: String in configurationSection.getKeys(false)) {
                tags.add(
                    Tag(
                        id = id,
                        chat = configurationSection.getString("$id.chat", "").replace("&", "§"),
                        playerList = configurationSection.getString("$id.playerList", "").replace("&", "§"),
                        headPrefix =  configurationSection.getString("$id.headPrefix", "").replace("&", "§"),
                        headSuffix = configurationSection.getString("$id.headSuffix", "").replace("&", "§"),
                        permission = configurationSection.getString("$id.permission", "").replace("&", "§")
                    )
                )
            }
        }
    }

    companion object {
        internal lateinit var INSTANCE: Main
    }
}

data class Jogador(var tags: HashSet<Tag> = HashSet())


