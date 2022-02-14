package tests

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import goHomeButton

class LayoutTest : Scene() {
	override suspend fun Container.sceneInit() {
	}

	override suspend fun Container.sceneMain() {

		container {

			text(text = "Horizontal Spread", textSize = 14.0, color = Colors.WHITE) {
				xy(10.0, 10.0)
			}

			text(text = "Vertical overflow", textSize = 14.0, color = Colors.WHITE) {
				xy(50.0, 25.0)
			}
			solidRect(200.0, 200.0, color = Colors.WHITE) {
				xy(50.0, 50.0)
			}
			flowContainer(
				maxWidth = 200.0,
				maxHeight = 200.0,
				minPadding = 5.0,
				cropOnOverflow = false,
				direction = FlowLayout.Vertical
			) {
				xy(50.0, 50.0)
				solidRect(50.0, 50.0, color = Colors.BLUE)
				solidRect(50.0, 50.0, color = Colors.RED)
				solidRect(50.0, 50.0, color = Colors.GREEN)
				solidRect(50.0, 50.0, color = Colors.YELLOW)
				solidRect(50.0, 50.0, color = Colors.PURPLE)
			}

			text(text = "Horizontal crop", textSize = 14.0, color = Colors.WHITE) {
				xy(350.0, 25.0)
			}
			solidRect(200.0, 200.0, color = Colors.WHITE) {
				xy(350.0, 50.0)
			}
			flowContainer(
				maxWidth = 200.0,
				maxHeight = 200.0,
				minPadding = 5.0,
				cropOnOverflow = true,
				direction = FlowLayout.Horizontal
			) {
				xy(350.0, 50.0)
				solidRect(50.0, 50.0, color = Colors.BLUE)
				solidRect(50.0, 50.0, color = Colors.RED)
				solidRect(50.0, 50.0, color = Colors.GREEN)
				solidRect(50.0, 50.0, color = Colors.YELLOW)
				solidRect(50.0, 50.0, color = Colors.PURPLE)
			}

			goHomeButton(sceneContainer)
		}
	}
}

inline fun Container.flowContainer(
	maxWidth: Double,
	maxHeight: Double,
	minPadding: Double,
	cropOnOverflow: Boolean,
	direction: FlowLayout = FlowLayout.Horizontal,
	callback: @ViewDslMarker (FlowContainer.() -> Unit) = {}
) = FlowContainer(maxWidth, maxHeight, minPadding, cropOnOverflow = cropOnOverflow, direction = direction).addTo(
	this,
	callback
).layout()

class FlowContainer(
	val maxWidth: Double = 200.0,
	val maxHeight: Double = 200.0,
	val minPadding: Double = 2.0,
	val cropOnOverflow: Boolean = false,
	val direction: FlowLayout = FlowLayout.Horizontal
) :
	Container() {

	fun layout() {
		val maxDimension = if (direction == FlowLayout.Vertical) {
			maxHeight
		} else {
			maxWidth
		}
		var stopRendering = false
		var xPos: Double = 0.0
		var yPos: Double = 0.0
		forEachChild {
			if (stopRendering) {
				it.visible = false
			} else {
				it.xy(xPos + minPadding, yPos + minPadding)
			}
			if (direction == FlowLayout.Vertical) {
				yPos += it.height + minPadding
			} else {
				xPos += it.width + minPadding
			}
			when (direction) {
				FlowLayout.Vertical -> {
					if (yPos >= maxHeight) {
						if (cropOnOverflow) {
							stopRendering = true
						} else {
							xPos += it.width + minPadding
							yPos = 0.0
						}
					}
				}
				FlowLayout.Horizontal -> {
					if (xPos >= maxWidth) {
						if (cropOnOverflow) {
							stopRendering = true
						} else {
							xPos += it.height + minPadding
							yPos = 0.0
						}
					}
				}
			}
		}
	}
}

enum class FlowLayout {
	Vertical,
	Horizontal;
}
