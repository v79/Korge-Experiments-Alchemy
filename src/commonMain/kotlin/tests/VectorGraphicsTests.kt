package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.klock.timesPerSecond
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.color.Colors
import com.soywiz.korim.paint.Paint
import com.soywiz.korim.vector.Context2d
import com.soywiz.korim.vector.Drawable
import com.soywiz.korim.vector.GraphicsPath
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.plus
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.circle

inline fun Container.outlineSquare(x: Double, y: Double, size: Double, paint: Paint, strokeThickness: Double): Graphics {
    return graphics {
        beginStroke(paint = paint, info = StrokeInfo(thickness = strokeThickness))
        moveTo(x, y)
        lineTo(x + size, y)
        lineTo(lastX,y + size)
        lineTo(x, lastY)
        lineTo(x,y)
        endStroke()
    }
}

class VectorGraphicsTests : Scene() {

    val newSquare: Graphics = Graphics()
    override suspend fun Container.sceneInit() {
       newSquare.apply {
           beginStroke(Colors.BLUE, StrokeInfo(thickness = 2.0))
           moveTo(200.0,200.0)
           lineTo(300.0, 200.0)
           lineTo(300.0, 300.0)
           lineTo(200.0,300.0)
           lineTo(200.0,200.0)
           endStroke()
           name = "new square graphics object"
       }
    }

    override suspend fun Container.sceneMain() {

        container {
            name = "my container"
//            val newSquareBmp = newSquare.renderToBitmap(views)

            addChild(newSquare) // does NOT add twice
            // completely redraw the shape, as a circle this time.
            newSquare.lock {
                newSquare.clear()
                newSquare.beginFill(Colors.YELLOW)
                newSquare.circle(200.0, 200.0, 50.0)
                newSquare.endFill()
            }
//            val cicleBmp = newSquare.renderToBitmap(views)

            solidRect(300.0, 300.0) {
                xy(50.0, 50.0)




                addChild(newSquare) // appears at the drawn coordinates


                newSquare.addUpdater {
//                    rotation +=1.degrees
                    x++
                    if(x > 500) x = 100.0
                }

               /* sprite(newSquareBmp) {
                    name = "newSquareBmp sprite at 600,600 isn't visible"
                    xy(600,600)
                }
                image(newSquareBmp) {
                    name = "newSquareBmp image at 400,400 isn't visible"
                    xy(400,400)
                }*/

                val square: Graphics = graphics {
                    name = "Green square"
                    beginStroke(
                        paint = Colors.GREEN,
                        info = StrokeInfo(thickness = 3.0, startCap = LineCap.ROUND, endCap = LineCap.ROUND)
                    )
                    val start = Point(100.0, 100.0)
                    moveTo(start.x, start.y)
                    lineTo(200.0, 100.0)
                    lineTo(200.0, 200.0)
                    lineTo(100.0, 200.0)
                    lineTo(start.x, start.y)
                    endStroke()
                }
                addFixedUpdater(30.timesPerSecond) {
                    square.x += 1.0
                    if (square.x > 200.0) {
                        square.x = 0.0
                    }
                }
            }

            /*solidRect(200.0, 200.0) {
                xy(250.0, 250.0)
                val outlineSquare = outlineSquare(x = 255.0, y=255.0, 32.0, paint = Colors.RED, strokeThickness = 4.0)

                addFixedUpdater(30.timesPerSecond) {
                    outlineSquare.x += 1.0
                }
            }

            text("Co-ordinates test; are they relative to their container? NO!", color = Colors.GREEN) {
                xy(300.0, 280.0)
            }
            solidRect(100.0,100.0, color = Colors.PINK) {
                xy(300,300)
                    solidRect(50.0, 50.0, color = Colors.PURPLE) {
                        xy(25,25)
                }
            }*/

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