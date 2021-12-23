import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korinject.AsyncInjector
import tests.CustomTestRenderer
import tests.DirectFontTests
import tests.FontTests
import tests.Test1
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = MyModule))

object MyModule : Module() {

    override val mainScene: KClass<out Scene> = MainMenu::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { MainMenu() }
        mapPrototype { Test1() }
        mapPrototype { FontTests() }
        mapPrototype { CustomTestRenderer() }
        mapPrototype { DirectFontTests() }
    }
}