package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korge.bus.Bus
import com.soywiz.korge.bus.GlobalBus
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.color.Colors

class MyInt(val int: Int)
class SpecialMessage(val wibble: String)

class BusTest(private val bus: Bus) : Scene() {

	private var specialMessage: String = ""
	override suspend fun Container.sceneInit() {
		println("sceneInit")
		bus.register<SpecialMessage> {
			specialMessage = it.wibble
			println("init received message ${it.wibble}")
		}
	}

	override suspend fun Container.sceneMain() {
		var counter = 0;
		container {
			container {
				text("Bus tests - click yellow square") {
					xy(20.0, 20.0)
				}

				text("CounterA: ") {
					xy(20.0, 40.0)
					bus.register<MyInt> {
						this.text = "CounterA: ${it.int}"
						println("CounterA Received ${it.int} from bus")
					}
				}

				text("Counter B: ") {
					xy(120.0, 40.0)
					bus.register<MyInt> {
						this.text = "CounterB: ${it.int}"
						println("CounterB Received ${it.int} from bus")
					}
				}

				solidRect(width = 100.0, height = 100.0, color = Colors.YELLOW) {
					xy(200.0, 200.0)
					onClick {
						counter++
						bus.send(MyInt(counter))
						println("Send $counter to bus")
					}
				}

				text("Special message goes here: $specialMessage") {
					xy(350.0, 200.0)
						bus.register<SpecialMessage> {
							println("received message ${it.wibble}")
							text = it.wibble
						}
				}
			}

			uiButton(text = "Go to page 2") {
				xy(800.0, 520.0)
				onClick {
					sceneContainer.changeTo<BusTestPage2>(
						transition = MaskTransition(
							transition = TransitionFilter.Transition.SWEEP,
							smooth = true,
							filtering = true
						),
						time = 0.5.seconds
					)
				}
			}

			uiButton(text = "Return to main menu") {
				xy(800.0, 600.0)
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
	}
}


class BusTestPage2(private val bus: Bus) : Scene() {

	var message = "Hello from page 2"
	override suspend fun Container.sceneInit() {

	}

	override suspend fun Container.sceneMain() {
		var counter = 0;
		container {
			container {
				text("Bus tests page two") {
					xy(20.0, 20.0)
				}

				text("Click the blue rectangle to send message '$message' to page one") {
					xy(200.0, 160.0)
				}

				solidRect(width = 100.0, height = 100.0, color = Colors.CORNFLOWERBLUE) {
					xy(200.0, 200.0)
					onClick {
						bus.send(SpecialMessage(message))
						println("Send $message to bus")
					}
				}
			}

			uiButton(text = "Return to page 1") {
				xy(800.0, 520.0)
				onClick {
					sceneContainer.changeTo<BusTest>(
						transition = MaskTransition(
							transition = TransitionFilter.Transition.SWEEP,
							smooth = true,
							filtering = true
						),
						time = 0.5.seconds
					)
				}
			}

			/*uiButton(text = "Return to main menu") {
				xy(800.0, 600.0)
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
			}*/

		}
	}
}
