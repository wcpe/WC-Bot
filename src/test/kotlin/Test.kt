import org.junit.jupiter.api.Test
import top.wcpe.wcbot.WCBotFunction
import java.time.LocalDate
import java.time.LocalDateTime
import javax.script.Invocable
import javax.script.ScriptEngineManager

/**
 * 由 WCPE 在 2021/12/26 19:50 创建
 *
 * Created by WCPE on 2021/12/26 19:50
 *
 * Github: https://github.com/wcpe
 *
 * QQ: 1837019522
 *
 * @author WCPE
 */
class Test {

    @Test
    fun testInvokeJavaScript() {

        println(
            WCBotFunction.invokeJavaScriptFunction(
                """
    function result(day) {
         return day
    }
""", "result", 233
            )
        )

        val engine = ScriptEngineManager().getEngineByName("nashorn")
        val js = """
    function result(day) {
         return day
    }
"""
        engine.eval(js)
        val invocable = engine as Invocable

        val result = invocable.invokeFunction("result", 111)
        println(result)
    }

    @Test
    fun testMonth() {
        println(LocalDate.now())
    }
}