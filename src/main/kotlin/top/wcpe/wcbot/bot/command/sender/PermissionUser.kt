package top.wcpe.wcbot.bot.command.sender

import top.wcpe.wcbot.WCBot
import top.wcpe.wcbot.entity.QQMemberData

/**
 * 由 WCPE 在 2021/12/16 20:03 创建
 *
 * Created by WCPE on 2021/12/16 20:03
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
abstract class PermissionUser(var qqMemberData: QQMemberData) : CommandSender {

    override fun hasPermission(permission: String): Boolean {
        if (qqMemberData.qq == WCBot.serverData.ownerQQ || qqMemberData.permissionList.contains(permission) || WCBot.instance
                .groupsConfig.getStringList("default-group").flatMap {
                    WCBot.instance
                        .groupsConfig.getStringList("groups.${it}")
                }.contains(permission)
        ) {
            return true
        }
        for (permissionGroup in qqMemberData.permissionGroups) {
            if (WCBot.instance.groupsConfig.getStringList("groups.$permissionGroup").contains(permission)) {
                return true
            }
        }
        return false
    }

}