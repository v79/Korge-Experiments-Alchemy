package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korge.debug.uiCollapsibleSection
import com.soywiz.korge.debug.uiEditableValue
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.BlurFilter
import com.soywiz.korge.view.filter.ColorMatrixFilter
import com.soywiz.korge.view.filter.ShaderFilter
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.*
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.radians
import com.soywiz.korte.Filter
import com.soywiz.korui.UiContainer

class DirectFontTests : Scene() {
    override suspend fun Container.sceneInit() {

    }

    override suspend fun Container.sceneMain() {

        val fontNamesToFile: Map<String, VfsFile> = SystemFont.listFontNamesWithFiles()
        val oxaniumRegular = localVfs(fontNamesToFile["Oxanium Regular"]!!.path).readTtfFont()
        val oxaniumBold = localVfs(fontNamesToFile["Oxanium Bold"]!!.path).readTtfFont()
        val start = 300.0

        /* container {
             visible = false
             *//*  val b32 = Bitmap32Context2d(200, 200) {
                  oxaniumRegular.drawText(
                      ctx = this,
                      size = 48.0,
                      text = "Font.drawText call filled",
                      x = 200.0,
                      y = 200.0,
                      paint = Colors.ORANGE,
                      fill = true
                  )
              }
              image(b32)*//*
            val rttbMetrics = oxaniumRegular.measureTextGlyphs(size = 48.0, text = "renderTextToBitmap")
            println(rttbMetrics.metrics.bounds)
            val rttb = oxaniumRegular.renderTextToBitmap(
                size = 48.0,
                text = "renderTextToBitmap",
                paint = Colors.PALEVIOLETRED
            )
            image(rttb.bmp) {
                xy(start, start)
            }
            val smaller = oxaniumRegular.renderTextToBitmap(24.0, text = "Smaller after rrtb", paint = Colors.LIGHTBLUE)
            image(smaller.bmp) {
                xy(start + rttbMetrics.metrics.width, start + smaller.metrics.height)
            }

        }*/
        container {
            val message = listOf(
                Message.MsgString("Once upon a time there was a"),
                Message.MsgItem("handsome", Message.MsgItemType.LEGENDARY),
                Message.MsgString("prince, who longed for the love of another man. But his courtiers only found him"),
                Message.MsgItem("pretty ladies", Message.MsgItemType.COMMON),
                Message.MsgString(", which didn't interest him at all.")
            )
            text(text = message.map { it.text }.joinToString(" "), textSize = 24.0, font = oxaniumBold) {
                xy(100, 270)
            }
//            roundRect(400.0, 300.0, 0.3, 0.3, fill = Colors.BLACK, stroke = Colors["#d0cec7"], strokeThickness = 4.0) {
//                xy(100, 300)
            paragraph(
                message,
                textSize = 24.0,
                color = Colors.BLANCHEDALMOND,
                maxWidth = 400.0,
                font = DefaultTtfFont
            ) {
                xy(100, 300)
            }
//            }

            val message2 = listOf(
                Message.MsgString("Deprecated Gradle features were used in this build, "),
                Message.MsgItem("making it incompatible", Message.MsgItemType.RARE),
                Message.MsgString("You can use '--warning-mode all' to show"),
                Message.MsgItem("the individual deprecation", Message.MsgItemType.COMMON),
                Message.MsgString("warnings and determine if they come from your own scripts or plugins.")
            )
            text(text = message2.map { it.text }.joinToString(" "), textSize = 24.0, font = oxaniumBold) {
                xy(100, 550)
            }
            val p = paragraph(
                message2,
                textSize = 18.0,
                color = Colors.BLANCHEDALMOND,
                maxWidth = 500.0,
                font = oxaniumBold
            ) {
                xy(550, 300)
            }
        }

        /*container {
            visible = false
            val text1 = "Thank you, adventurer. Please take this "
            val item = "[ring of greater power]"
            val text2 = " and "
            val item2 = "[23 gold]"
            val text3 = " for your troubles."
            val textSize = 24.0

            var xStart = start
            var yStart = start + 50
            val textList = listOf(text1, item, text2, item2, text3)
            val endOfLine = 700.0

            for (thing in textList) {
                var isItem = thing.startsWith("[")
                val thingMetrics = oxaniumRegular.measureTextGlyphs(size = textSize, text = thing)
                val thingBmp = oxaniumRegular.renderTextToBitmap(
                    size = textSize,
                    text = thing,
                    paint = if (isItem) Colors.PINK else Colors.WHITE
                ).bmp
                if (thingMetrics.metrics.width + xStart > endOfLine) {
                    xStart = start
                    yStart += 40.0
                }
                image(thingBmp) {
                    xy(xStart, yStart)
                    onClick {
                        if (isItem) {
                            println("Clicked on item: $thing")
                        }
                    }
                }
                xStart += thingMetrics.metrics.width
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

sealed class Message(val text: String) {
    class MsgString(text: String) : Message(text) {
        override fun toString(): String {
            return text
        }
    }

    class MsgItem(text: String, val type: MsgItemType) : Message(text) {
        override fun toString(): String {
            return "[$text ($type)]"
        }
    }

    enum class MsgItemType(val color: RGBA) {
        COMMON(Colors.DARKSLATEGREY),
        RARE(Colors.GREEN),
        LEGENDARY(Colors.PURPLE)
    }
}

fun Container.paragraph(
    para: List<Message>,
    textSize: Double = 24.0,
    color: RGBA = Colors.WHITE,
    font: Font = DefaultTtfFont,
    maxWidth: Double = 100.0,
    block: @ViewDslMarker Paragraph.() -> Unit = {}
): Paragraph = Paragraph(
    para, textSize, color, font, maxWidth
).addTo(this, block)

class Paragraph(
    private val messageItems: List<Message>,
    textSize: Double = 24.0,
    color: RGBA = Colors.WHITE,
    font: Font = DefaultTtfFont,
    maxWidth: Double = 100.0
) : Container(), ViewLeaf, IText {

    override var text: String = (messageItems.map { it.text }).joinToString("")
    var lineCount: Int = 1; private set

    var currentX: Double = 0.0
    var currentY: Double = 0.0

    private val spaceWidth = font.getGlyphMetrics(textSize, ' '.code).xadvance

    init {
        val tokens = tokenise()

        for (word in tokens) {
            val wordMetrics = font.measureTextGlyphs(textSize, word.text)
            val wordBmp = font.renderTextToBitmap(
                size = textSize,
                text = word.text,
                paint = if (word is Message.MsgItem) {
                    word.type.color
                } else color,
                drawBorder = true
            ).bmp
            if (wordMetrics.metrics.width + currentX > maxWidth) {
                lineCount++
                currentX = 0.0
                currentY += wordMetrics.fmetrics.lineHeight
            }
            image(wordBmp) {
                xy(currentX, currentY)
                if (word is Message.MsgItem) {
                    onOver {
                        this.skewX = 15.degrees
                    }
                    onOut {
                        this.skewX = 0.degrees
                    }
                }
            }
            currentX += if(word is Message.MsgString ) {wordMetrics.metrics.width + (spaceWidth * 1.5) } else { wordMetrics.metrics.width + spaceWidth } // seem to need a bigger spacing for clarity
        }
    }

    // split it into words and items. Items are never split regardless of their length?
    private fun tokenise(): List<Message> {
        val tokens: MutableList<Message> = mutableListOf()
        for (item in messageItems) {
            val words: List<Message> = when (item) {
                is Message.MsgString -> {
                    item.text.split(" ").map { Message.MsgString(it) }
                }
                is Message.MsgItem -> {
                    listOf(item)
                }
            }
            tokens.addAll(words)
        }
        return tokens
    }

    override fun buildDebugComponent(views: Views, container: UiContainer) {
        container.uiCollapsibleSection("Paragraph") {
            uiEditableValue(::text)
        }
        super.buildDebugComponent(views, container)
    }
}