package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.klock.timesPerSecond
import com.soywiz.korge.debug.uiCollapsibleSection
import com.soywiz.korge.debug.uiEditableValue
import com.soywiz.korge.html.Html
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalFill
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.*
import com.soywiz.korim.text.TextRenderer
import com.soywiz.korim.text.TextRendererActions
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korui.UiContainer
import goHomeButton

// TODO: I need to know if the text overflew its container's height, as defined by the wrapHeight property
class Test1 : Scene() {

    val shortText = "Fly me to the moon"
    val newlineText = """
        Fly me to the moon
        Let me play among the stars
        And let me see what spring is like
        On a-Jupiter and Mars
        In other words, hold my hand
        In other words, baby, kiss me.
    """.trimIndent()
    val longFlowText =
        "Fly me to the moon. Let me play among the stars.\nAnd let me see what spring is like.\nOn a-Jupiter and Mars. In other words, hold my hand. In other words, baby, kiss me."
    val willOverflowText =
        "One way of animation in KorGE is to just make a loop and place a delay. This method allows you to define complex logic inside the loop and define state machines just by code. (Have in mind that this approach is likely to have some kind of stuttering.) One way of animation in KorGE is to just make a loop and place a delay. This method allows you to define complex logic inside the loop and define state machines just by code. (Have in mind that this approach is likely to have some kind of stuttering.) One way of animation in KorGE is to just make a loop and place a delay. This method allows you to define complex logic inside the loop and define state machines just by code. (Have in mind that this approach is likely to have some kind of stuttering.)"

    private val fontNamesToFile: MutableMap<String, VfsFile> = mutableMapOf()

    override suspend fun Container.sceneInit() {
        val fnames = SystemFont.listFontNamesWithFiles()
        fontNamesToFile.putAll(fnames)
    }

    override suspend fun Container.sceneMain() {
        var textString: String = shortText
        var textHeight: Double = 24.0
        val fontChoice = localVfs(fontNamesToFile["Segoe Script"]!!.path).readTtfFont()

        container {
            solidRect(stage!!.width, stage!!.height, color = Colors.ANTIQUEWHITE)
            centerOnStage()
            text( text = "Wrapping Text", textSize = 40.0, color = Colors.BLACK) {
                centerXOnStage()
            }
            roundRect(300.0, 200.0, rx = 0.3, ry = 0.3, fill = Colors.LIGHTGRAY) {
                centerOnStage()
                uiHorizontalFill(width = 300.0) {
                    alignBottomToTopOf(this@roundRect)
                    centerXOnStage()
                    uiButton(text = "Short") {
                        onClick {
                            textString = shortText
                        }
                    }
                    uiButton(text = "Newlines") {
                        onClick {
                            textString = newlineText
                        }
                    }
                    uiButton(text = "Long Flow") {
                        onClick {
                            textString = longFlowText
                        }
                    }
                    uiButton(text = "Overflow") {
                        onClick {
                            textString = willOverflowText
                        }
                    }
                }
                wrappingText(
                    textString,
                    textSize = textHeight,
                    color = Colors.BLACK,
                    maxTextHeight = 200.0,
                    overflow = WrappingText.Overflow.NONE,
                    alignment = WrappingText.Alignment.LEFT,
                    font = fontChoice
                ) {
                    alignLeftToLeftOf(this@roundRect, 2.0)
                    alignTopToTopOf(this@roundRect, 2.0)
                    addUpdater {
                        this.text = textString
                        this.textSize = textHeight
                    }
                }

                text(text = "<b>Bold</b> or <strong>Strong<strong>", color = Colors.BLACK) {
                    setHtml("<b>Bold</b> or <strong>Strong<strong>")
                    alignTopToBottomOf(this@roundRect)
                    centerXOnStage()
                }
                text(text = "<i>Italic</i> or <em>Emphasis<em>", color = Colors.BLACK) {
                    setFormat(Html.Format())
                    setHtml( "<i>Italic</i> or <em>Emphasis<em>")
                    alignTopToBottomOf(this@roundRect, padding = 40.0)
                    centerXOnStage()
                }
            }

            goHomeButton(sceneContainer)
        }
    }
}


/** Based on https://forum.korge.org/topic/40/text-wrapping/3 */
/** https://github.com/andstatus/game2048/blob/master/src/commonMain/kotlin/org/andstatus/game2048/view/WrappableText.kt **/
inline fun Container.wrappingText(
    text: String,
    textSize: Double = 16.0,
    alignment: WrappingText.Alignment = WrappingText.Alignment.LEFT,
    color: RGBA = Colors.WHITE,
    overflow: WrappingText.Overflow = WrappingText.Overflow.NONE,
    maxTextHeight: Double = this.height,
    font: TtfFont,
    callback: @ViewDslMarker (WrappingText.() -> Unit) = {},
) = WrappingText(
    text = text,
    textSize = textSize,
    alignment = alignment,
    color = color,
    wrapWidth = 280.0,
    maxTextHeight = maxTextHeight,
    overflow = overflow,
    font = font
).addTo(this, callback)

/**
 * A text display component which can wrap text across multiple lines
 * @param text the text to display. Embedded newlines (\n) will be respected
 * @param textSize the font size
 * @param alignment one of [WrappingText.Alignment]
 * @param color text color
 * @param wrapWidth the width of the text, after which it will wrap onto the next line
 * @param maxTextHeight the maximum height of the text block; ignored if [WrappingText.Overflow.OVERFLOW]
 * @param overflow controls whether to crop the text at maxTextHeight or not
 */
