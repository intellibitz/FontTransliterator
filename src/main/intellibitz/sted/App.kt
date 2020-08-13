package sted

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNullOrEmpty()) {
            // launch GUI
            STEDGUI.main(args)
        } else {
            // launch Console
            STEDConsole.main(args)
        }
    }

}

class App {
    val projectName: String
        get() = "FontTransliterator - STED."

}

fun main(args: Array<String>) {
    println(App().projectName + args)
    Main.main(args)
}
