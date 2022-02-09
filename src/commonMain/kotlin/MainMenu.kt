import com.soywiz.klock.seconds
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.service.process.NativeProcess
import com.soywiz.korge.ui.UISkin
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalFill
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.centerOnStage
import com.soywiz.korge.view.container
import com.soywiz.korge.view.filter.TransitionFilter
import tests.*

class MainMenu : Scene() {

	lateinit var mainMenuSkin: UISkin

	override suspend fun Container.sceneInit() {
		mainMenuSkin = UISkin { }
	}

	override suspend fun Container.sceneMain() {
		container {
			uiVerticalFill(height = 600.0) {
				centerOnStage()
				uiButton(text = "Wrapping Text") {
					onClick { goToScene("test1") }
				}
				uiButton(text = "Fonts") {
					onClick { goToScene("fonts") }
				}
				uiButton(text = "Custom Renderer") {
					onClick { goToScene("customRenderer") }
				}
				uiButton(text = "Direct Font Render") {
					onClick { goToScene("directFont") }
				}
				uiButton(text = "Vector Graphics") {
					onClick { goToScene("vectorGraphics") }
				}
				uiButton(text = "Draw follows mouse") {
					onClick { goToScene("followMouseTest") }
				}
				uiButton(text = "Typing Text") {
					onClick { goToScene("typingText") }
				}
				uiButton(text = "Dragging Tests") {
					onClick { goToScene("draggingTest") }
				}
				uiButton(text = "Backing Data Tests") {
					onClick { goToScene("backingTest") }
				}
				uiButton(text = "Bus Tests") {
					onClick { goToScene("busTest") }
				}
				uiButton(text = "Quit") {
					onClick {
						NativeProcess(views).close(0)
					}
				}
			}
		}
	}

	private suspend fun goToScene(nextScene: String) {
		when (nextScene) {
			"test1" -> {
				sceneContainer.changeTo<Test1>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"fonts" -> {
				sceneContainer.changeTo<FontTests>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"customRenderer" -> {
				sceneContainer.changeTo<CustomTestRenderer>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"directFont" -> {
				sceneContainer.changeTo<DirectFontTests>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"vectorGraphics" -> {
				sceneContainer.changeTo<VectorGraphicsTests>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"followMouseTest" -> {
				sceneContainer.changeTo<FollowMousePointerTest>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"typingText" -> {
				sceneContainer.changeTo<TypingTextTest>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"draggingTest" -> {
				sceneContainer.changeTo<DraggingTest>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"backingTest" -> {
				sceneContainer.changeTo<BackingDataTest>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
			"busTest" -> {
				sceneContainer.changeTo<BusTest>(
					transition = MaskTransition(
						transition = TransitionFilter.Transition.VERTICAL,
						smooth = true,
						filtering = true
					),
					time = 1.seconds
				)
			}
		}
	}
}
