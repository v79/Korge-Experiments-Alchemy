package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter

class FollowMousePointerTest : Scene() {

    val rect = RoundRect(25.0,25.0,0.3,0.3)
    override suspend fun Container.sceneInit() {
    }

    override suspend fun Container.sceneMain() {

        rect.apply {
            xy(0.0,0.0)

            // THIS WORKS
            addUpdater {
                x = views.globalMouseX
                y = views.globalMouseY
            }
        }

        container {

            addChild(rect)


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

        /**
         * This is INCREDIBLY SLOW and non responsive
         */
       /* mouse {
            onMove {
                rect.apply {
                    xy(it.currentPosGlobal.x, it.currentPosGlobal.y)
                }
            }
        }*/

    }
}