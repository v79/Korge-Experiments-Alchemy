package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import kotlin.random.Random

class BackingDataTest : Scene() {

	val squares = listOf(Square("1","RED"),Square("2","BLUE"),Square("3","GREEN"), Square("4","BLUE"),Square("5","RED"), Square("6","GREEN"))

	private fun getRandomColour(): String {
		val r = Random.Default
		val newColour = when(r.nextInt(0,3)) {
			0 -> "RED"
			1 -> "GREEN"
			2 -> "BLUE"
			else -> "WHITE"
		}
		return newColour
	}

	override suspend fun Container.sceneInit() {

	}

	override suspend fun Container.sceneMain() {
		container {

			container {
				xy(50,50)
				var xPos = 50.0
				for(square in squares) {
					val colour = getRGBA(square.colour)
					roundRect(50.0,50.0,0.0, 0.0, colour) {
						xy(xPos,100.0)
						text(square.label, color = Colors.LIGHTGRAY, textSize = 16.0) {
							xy(xPos,100.0)
						}
						onClick {
							val newColour = getRandomColour()
							println("Setting colour from ${square.colour} to $newColour")
							this.color = getRGBA(newColour)
							square.colour = newColour
						}
					}
					xPos += 50.0
				}

				var textYPos = 200.0
				for(square in squares) {
					text(square.toString(), color = Colors.WHITE, textSize = 16.0) {
						xy(50.0, textYPos)
						textYPos += 20.0
						addUpdater {
							this.text = square.colour
						}
					}
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

	private fun getRGBA(colorName: String): RGBA {
		val colour = when (colorName) {
			"RED" -> Colors.RED
			"BLUE" -> Colors.BLUE
			"GREEN" -> Colors.GREEN
			else ->
				Colors.WHITE
		}
		return colour
	}
}

data class Square(val label: String, var colour: String)
