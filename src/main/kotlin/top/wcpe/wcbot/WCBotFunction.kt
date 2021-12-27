package top.wcpe.wcbot

import javax.script.Invocable
import javax.script.ScriptEngineManager
import kotlin.jvm.Throws

/**
 * 由 WCPE 在 2021/12/24 23:43 创建
 *
 * Created by WCPE on 2021/12/24 23:43
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
object WCBotFunction {

    @Throws(ClassCastException::class)
    fun invokeJavaScriptFunction(javaScript: String, function: String, vararg args: Any): Any {
        val engine = ScriptEngineManager().getEngineByName("nashorn")
        engine.eval(javaScript)
        return (engine as Invocable).invokeFunction(function, *args)
    }

}