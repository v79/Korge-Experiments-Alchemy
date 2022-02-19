package tests

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiComboBox
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korge.bus.SyncBus
import com.soywiz.korge.ui.uiCheckBox
import goHomeButton
import kotlin.math.sign


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
				solidRect(50.0, 50.0, color = Colors.BLUE).apply { name = "blue" }
				solidRect(50.0, 50.0, color = Colors.RED).apply { name = "red" }
				container {
					name = "green container"
					solidRect(50.0, 50.0, color = Colors.GREEN)
					text("3", color = Colors.BLACK).apply { name = "black" }
				}
				solidRect(50.0, 50.0, color = Colors.YELLOW).apply { name = "yellow" }
//				repeat(6) {
				solidRect(50.0, 50.0, color = Colors.PURPLE).apply { name = "purple1" }
				solidRect(50.0, 50.0, color = Colors.PURPLE).apply { name = "purple2" }
				solidRect(50.0, 50.0, color = Colors.PURPLE).apply { name = "purple3" }
				solidRect(50.0, 50.0, color = Colors.PURPLE).apply { name = "purple4" }
				solidRect(50.0, 50.0, color = Colors.PURPLE).apply { name = "purple5" }
//				solidRect(50.0, 50.0, color = Colors.PURPLE).apply { name = "purple6" }
//				}
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
				val cropBox = uiCheckBox(checked = false, text = "Crop") {
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
	) {
		override fun toString() =
			"$direction $crop $alignment"
	}

	fun layout(newConfiguration: FlowConfiguration) {
		configuration = newConfiguration

		val rowsOrColumns = mutableListOf<List<View>>() // need to keep track of the x-positions of each row

		val xStart = when (configuration.alignment) {
			FlowAlignment.Left -> {
				0.0
			}
			FlowAlignment.Center -> {
				var sumWidth = 0.0
				var row = mutableListOf<View>()
				var rowCount = 0
				for (child in children) {
					sumWidth += child.width
					println("\tsumWidth: $sumWidth")
					if (sumWidth >= maxWidth) {
						println("add $row to rowsOrColumns, reset")
						rowsOrColumns += row
						row = mutableListOf()
						rowCount++
						println("add $child to row $rowCount")
						row += child
						sumWidth = child.width
					} else {
						println("add $child to row $rowCount")
						row += child
//						sumWidth += child.width
					}
				}
				// and add the final row
				rowsOrColumns += row
				println("sumWidth: $sumWidth -> Centered over ${rowsOrColumns.size + 1} rows")

				val startX = calculateRowStart(rowsOrColumns[0])
				println("first startX = $startX")


				println("---------------- CENTER -------------")
				println("Total rows: ${rowsOrColumns.size}")
				rowsOrColumns.forEachIndexed { index, row ->
					println("Row: $index contains (${row.size}):")
					row.forEachIndexed { rIndex, item ->
						println("\tItem $rIndex = $item")
					}
					val tmpRowStart = calculateRowStart(row)
					println("\tSTART: $tmpRowStart")
				}
				println("---------------- end CENTER ----------")

				startX
			}
			FlowAlignment.Right -> {
				maxWidth - children.first().width - (minPadding * 2) // why?
			}
		}

		println("config: $configuration, xStart = $xStart")

		var stopRendering = false // if we are cropping we may need to stop showing the items
		var xPos = xStart
		var yPos = 0.0
		var rowCounter = 0 // for centering
		forEachChild {
			it.visible = true // reset visibility
			when (configuration.alignment) {
				FlowAlignment.Left -> {
					when (configuration.direction) {
						FlowLayout.Horizontal -> {
							if (xPos >= maxWidth || (it.width + xPos) > maxWidth) {
								if (configuration.crop) {
									stopRendering = true
								} else {
									yPos += it.height + minPadding
									xPos = 0.0
								}
							}
						}
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
					}

				}
				FlowAlignment.Center -> {

					when (configuration.direction) {
						FlowLayout.Horizontal -> {
							println("number of rows: ${rowsOrColumns.size}, at row: $rowCounter")
							if(xPos + it.width >= maxWidth) {
								println("Next row at $it for row $rowCounter?")
								rowCounter++
								yPos += it.width + minPadding
								if(rowCounter <= rowsOrColumns.size) {
									println("Calculate the start for row $rowCounter")
									xPos = calculateRowStart(rowsOrColumns[rowCounter])
								} else {
									println("Error - we've got a rowCounter which exceeds the number of rows ")
								}
							}

						}
						FlowLayout.Vertical -> {

						}
					}

				}
				FlowAlignment.Right -> {
					when (configuration.direction) {
						FlowLayout.Horizontal -> {
							if (xPos <= minPadding) {
								if (configuration.crop) {
									stopRendering = true
								} else {
									yPos += it.height + minPadding
									xPos = xStart
								}
							}
						}
						FlowLayout.Vertical -> {
							if (yPos >= maxHeight || (it.height + yPos) > maxHeight) {
								if (configuration.crop) {
									stopRendering = true
								} else {
									xPos -= it.width + minPadding
									yPos = 0.0
								}
							}
						}
					}
				}
			}

			// draw the damn thing
			if (stopRendering) {
				it.visible = false
			} else {
				println("$it at $xPos,$yPos")
				it.xy((xPos + minPadding), (yPos + minPadding))
			}

			// increment to move on to the next item
			when (configuration.alignment) {
				FlowAlignment.Left -> {
					when (configuration.direction) {
						FlowLayout.Horizontal -> {
							xPos += it.width + minPadding
						}
						FlowLayout.Vertical -> {
							yPos += it.height + minPadding
						}
					}
				}
				FlowAlignment.Right -> {
					when (configuration.direction) {
						FlowLayout.Horizontal -> {
							xPos -= it.width + minPadding
						}
						FlowLayout.Vertical -> {
							yPos += it.height + minPadding
						}
					}
				}
				FlowAlignment.Center -> {
					when (configuration.direction) {
						FlowLayout.Horizontal -> {
							xPos += it.width + minPadding
						}
						FlowLayout.Vertical -> {
							yPos += it.height + minPadding
						}
					}
				}
			}

		}


		/*
		maxWidth = 400
		minPadding = 5

		if(LEFT) {
		xPos = 0
		}
		if(RIGHT) {
		xPos = (maxWidth - width)
		}

		if(CENTER) {
		 sumWidth = 0
		 counter = 0
		 for(c in children) {
		   if(sumWidth >= maxWidth) {
		   break;
		   } else {
		      sumWidth += c.width
		      counter ++
		    }
		    // at this point we have first [counter] items which will fit in the row and the total width is sumWidth
		    xPos = (maxWidth - sumWidth) / 2

		}



		 */


		/*	var stopRendering = false // if we are cropping we may need to stop showing the items
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
			}*/
	}

	private fun calculateRowStart(row: List<View>): Double {
		println("\t\tCalculating xStart for row $row sized ${row.size}")
		val rowWidth = row.sumOf { it.width } + (minPadding * row.size)
		println("\t\trowWidth: $rowWidth")
		println("\t\titemCount: ${row.size}")
		println("\t\tnewXStart: ${(maxWidth - rowWidth) / 2.0}")
		return (maxWidth - rowWidth) / 2.0
	}
}




