# WC-Bot
Nukkit QQ机器人插件

## 食用方法
> 首先进入下载链接
> 
> 下载插件本体 下载插件前置 [WcpeLib](https://wxys233.lanzout.com/b00v6geyh) 密码 1stb
> 
>将本体和前置一起丢入服务器下的插件文件夹 plugins
>
>开启服务器一次服务器 加载完毕后关服
> 
>然后打开服务器插件文件夹 plugins/WC-Bot/config.yml 配置文件
> 
>进行相应配置 然后开服即可
> 
>如果你登录不上去 请参照帖子使用 Mirai Android 进行登录
> 
> [无法登录的临时处理方案 | MiraiForum (mamoe.net) ](https://mirai.mamoe.net/topic/223/%E6%97%A0%E6%B3%95%E7%99%BB%E5%BD%95%E7%9A%84%E4%B8%B4%E6%97%B6%E5%A4%84%E7%90%86%E6%96%B9%E6%A1%88/3)

## 指令部分
> /wc-bot
> 
> /wb
> 
>      setOwner <主人QQ>
>      描述: 设置主人QQ
>      权限: wc-bot.setOwner.use
>      addGroups <QQ群号>
>      描述: 添加开启群聊
>      权限: wc-bot.addGroups.use
>      delGroups <QQ群号>
>      描述: 删除开启群聊
>      权限: wc-bot.delGroups.use
>      listGroups
>      描述: 查询开启群聊
>      权限: wc-bot.listGroups.use
>      sendGroup <QQ群号> <消息>
>      描述: 发送消息至QQ群
>      权限: wc-bot.sendGroup.use
>      sendQQ <QQ号> <消息>
>      描述: 发送消息至QQ
>      权限: wc-bot.sendQQ.use
>      reload
>      描述: 重载机器人
>      权限: wc-bot.reload.use
> []为选填参数 <>为必填参数
## Api

WCBotApi

```kotlin
//获取 QQ 用户的数据
fun getQQMemberData(qq: Long): QQMemberData

//存储 QQ 用户的数据
fun saveQQMemberData(qqMemberData: QQMemberData): Boolean

//使用 QQ 用户的数据
fun useQQMemberData(qq: Long, callBack: Consumer<QQMemberData>)

//使用并存储 QQ 用户的数据
fun useSaveQQMemberData(qq: Long, callBack: UnaryOperator<QQMemberData>): Boolean

//获取玩家的数据
fun getGamePlayerData(playerName: String): GamePlayerData

//获取玩家的数据
fun saveGamePlayerData(gamePlayerData: GamePlayerData): Boolean

//使用玩家的数据
fun useGamePlayerData(playerName: String, callBack: Consumer<GamePlayerData>)

//使用并存储玩家的数据
fun useSaveGamePlayerData(playerName: String, callBack: UnaryOperator<GamePlayerData>): Boolean
```

## Building

* [Gradle](https://gradle.org/) - Dependency Management

The GradleWrapper in included in this project.

**Windows:**

```
gradlew.bat clean shadowJar
```

**macOS/Linux:**

```
./gradlew clean shadowJar
```

Build artifacts should be found in `./build/libs` folder.


# 本插件使用以下开源 SDK

<div align="center">
   <img width="160" src="https://github.com/mamoe/mirai/blob/dev/docs/mirai.png" alt="logo"></br>

   <img width="95" src="https://github.com/mamoe/mirai/blob/dev/docs/mirai.svg" alt="title">

----

![Gradle CI](https://github.com/mamoe/mirai/workflows/Gradle%20CI/badge.svg?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/net.mamoe/mirai-core-api.svg?label=Maven%20Central)](https://search.maven.org/search?q=net.mamoe%20mirai)
[![Gitter](https://badges.gitter.im/mamoe/mirai.svg)](https://gitter.im/mamoe/mirai?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net)

mirai 是一个在全平台下运行，提供 QQ Android 协议支持的高效率机器人库

这个项目的名字来源于
<p><a href = "http://www.kyotoanimation.co.jp/">京都动画</a>
作品<a href = "https://zh.moegirl.org.cn/zh-hans/%E5%A2%83%E7%95%8C%E7%9A%84%E5%BD%BC%E6%96%B9">《境界的彼方》</a>
的<a href = "https://zh.moegirl.org.cn/zh-hans/%E6%A0%97%E5%B1%B1%E6%9C%AA%E6%9D%A5">栗山未来(Kuriyama <b>mirai</b>)</a></p>
<p><a href = "https://www.crypton.co.jp/">CRYPTON</a>以<a href = "https://www.crypton.co.jp/miku_eng">初音未来</a>
为代表的创作与活动<a href = "https://magicalmirai.com/2019/index_en.html">(Magical <b>mirai</b>)</a></p>
图标以及形象由画师<a href = "https://github.com/DazeCake">DazeCake</a>绘制
</div>