package sted

import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
    @Test
    fun testAppHasAGreeting() {
        val classUnderTest = App()
        assertNotNull(classUnderTest.projectName, "app should have a project name")
    }
}
