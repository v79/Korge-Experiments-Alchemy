package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.animate.animate
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.input.*
import com.soywiz.korge.input.draggable
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UITextInput
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Point

class DraggingTest : Scene() {

	override suspend fun Container.sceneInit() {
	}

	override suspend fun Container.sceneMain() {

		container {
			text("Circle is draggable{} and updater does collision checks. Not nested.")
			val label = text(text = "White circle is at 120,120") {
				xy(100, 300)
			}
			val collidesLabel = text("") {
				xy(100, 320)
			}

			val source =
				solidRect(100, 100, color = Colors.RED) {
					name = "Source"
					xy(100, 100)
				}

			val target =
				solidRect(100, 100, color = Colors.BLUE) {
					name = "Target"
					xy(300, 100)
				}


			val circle = Circle(radius = 25.0, fill = Colors.WHITE).apply {
				var collidesWith: View? = null
				centerOn(source)
				draggable {
					if (it.end) {
						println("drag end")
						if (!(this.collidesWith(listOf(target)))) {
							println("not on target")
							launchImmediately {
								animate {
									parallel {
										this@apply.moveToWithSpeed(source.x + 25.0, source.y + 25.0, speed = 200.0)
									}
								}
							}
						} else {
							println("colliding with something")
						}
					}
				}
				addUpdater {
					label.text = "Circle is at ${this.pos}"
					if (this.collidesWith(source)) {
						collidesLabel.text = "Source"
					} else if (this.collidesWith(target)) {
						collidesLabel.text = "Target"
					} else {
						collidesLabel.text = ""
					}

				}
			}

			addChild(circle)

		}


		container {
			var circle2 = Circle(radius = 15.0, Colors.ORANGERED)
			var source2 = SolidRect(100, 100, Colors.YELLOW)
			var target2 = SolidRect(100, 100, Colors.PURPLE)

			addChild(source2)
			addChild(target2)

			xy(100, 400)
			text("Circle has custom myDrag{} component. Not nested.")
			val label2 = text(text = "Yellow circle is at...") {
				xy(0, 200)
			}
			val collidesLabel2 = text("") {
				xy(0, 220)
			}
			source2.apply {
				xy(50, 50) // relative to parent container
				name = "source2"
				val sourceContainer = Container()
				circle2.apply {
					centerOn(source2)
					addUpdater {
						label2.text = "Yellow circle is at ${this.pos}"
					}

					mouse {
//					onMouseDrag {
//						println(this@mouse)
//					}
						myDrag {
							if (it.end) {
								if (!circle2.collidesWith(target2)) {
								} else {
									println(target2.isContainer)
									println("Hit!")
								}
							}
						}
//					draggable {
//						println("draggable")
//					}
					}
				}
				addChild(circle2)
			}

			target2.apply {
				xy(350, 50) // relative
				name = "target2"
				val targetContainer = Container()
			}

		}

		container {
			xy(500, 100)
			text("Circle is nested inside box1. Is draggable{}.")
			container {
				xy(0,30)
				name = "box1"
				val box1 = solidRect(50,50, Colors.PALEGOLDENROD)
				circle(15.0, Colors.RED) {
					centerOn(box1)
					draggable {
					}
				}
			}
			container {
				xy(100,30)
				name = "box2"
				val box2 = solidRect(50,50, Colors.PALETURQUOISE)
			}
			text("But because the second box is drawn after the circle, it's always on top.\nThe only way I can think of doing it is having another container, drawn absolutely last, which follows the mouse pointer.\nThen when a drag starts, the circle is deleted from the yellow box, added to the pointer container,\nthen when the mouse is released, create a new circle in the blue box.") {
				xy(0,120)
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


fun <T : View> T.myDrag(selector: View = this, autoMove: Boolean = true, onDrag: ((DraggableInfo) -> Unit)? = null): T {
	val view = this
	val info = DraggableInfo(view)
	selector.onMouseDrag(info = info) {
		if (info.start) {
			info.viewStartXY.copyFrom(view.pos)
		}
		//println("localDXY=${info.localDX(view)},${info.localDY(view)}")
		info.viewPrevXY.copyFrom(view.pos)
		info.viewNextXY.setTo(info.viewStartX + info.localDX(view), info.viewStartY + info.localDY(view))
		info.viewDeltaXY.setTo(info.viewNextX - info.viewPrevX, info.viewNextY - info.viewPrevY)
		if (autoMove) {
			view.xy(info.viewNextXY)
		}
		onDrag?.invoke(info)
		if (info.end) {
			println("Drag ended")
		}
//		println("DRAG: ${info.dx}, ${info.dy}, ${info.start}, ${info.end}")
	}
	return this
}
