package tests

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiComboBox
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korge.bus.SyncBus
import com.soywiz.korge.ui.uiCheckBox
import goHomeButton


class LayoutTest(private val bus: SyncBus) : Scene() {
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
				layout = FlowContainer.FlowLayout.Vertical,
				crop = false,
				alignment = FlowContainer.FlowAlignment.Left
			) {
				xy(50.0, 50.0)
				solidRect(50.0, 50.0, color = Colors.BLUE)
				solidRect(25.0, 25.0, color = Colors.RED)
				circle(40.0, fill = Colors.GREEN)
				solidRect(50.0, 50.0, color = Colors.YELLOW)
				solidRect(15.0, 50.0, color = Colors.PURPLE)
			}



			text(text = "<Variable> <cropped>", textSize = 14.0, color = Colors.WHITE) {
				xy(350.0, 25.0)
				bus.register<FlowContainer.FlowConfiguration> {
					text = "${it.direction} cropped: ${it.crop}"
				}
			}
			solidRect(250.0, 200.0, color = Colors.WHITE) {
				xy(350.0, 50.0)
			}
			flowContainer(
				maxWidth = 250.0,
				maxHeight = 200.0,
				minPadding = 5.0,
				configuration = FlowContainer.FlowConfiguration(
					FlowContainer.FlowLayout.Horizontal,
					true,
					FlowContainer.FlowAlignment.Left
				),
			) {
				xy(350.0, 50.0)
				solidRect(50.0, 50.0, color = Colors.BLUE)
				solidRect(50.0, 50.0, color = Colors.RED)
				container {
					solidRect(50.0, 50.0, color = Colors.GREEN)
					text("3", color = Colors.BLACK)
				}
				solidRect(50.0, 50.0, color = Colors.YELLOW)
				solidRect(50.0, 50.0, color = Colors.PURPLE)

				bus.register<FlowContainer.FlowConfiguration> {
					layout(it)
				}
			}
			container {
				var crop: Boolean = true
				var direction = FlowContainer.FlowLayout.Horizontal
				var alignment = FlowContainer.FlowAlignment.Left
				name = "controls"
				xy(350.0, 300.0)
				val dirText = text(text = "Direction")
				val combo = uiComboBox(
					width = 200.0,
					height = 50.0,
					items = listOf(FlowContainer.FlowLayout.Horizontal, FlowContainer.FlowLayout.Vertical)
				) {
					alignTopToBottomOf(dirText)

					onSelectionUpdate {
						direction = it.selectedItem!!
						bus.send(FlowContainer.FlowConfiguration(direction, crop, alignment))
					}
				}
				val cropBox = uiCheckBox(checked = true, text = "Crop") {
					onChange {
						crop = it.checked
						bus.send(FlowContainer.FlowConfiguration(direction, crop, alignment))
					}
					alignTopToBottomOf(combo)
				}
				val alignCombo = uiComboBox(
					width = 200.0, height = 50.0,
					items = FlowContainer.FlowAlignment.values().toList()
				)
				{
					alignTopToBottomOf(cropBox)
					onSelectionUpdate {
						alignment = it.selectedItem!!
						bus.send(FlowContainer.FlowConfiguration(direction, crop, alignment))
					}
				}
			}

			goHomeButton(sceneContainer)
		}
	}
}

inline fun Container.flowContainer(
	maxWidth: Double,
	maxHeight: Double,
	minPadding: Double,
	configuration: FlowContainer.FlowConfiguration = FlowContainer.FlowConfiguration(),
	callback: @ViewDslMarker (FlowContainer.() -> Unit) = {}
) = FlowContainer(maxWidth, maxHeight, minPadding, configuration).addTo(
	this,
	callback
).layout(configuration)

inline fun Container.flowContainer(
	maxWidth: Double,
	maxHeight: Double,
	minPadding: Double,
	alignment: FlowContainer.FlowAlignment,
	crop: Boolean,
	layout: FlowContainer.FlowLayout,
	callback: @ViewDslMarker (FlowContainer.() -> Unit) = {}
) = FlowContainer(maxWidth, maxHeight, minPadding, FlowContainer.FlowConfiguration(layout, crop, alignment)).addTo(
	this,
	callback
).layout(FlowContainer.FlowConfiguration(layout, crop, alignment))

class FlowContainer(
	val maxWidth: Double = 200.0,
	val maxHeight: Double = 200.0,
	val minPadding: Double = 2.0,
	private var configuration: FlowConfiguration = FlowConfiguration()
) :
	Container() {

	enum class FlowLayout {
		Vertical,
		Horizontal;
	}

	enum class FlowAlignment {
		Left,
		Right,
		Center;
	}

	class FlowConfiguration(
		val direction: FlowLayout = FlowLayout.Horizontal,
		val crop: Boolean = false,
		val alignment: FlowAlignment = FlowAlignment.Left
	)

	fun layout(newConfiguration: FlowConfiguration) {
		configuration = newConfiguration
		var stopRendering = false // if we are cropping we may need to stop showing the items
		var alignmentMultplier = if(configuration.alignment == FlowAlignment.Left) { 1 } else { -1 }
		var xPos: Double = if (configuration.alignment == FlowAlignment.Left) {
			0.0
		} else {
			maxWidth
		}
		var yPos: Double = 0.0
		forEachChild {
			it.visible = true // reset visibility


			when (configuration.direction) {
				FlowLayout.Vertical -> {
					if (yPos >= maxHeight || (it.height + yPos) > maxHeight) {
						if (configuration.crop) {
							stopRendering = true
						} else {
							xPos += it.height + minPadding
							yPos = 0.0
						}
					}
				}
				FlowLayout.Horizontal -> {
					if (configuration.alignment == FlowAlignment.Left && (xPos >= maxWidth || (it.width + xPos) > maxWidth)) {
						if (configuration.crop) {
							stopRendering = true
						} else {
							yPos += it.width + minPadding
							xPos = 0.0
						}
					}
					if (configuration.alignment == FlowAlignment.Right && (xPos >= maxWidth || (it.width + xPos) > maxWidth)) {

					}
				}
			}
			if (stopRendering) {
				it.visible = false
			} else {
				it.xy((xPos + minPadding), yPos + minPadding)
//				it.xy(xPos + minPadding, yPos + minPadding)
			}
			if (configuration.direction == FlowLayout.Vertical) {
				yPos += it.height + minPadding
			} else {
				xPos += it.width + minPadding
			}
		}
	}
}




