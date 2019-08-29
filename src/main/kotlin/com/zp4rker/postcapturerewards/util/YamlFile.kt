package com.zp4rker.postcapturerewards.util

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class YamlFile(private val file: File) {

    val yaml: YamlConfiguration

    init {
        file.parentFile.mkdirs()
        if (!file.exists()) file.createNewFile()
        yaml = YamlConfiguration.loadConfiguration(file)
    }

    fun save() {
        yaml.save(file)
    }

}