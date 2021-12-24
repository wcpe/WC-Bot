package top.wcpe.wcbot.nukkit

import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.lang.TextContainer
import cn.nukkit.permission.Permission
import cn.nukkit.permission.PermissionAttachment
import cn.nukkit.permission.PermissionAttachmentInfo
import cn.nukkit.plugin.Plugin

/**
 * 由 WCPE 在 2021/12/14 10:47 创建
 *
 * Created by WCPE on 2021/12/14 10:47
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *r
 * @author WCPE
 */
data class NukkitConsoleSender(var commanderSender: top.wcpe.wcbot.bot.command.sender.CommandSender? = null) :
    CommandSender {


    override fun sendMessage(s: String) {
        commanderSender?.let { it.sendMessage(s) }
    }

    override fun sendMessage(textContainer: TextContainer?) {
        sendMessage(this.server.language.translate(textContainer))
    }

    override fun getServer(): Server {
        return Server.getInstance()
    }

    override fun getName(): String {
        return "WC-Bot Console"
    }

    override fun isPlayer(): Boolean {
        return false
    }

    override fun isPermissionSet(s: String): Boolean {
        return true
    }

    override fun isPermissionSet(permission: Permission): Boolean {
        return true
    }

    override fun hasPermission(s: String?): Boolean {
        return true
    }

    override fun hasPermission(permission: Permission): Boolean {
        return true
    }

    override fun addAttachment(plugin: Plugin): PermissionAttachment? {
        return null
    }

    override fun addAttachment(plugin: Plugin, s: String?): PermissionAttachment? {
        return null
    }

    override fun addAttachment(plugin: Plugin, s: String?, aBoolean: Boolean?): PermissionAttachment? {
        return null
    }

    override fun removeAttachment(permissionAttachment: PermissionAttachment) {}

    override fun recalculatePermissions() {}

    override fun getEffectivePermissions(): Map<String?, PermissionAttachmentInfo?>? {
        return null
    }

    override fun isOp(): Boolean {
        return true
    }

    override fun setOp(b: Boolean) {}
}