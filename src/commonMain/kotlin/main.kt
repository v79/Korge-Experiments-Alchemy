import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.bus.mapSyncBus
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korge.view.xy
import com.soywiz.korinject.AsyncInjector
import tests.*
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = MyModule))

object MyModule : Module() {

    override val mainScene: KClass<out Scene> = MainMenu::class

    override suspend fun AsyncInjector.configure() {

        mapSyncBus()

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
        mapPrototype { LayoutTest(get()) }
        mapPrototype { UIExperiments(get()) }
        mapPrototype { PixelShaderTest() }
    }
}

fun Container.goHomeButton(sceneContainer: SceneContainer) {
    uiButton(text = "Return to main menu") {
        xy(sceneContainer.width - 150, sceneContainer.height - 50)
        onClick {
            sceneContainer.changeTo<MainMenu>(
                transition = MaskTransition(
                    transition = TransitionFilter.Transition.SWEEP,
                    smooth = true,
                    filtering = true
                ),
                time = 0.5.seconds
            )
        }
    }
}
