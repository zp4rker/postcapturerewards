package com.zp4rker.postcapturerewards.util

import org.bukkit.configuration.file.FileConfiguration
import java.io.File

class YamlFile(private val file: File) : FileConfiguration() {

    init {
        file.parentFile.mkdirs()
        if (!file.exists()) file.createNewFile()
        load(file)
    }

    fun save() {
        save(file)
    }

    override fun loadFromString(p0: String) = load(p0.reader())
    override fun saveToString() = toString()
    override fun buildHeader() = ""

}