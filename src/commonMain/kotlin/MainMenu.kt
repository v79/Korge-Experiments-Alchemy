import com.soywiz.klock.seconds
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UISkin
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalFill
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import tests.*

class MainMenu : Scene() {

	lateinit var mainMenuSkin: UISkin

	val transition = MaskTransition(
		transition = TransitionFilter.Transition.VERTICAL,
		smooth = true,
		filtering = true
	)

	override suspend fun Container.sceneInit() {
		mainMenuSkin = UISkin { }
	}

	override suspend fun Container.sceneMain() {
		container {
			flowContainer(
				maxWidth = 400.0,
				maxHeight = 650.0,
				minPadding = 10.0,
				alignment = FlowContainer.FlowAlignment.Center,
				crop = false,
				layout = FlowContainer.FlowLayout.Horizontal
			) {
				centerXOnStage()
				uiVerticalFill(height = 600.0) {
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
					uiButton(text = "Layout Tests") {
						onClick { goToScene("layoutTest") }
					}
				}
				uiVerticalFill {
					uiButton(text = "UI Experiments") {
						onClick { goToScene("uiTest") }
					}
					uiButton(text = "Pixel Shader Tests") {
						onClick { goToScene("pixelShaderTest") }
					}
				}
			}
		}
		uiButton(text = "Quit") {
			xy(0.0, 0.0)
			onClick {
				views.gameWindow.close()
			}
		}
	}

	private suspend fun goToScene(nextScene: String) {
		when (nextScene) {
			"test1" -> {
				sceneContainer.changeTo<Test1>(
					transition = transition,
					time = 1.seconds
				)
			}
			"fonts" -> {
				sceneContainer.changeTo<FontTests>(
					transition = transition,
					time = 1.seconds
				)
			}
			"customRenderer" -> {
				sceneContainer.changeTo<CustomTestRenderer>(
					transition = transition,
					time = 1.seconds
				)
			}
			"directFont" -> {
				sceneContainer.changeTo<DirectFontTests>(
					transition = transition,
					time = 1.seconds
				)
			}
			"vectorGraphics" -> {
				sceneContainer.changeTo<VectorGraphicsTests>(
					transition = transition,
					time = 1.seconds
				)
			}
			"followMouseTest" -> {
				sceneContainer.changeTo<FollowMousePointerTest>(
					transition = transition,
					time = 1.seconds
				)
			}
			"typingText" -> {
				sceneContainer.changeTo<TypingTextTest>(
					transition = transition,
					time = 1.seconds
				)
			}
			"draggingTest" -> {
				sceneContainer.changeTo<DraggingTest>(
					transition = transition,
					time = 1.seconds
				)
			}
			"backingTest" -> {
				sceneContainer.changeTo<BackingDataTest>(
					transition = transition,
					time = 1.seconds
				)
			}
			"busTest" -> {
				sceneContainer.changeTo<BusTest>(
					transition = transition,
					time = 1.seconds
				)
			}
			"layoutTest" -> {
				sceneContainer.changeTo<LayoutTest>(
					transition = transition,
					time = 1.seconds
				)
			}
			"uiTest" -> {
				sceneContainer.changeTo<UIExperiments>(
					transition = transition,
					time = 1.seconds
				)
			}
			"pixelShaderTest" -> {
				sceneContainer.changeTo<PixelShaderTest>(
					transition = transition,
					time = 1.seconds
				)			}
		}
	}
}
