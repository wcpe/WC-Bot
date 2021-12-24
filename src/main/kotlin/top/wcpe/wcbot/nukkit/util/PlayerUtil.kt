package top.wcpe.wcbot.nukkit.util

import cn.nukkit.Server
import cn.nukkit.nbt.NBTIO
import cn.nukkit.nbt.tag.CompoundTag
import cn.nukkit.nbt.tag.ListTag
import top.wcpe.wcbot.nukkit.entity.PlayerItemInventory
import top.wcpe.wcpelib.nukkit.inventory.InventoryPlusBuilder
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * 由 WCPE 在 2021/12/20 10:47 创建
 *
 * Created by WCPE on 2021/12/20 10:47
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
object PlayerUtil {

    private val PLAYER_REGEX: Pattern =
        Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}.dat$")

    private fun getPlayerUUID(name: String): UUID? {
        Server.getInstance().getPlayer(name)
            ?.let { return it.uniqueId }

        for (file in File(Server.getInstance().dataPath, "players/").listFiles()) {
            if (PLAYER_REGEX.matcher(file.name).matches() && file.name.endsWith(".dat")) {
                var fileName = file.name
                fileName = fileName.substring(0, fileName.length - 4)
                return UUID.fromString(fileName)
            }
        }

        return null
    }

    private fun getPlayerCompoundTag(playerName: String): CompoundTag? {
        getPlayerUUID(playerName)?.let {
            return Server.getInstance().getOfflinePlayerData(it, true)
        }
        return null
    }

    fun getPlayerInventory(playerName: String): PlayerItemInventory? {
        getPlayerCompoundTag(playerName)?.let {
            if (it.contains("Inventory") && it["Inventory"] is ListTag<*>) {
                val playerItemInventory = PlayerItemInventory(
                    InventoryPlusBuilder().build().rawInventory,
                    InventoryPlusBuilder().build().rawInventory
                )
                val inventoryList = it.getList("Inventory", CompoundTag::class.java)
                for (item in inventoryList.all) {
                    val slot = item.getByte("Slot")
                    if (slot in 100..103) {
                        playerItemInventory.mainInventory.setItem(
                            playerItemInventory.mainInventory.size + slot - 100,
                            NBTIO.getItemHelper(item)
                        )
                    } else {
                        playerItemInventory.mainInventory.setItem(slot - 9, NBTIO.getItemHelper(item))
                    }
                    if (slot == -106) playerItemInventory.offInventory.setItem(0, NBTIO.getItemHelper(item))
                }
                return playerItemInventory
            }
        }
        return null

    }


}