class WrappingText(
    text: String,
    textSize: Double = 16.0,
    alignment: Alignment = Alignment.LEFT,
    color: RGBA = Colors.WHITE,
    wrapWidth: Double = 200.0,
    maxTextHeight: Double = 200.0,
    overflow: Overflow = Overflow.OVERFLOW,
    font: TtfFont = DefaultTtfFont
) : Text(
    text = text,
    textSize = textSize,
    color = color,
    font = font,
    renderer = WrappingTextRenderer(
        wrapWidth = wrapWidth,
        maxTextHeight = maxTextHeight,
        overflow = overflow,
        alignment = alignment
    )
) {

    init {
        val f = this.font.getOrNull()

        if (f is Font) {
            val metrics = f.getTextBounds(textSize, text, renderer = renderer)
            metrics.bounds.x = 0.0
            metrics.bounds.y = 0.0
            metrics.bounds.width = wrapWidth
            setTextBounds(metrics.bounds)
        }
    }

    /**
     * Alignment for text component, LEFT, CENTER or RIGHT
     */
    enum class Alignment {
        LEFT, CENTER, RIGHT
    }

    /**
     * Text will be allowed to extend beyond the maxTextHight if OVERFLOW is set
     */
    enum class Overflow {
        OVERFLOW, NONE
    }

}

/**
 * A custom text renderer which can wrap text across multiple lines
 * @param wrapWidth the width after which text should wrap
 * @param maxTextHeight the maximum height of the text block; ignored if [WrappingText.Overflow.OVERFLOW]
 * @param overflow controls whether to crop the text at maxTextHeight or not
 * @param alignment one of [WrappingText.Alignment]
 */
class WrappingTextRenderer(
    val wrapWidth: Double,
    val maxTextHeight: Double,
    val overflow: WrappingText.Overflow = WrappingText.Overflow.OVERFLOW,
    val alignment: WrappingText.Alignment = WrappingText.Alignment.LEFT
) :
    TextRenderer<String> {

    override fun TextRendererActions.run(text: String, size: Double, defaultFont: Font) {
        val SPACE = ' '
        val lines = mutableListOf(Line())

        reset()
        setFont(defaultFont, size)

        val spaceWidth = getGlyphMetrics(SPACE.code).xadvance + getKerning(
            SPACE.code,
            'A'.code
        ) // get the width of a ' ' space character, assuming it's kerned aside an 'A'

        // split the text into paragraphs up by existing line breaks
        splitIntoLines(text, SPACE, spaceWidth, lines) // end of splitting into words and lines of words

        // we've got all our lines. Render them
        var currentMaxY = 0.0
        lineloop@ for (line in lines) {
            var start = when (alignment) {
                WrappingText.Alignment.LEFT -> 0.0
                WrappingText.Alignment.CENTER -> (wrapWidth - line.calculateWidth(spaceWidth)) / 2
                WrappingText.Alignment.RIGHT -> wrapWidth - line.calculateWidth(spaceWidth)
            }
            if (overflow == WrappingText.Overflow.NONE && (currentMaxY + lineHeight) >= maxTextHeight) {
                break@lineloop
            }
            for (word in line.words) {
                x = start

                for (character in word.text.indices) {
                    val code = word.text[character].code
                    val nextCode = word.text.getOrElse(character + 1) { '\u0000' }.code
                    val glyph = getGlyphMetrics(code)
                    transform.identity()
                    val advanceX = glyph.xadvance + getKerning(code, nextCode)
                    put(code)
                    advance(advanceX)
                }
                start += word.width + spaceWidth
            }
            newLine(lineHeight)

            currentMaxY += lineHeight
        }
        put(0) // terminate
    }

    private fun TextRendererActions.splitIntoLines(
        text: String,
        SPACE: Char,
        spaceWidth: Double,
        lines: MutableList<Line>
    ) {
        for (wrapped in text.split('\n')) {
            var currentX = 0.0
            // split into words
            for (word in wrapped.split(SPACE)) {
                var wordWidth = 0.0
                var currentWord = ""

                // split the word into characters
                for (char: Int in word.indices) {
                    val code = word[char].code

                    val nextCode =
                        word.getOrElse(char + 1) { '\u0000' }.code  // get next character for kerning purposes

                    val glyph = getGlyphMetrics(code)
                    val kerning = getKerning(code, nextCode)
                    val charWidth = glyph.xadvance + kerning

                    if (wordWidth + charWidth + spaceWidth > wrapWidth) {
                        // wrapping inside word // if needed?
                        val thisWord = Word(currentWord, wordWidth)
                        if (lines.last().words.isEmpty()) {
                            lines.last().words.add(thisWord)
                        } else {
                            lines.add(Line(mutableListOf(thisWord)))
                        }
                        currentX = 0.0 // reset as we're on a new line
                        wordWidth = 0.0
                        currentWord = ""
                        lines.add(Line())
                    }
                    // next character in this word
                    wordWidth += charWidth
                    currentWord += code.toChar()
                }

                currentX += wordWidth + spaceWidth

                if (currentX > wrapWidth) {
                    lines.add(Line())
                    currentX = wordWidth + spaceWidth
                }

                // add the word to the line
                lines.last().words.add(Word(currentWord, wordWidth))
            }
            lines.add(Line())
        }
    }

    data class Word(val text: String, val width: Double)
    data class Line(val words: MutableList<Word> = mutableListOf()) {
        fun calculateWidth(spaceWidth: Double): Double {
            return words.sumOf { it.width } + (words.size - 1) * spaceWidth
        }
    }
}
