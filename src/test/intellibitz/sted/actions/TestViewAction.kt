package sted.actions

import org.junit.Test
import javax.swing.Action

class TestViewAction {
    @Test
    fun testViewToolBar() {
        var action: Action? = null
        var action2: Action? = null
        try {
            action = Class.forName("sted.actions.ViewAction")
                .getDeclaredConstructor().newInstance() as Action
            action2 = Class.forName("sted.actions.ViewAction\$ViewToolBar")
                .getDeclaredConstructor().newInstance() as Action
        } catch (e: Exception) {
            e.printStackTrace()
        }
        action!!
        action2!!
    }
}