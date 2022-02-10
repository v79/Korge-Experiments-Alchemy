import com.soywiz.korge.Korge
import com.soywiz.korge.bus.Bus
import com.soywiz.korge.bus.GlobalBus
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korinject.AsyncInjector
import tests.*
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = MyModule))

object MyModule : Module() {

    override val mainScene: KClass<out Scene> = MainMenu::class

    override suspend fun AsyncInjector.configure() {

        mapSingleton { GlobalBus() }
        mapPrototype { Bus(get()) }

        mapPrototype { MainMenu() }
        mapPrototype { Test1() }
        mapPrototype { FontTests() }
        mapPrototype { CustomTestRenderer() }
        mapPrototype { DirectFontTests() }
        mapPrototype { VectorGraphicsTests() }
        mapPrototype { FollowMousePointerTest() }
        mapPrototype { TypingTextTest() }
        mapPrototype { DraggingTest() }
        mapPrototype { BackingDataTest() }
        mapPrototype { BusTest(get()) }
        mapPrototype { BusTestPage2(get()) }
    }
}
