package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korev.Key
import com.soywiz.korge.debug.uiCollapsibleSection
import com.soywiz.korge.debug.uiEditableValue
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.degrees
import com.soywiz.korui.UiContainer

class DirectFontTests : Scene() {

    lateinit var sword: Bitmap
    lateinit var overflowAnimation: SpriteAnimation
    override suspend fun Container.sceneInit() {
        val swordSpriteMap: Bitmap = resourcesVfs["sword.png"].readBitmap()
//        val swordanim = SpriteAnimation(spriteMap = swordSpriteMap,
//        spriteWidth = 32,
//        spriteHeight = 32,
//        columns = 1,
//        rows = 1)
        sword = swordSpriteMap

        val flaskBmp = resourcesVfs["overflow_flask.png"].readBitmap()
        overflowAnimation = SpriteAnimation(
            flaskBmp,
            spriteWidth = 20,
            spriteHeight = 18,
            marginTop = 0,
            marginLeft = 0,
            columns = 4
        )
        println(overflowAnimation.sprites.size)
    }

    override suspend fun Container.sceneMain() {

        val fontNamesToFile: Map<String, VfsFile> = SystemFont.listFontNamesWithFiles()
        val oxaniumRegular = localVfs(fontNamesToFile["Oxanium Regular"]!!.path).readTtfFont()
        val oxaniumBold = localVfs(fontNamesToFile["Oxanium Bold"]!!.path).readTtfFont()
        val start = 300.0


        container {
            val message = listOf(
                Message.MsgString("Once upon a time there was a"),
                Message.MsgItem("handsome", Message.MsgItemType.LEGENDARY, sword),
                Message.MsgString("prince, who longed for the love of another man. But his courtiers only found him"),
                Message.MsgItem("pretty ladies", Message.MsgItemType.COMMON),
                Message.MsgString(", which didn't interest him at all."),
                Message.MsgString("One spring morning, he set out on his trusty steed"),
                Message.MsgItem("Tinto", Message.MsgItemType.RARE, sword),
                Message.MsgString("to search for himself.")
            )
            text(text = message.map { it.text }.joinToString(" "), textSize = 24.0, font = DefaultTtfFont) {
                xy(100, 270)
            }
//            roundRect(400.0, 300.0, 0.3, 0.3, fill = Colors.BLACK, stroke = Colors["#d0cec7"], strokeThickness = 4.0) {
//                xy(100, 300)
            paragraph(
                message,
                textSize = 24.0,
                color = Colors.BLANCHEDALMOND,
                maxWidth = 400.0,
                font = DefaultTtfFont,
                maxHeight = 125.0,
                overflowSpriteAnimation = overflowAnimation
            ) {
                xy(100, 300)
            }
//            }

            val message2 = listOf(
                Message.MsgString("Overflowing: Deprecated Gradle features were used in this build, "),
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
                font = oxaniumBold,
                maxHeight = 50.0,
                overflowSpriteAnimation = overflowAnimation
            ) {
                xy(550, 300)
            }
            println("lines: ${p.lineCount}, overflow: ${p.overflowing}, height: ${p.height}, maxHeight: 50.0, pages; ${p.pageCount}")
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

sealed class Message(val text: String) {
    class MsgString(text: String) : Message(text) {
        override fun toString(): String {
            return text
        }
    }

    class MsgItem(text: String, val type: MsgItemType, val sprite: Bitmap? = null) : Message(text) {
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
    maxHeight: Double = 0.0,
    overflowSpriteAnimation: SpriteAnimation,
    block: @ViewDslMarker Paragraph.() -> Unit = {}
): Paragraph = Paragraph(
    para, textSize, color, font, maxWidth, maxHeight, overflowSpriteAnimation
).addTo(this, block)

class Paragraph(
    private val messageItems: List<Message>,
    textSize: Double = 24.0,
    color: RGBA = Colors.WHITE,
    font: Font = DefaultTtfFont,
    maxWidth: Double = 100.0,
    maxHeight: Double = 0.0,
    overflowSpriteAnimation: SpriteAnimation
) : Container(), ViewLeaf, IText {

    override var text: String = (messageItems.map { it.text }).joinToString("")
    var lineCount: Int = 1; private set
    var overflowing: Boolean = false; private set

    private var currentX: Double = 0.0
    private var currentY: Double = 0.0

    private val spaceWidth = font.getGlyphMetrics(textSize, ' '.code).xadvance

    val pages = mutableListOf<Container>()
    var currentPage = 0

    val pageCount: Int
        get() = pages.size

    init {
        var pageContainer = Container()
        pageContainer.name = "Page 0"
        val overflowContainer = Container()
        overflowContainer.name = "Overflow"
//        pages.add(pageContainer)
        val tokens = tokenise()
        var pageCounter = 0
        for (word in tokens) {
            val wordMetrics = font.measureTextGlyphs(textSize, word.text)
            if (wordMetrics.metrics.width + currentX > maxWidth) {
                lineCount++
                currentX = 0.0
                currentY += wordMetrics.fmetrics.lineHeight
            }
            if (maxHeight > 0.0 && (currentY + wordMetrics.metrics.height) >= maxHeight) {
                overflowing = true
                pages.add(pageContainer)
                pageCounter++
                pageContainer = Container() // new container
                pageContainer.name = "Page $pageCounter"
                // reset positions
                currentX = 0.0
                currentY = 0.0
            }
            val wordBmp = font.renderTextToBitmap(
                size = textSize,
                text = word.text,
                paint = if (word is Message.MsgItem) {
                    word.type.color
                } else color,
                drawBorder = true
            ).bmp
            if (word is Message.MsgItem && word.sprite != null) {
                pageContainer.sprite(word.sprite) {
                    xy(currentX, currentY)
                }
                currentX += word.sprite.width + 8.0
            }
            pageContainer.image(wordBmp) {
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
            currentX += if (word is Message.MsgString) {
                wordMetrics.metrics.width + (spaceWidth * 1.5)
            } else {
                wordMetrics.metrics.width + spaceWidth
            } // seem to need a bigger spacing for clarity
        }
        pages.add(pageContainer)

        pages.forEach { it.visible = false } // hide them all
        this.addChildren(pages)
        this.addChild(overflowContainer)

        pages[0].visible = true
        if (overflowing) {
            overflowContainer.sprite(overflowSpriteAnimation) {
                playAnimationLooped(spriteDisplayTime = 0.5.seconds)
//            overflowContainer.image(font.renderTextToBitmap(textSize, "/", Colors.WHITE).bmp) {
                xy(maxWidth - textSize, maxHeight - textSize)
                onClick {
                    nextPage(overflowContainer)
                }
                keys {
                    up(Key.SPACE) {
                        nextPage(overflowContainer)
                    }
                }
            }
        }

        for (page in pages) {
            println(page)
        }
    }

    /**
     * Show the next page
     */
    private fun nextPage(overflowContainer: Container) {
        if (currentPage < pageCount - 1) {
            pages[currentPage].visible = false
            currentPage++
            pages[currentPage].visible = true
        }
        if (currentPage >= (pageCount - 1)) {
            overflowContainer.visible = false
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

typealias ParagraphPage = Bitmap