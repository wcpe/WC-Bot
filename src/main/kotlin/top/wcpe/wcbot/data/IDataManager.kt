package top.wcpe.wcbot.data

import top.wcpe.wcbot.entity.GamePlayerData
import top.wcpe.wcbot.entity.QQMemberData
import java.util.function.Consumer
import java.util.function.UnaryOperator

/**
 * 由 WCPE 在 2021/11/26 22:19 创建
 * Created by WCPE on 2021/11/26 22:19
 * Github: https://github.com/wcpe
 * QQ: 1837019522
 *
 * @author WCPE
 */
interface IDataManager {

    fun getQQMemberData(qq: Long): QQMemberData

    fun saveQQMemberData(qqMemberData: QQMemberData): Boolean

    fun useQQMemberData(qq: Long, callBack: Consumer<QQMemberData>) {
        callBack.accept(getQQMemberData(qq))
    }

    fun useSaveQQMemberData(qq: Long, callBack: UnaryOperator<QQMemberData>): Boolean {
        return saveQQMemberData(callBack.apply(getQQMemberData(qq)))
    }

    fun getGamePlayerData(playerName: String): GamePlayerData

    fun saveGamePlayerData(gamePlayerData: GamePlayerData): Boolean

    fun useGamePlayerData(playerName: String, callBack: Consumer<GamePlayerData>) {
        callBack.accept(getGamePlayerData(playerName))
    }

    fun useSaveGamePlayerData(playerName: String, callBack: UnaryOperator<GamePlayerData>): Boolean {
        return saveGamePlayerData(getGamePlayerData(playerName))
    }

}