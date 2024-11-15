package cn.yurin.stardewlauncher

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

private val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

fun buildModTree(folder: File): ModTree =
    if (folder.listFiles { file: File -> file.isFile && file.name == "manifest.json" }!!.any()) {
        val file = File(folder, "manifest.json")
        json.decodeFromString<Manifest>(Files.readString(Path(file.absolutePath)).trimStart('\uFEFF')).toMod(folder)
    } else {
        ModCategory(
            file = folder,
            name = folder.name.trimStart('.'),
            enabled = !folder.name.startsWith('.'),
            subMods = folder.listFiles()!!.map { buildModTree(it) }
                .groupBy {
                    if (it is ModCategory) {
                        "Category"
                    } else {
                        "Mod"
                    }
                }.mapValues { (_, value) -> value.sortedBy { it.name } }.let {
                    listOf(
                        *it["Category"]?.toTypedArray() ?: arrayOf(),
                        *it["Mod"]?.toTypedArray() ?: arrayOf()
                    )
                }
        )
    }

abstract class ModTree(
    val file: File,
    val name: String,
    val enabled: Boolean,
)

class ModCategory(
    file: File,
    name: String,
    enabled: Boolean,
    val subMods: List<ModTree>
) : ModTree(file, name, enabled)

abstract class Mod(
    file: File,
    name: String,
    enabled: Boolean,
    val description: String,
    val author: String,
    val version: String,
    val minimumApiVersion: String?,
    val minimumGameVersion: String?,
    val uniqueID: String,
    val dependencies: List<Dependency>,
    val updateKeys: List<String>
) : ModTree(file, name, enabled)

class SMAPIMod(
    file: File,
    name: String,
    enabled: Boolean,
    description: String,
    author: String,
    version: String,
    minimumApiVersion: String?,
    minimumGameVersion: String?,
    uniqueID: String,
    val entryDll: String,
    dependencies: List<Dependency>,
    updateKeys: List<String>
) : Mod(file, name, enabled, description, author, version, minimumApiVersion, minimumGameVersion, uniqueID, dependencies, updateKeys)

class ContentPackMod(
    file: File,
    name: String,
    enabled: Boolean,
    description: String,
    author: String,
    version: String,
    minimumApiVersion: String?,
    minimumGameVersion: String?,
    uniqueID: String,
    val contentPackFor: ContentPackDependency,
    dependencies: List<Dependency>,
    updateKeys: List<String>
) : Mod(file, name, enabled, description, author, version, minimumApiVersion, minimumGameVersion, uniqueID, dependencies, updateKeys)

@Serializable
data class Manifest(
    @SerialName("Name")
    val name: String,
    @SerialName("Description")
    val description: String = "",
    @SerialName("Author")
    val author: String,
    @SerialName("Version")
    val version: String,
    @SerialName("MinimumApiVersion")
    val minimumApiVersion: String? = null,
    @SerialName("MinimumGameVersion")
    val minimumGameVersion: String? = null,
    @SerialName("UniqueID")
    val uniqueID: String,
    @SerialName("EntryDll")
    val entryDll: String? = null,
    @SerialName("ContentPackFor")
    val contentPackFor: ContentPackDependency? = null,
    @SerialName("Dependencies")
    val dependencies: List<Dependency> = emptyList(),
    @SerialName("UpdateKeys")
    val updateKeys: List<String> = emptyList()
) {
    fun toMod(file: File): Mod {
        if (entryDll != null) {
            return SMAPIMod(
                file = file,
                name = name,
                enabled = !file.name.startsWith('.'),
                description = description,
                author = author,
                version = version,
                minimumApiVersion = minimumApiVersion,
                minimumGameVersion = minimumGameVersion,
                uniqueID = uniqueID,
                entryDll = entryDll,
                dependencies = dependencies,
                updateKeys = updateKeys
            )
        }
        if (contentPackFor != null){
            return ContentPackMod(
                file = file,
                name = name,
                enabled = !file.name.startsWith('.'),
                description = description,
                author = author,
                version = version,
                minimumApiVersion = minimumApiVersion,
                minimumGameVersion = minimumGameVersion,
                uniqueID = uniqueID,
                contentPackFor = contentPackFor,
                dependencies = dependencies,
                updateKeys = updateKeys
            )
        }
        throw RuntimeException("Unsupported mod")
    }
}

@Serializable
data class ContentPackDependency(
    @SerialName("UniqueID")
    val uniqueID: String,
    @SerialName("MinimumVersion")
    val minimumVersion: String? = null
)

@Serializable
data class Dependency(
    @SerialName("UniqueID")
    val uniqueID: String,
    @SerialName("MinimumVersion")
    val minimumVersion: String? = null,
    @SerialName("IsRequired")
    val isRequired: Boolean? = null
)