package cn.yurin.stardewlauncher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope
import com.kichik.pecoff4j.constant.ResourceType
import com.kichik.pecoff4j.io.DataReader
import com.kichik.pecoff4j.io.PEParser
import com.kichik.pecoff4j.resources.VersionInfo
import com.kichik.pecoff4j.util.ResourceHelper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

object Data {
    var window by mutableStateOf<FrameWindowScope?>(null)
    var gameInformation by mutableStateOf<GameInformation?>(null)

    fun update() {
        gameInformation?.let { setGame(File(it.folder)) }
    }

    fun setGame(folder: File) {
        val deps = File(folder, "Stardew Valley.deps.json")
        val jsonElement = Json.parseToJsonElement(Files.readString(Path(deps.absolutePath)))
        val vanilla = File(folder, "Stardew Valley.exe")
        val vanillaVersion = jsonElement
            .jsonObject["libraries"]!!
            .jsonObject.firstNotNullOf { (key, _) ->
                if (key.startsWith("Stardew Valley/")) {
                    key.removePrefix("Stardew Valley/")
                } else {
                    null
                }
            }
        val mod = File(folder, "StardewModdingAPI.exe")
        if (mod.exists()) {
            val moddingVersion = VersionInfo.read(
                DataReader(
                    ResourceHelper.findResources(
                        PEParser.parse(mod).imageData.resourceTable,
                        ResourceType.VERSION_INFO
                    )[0].data
                )
            )
                .stringFileInfo.getTable(0).strings
                .find { it.key == "FileVersion" || it.key == "Assembly Version" }!!
                .value
            val modsFolder = File(folder, "Mods")
            gameInformation = ModdingGameInformation(
                folder = folder.absolutePath,
                vanillaGame = vanilla.absolutePath,
                vanillaVersion = vanillaVersion,
                moddingGame = mod.absolutePath,
                moddingVersion = moddingVersion,
                mods = buildModTree(modsFolder) as ModCategory
            )
        } else {
            gameInformation = GameInformation(
                folder = folder.absolutePath,
                vanillaGame = vanilla.absolutePath,
                vanillaVersion = vanillaVersion
            )
        }
    }
}

open class GameInformation(
    val folder: String,
    val vanillaGame: String,
    val vanillaVersion: String
)

class ModdingGameInformation(
    folder: String,
    vanillaGame: String,
    vanillaVersion: String,
    val moddingGame: String,
    val moddingVersion: String,
    val mods: ModCategory
) : GameInformation(folder, vanillaGame, vanillaVersion)