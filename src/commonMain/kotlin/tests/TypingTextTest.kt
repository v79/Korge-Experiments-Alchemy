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
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.font.TtfFont
import com.soywiz.korim.font.renderTextToBitmap
import com.soywiz.korim.vector.rasterizer.RasterizerCallback

class TypingTextTest : Scene() {

	val shortSentence =
		"It is a truth universally acknowledged,\nthat a single man in possession of a good fortune,\nmust be in want of a wife."

	override suspend fun Container.sceneInit() {
	}

	override suspend fun Container.sceneMain() {

		container {
			solidRect(400.0, 400.0, color = Colors.PALEGOLDENROD) {
				centerOnStage()
//				typingText(text = shortSentence)
				val typeMe = Text(text = shortSentence, textSize = 18.0, color = Colors.BLACK)
				typeMe.xy(450,200)
				addChild(typeMe)
			}
		}

		uiButton(text = "Return to main menu") {
			xy(sceneContainer.width - 50, sceneContainer.height - 25)
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

inline fun Container.typingText(
	text: String,
	callback: @ViewDslMarker (TypingText.() -> Unit) = {},
) = TypingText(text = text).addTo(this, callback)

class TypingText(override var text: String) : Container(), ViewLeaf, IText {

	init {
		val tokens = text.split(" ")
		for (word in tokens) {
			val wordBmp = DefaultTtfFont.renderTextToBitmap(14.0, word, paint = Colors.BLACK).bmp
			this.addChild(sprite(wordBmp))
		}
	}
}
