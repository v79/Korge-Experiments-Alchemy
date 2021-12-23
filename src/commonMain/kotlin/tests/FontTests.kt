package tests

import MainMenu
import com.soywiz.kds.doubleArrayListOf
import com.soywiz.klock.seconds
import com.soywiz.klock.timesPerSecond
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalFill
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.bitmap.Bitmaps
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.bitmap.effect.BitmapEffect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.*
import com.soywiz.korim.paint.LinearGradientPaint
import com.soywiz.korim.paint.RadialGradientPaint
import com.soywiz.korim.text.*
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.fullName
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.file.std.rootLocalVfs
import com.soywiz.korio.lang.forEachCodePoint
import com.soywiz.korio.util.niceStr
import com.soywiz.korma.geom.*

class FontTests : Scene() {

    override suspend fun Container.sceneInit() {

    }

    override suspend fun Container.sceneMain() {
        container {

//            val sysFont = SystemFont.getDefaultFont()
//            println("System default font: $sysFont")
//            val sysEmojiFont = SystemFont.getEmojiFont()
//            println("System emoji font: $sysFont")
//            println("--list fonts")
//            val fontNames = SystemFont.listFontNames()
//            for(f in fontNames) {
//                println(f)
//            }

            val fontNamesToFile: Map<String, VfsFile> = SystemFont.listFontNamesWithFiles()
//            for (f in fontNamesToFile) {
//                println("${f.key} - ${f.value.fullName}")
//            }

            val oxaniumRegular = localVfs(fontNamesToFile["Oxanium Regular"]!!.path).readTtfFont()
            val oxaniumBold = localVfs(fontNamesToFile["Oxanium Bold"]!!.path).readTtfFont()
            println(oxaniumRegular)


            text(text = "Fly me to the moon 0123459789 £%$ emoji: ☺", textSize = 30.0, font = oxaniumRegular) {
                xy(50.0, 50.0)
            }
            text(text = "Fly me to the moon\n0123459789 £%$ emoji: ☺", textSize = 30.0, font = oxaniumBold) {
                xy(50.0, 100.0)
            }
            var version = 1

            val oxBitmapFont = BitmapFont(
                oxaniumRegular,
                64.0,
                paint = LinearGradientPaint(0, 0, 0, 50).add(0.0, Colors.CADETBLUE).add(1.0, Colors["#5d8047"]),
                effect = BitmapEffect(
                    dropShadowX = 2,
                    dropShadowY = 2,
                    dropShadowRadius = 2,
                    dropShadowColor = Colors["#5f005f"]
                )
            )

            text("Fly me to the moon!", font = oxBitmapFont, textSize = 64.0, alignment = TextAlignment.BASELINE_LEFT,
                renderer = CreateStringTextRenderer({ version++ }) { text, n, c, c1, g, advance ->
                    transform.identity()
                    val sin = sin(0.degrees + (n * 360 / text.length).degrees)
                    transform.rotate(15.degrees)
                    transform.translate(0.0, sin * 16)
                    transform.scale(1.0, 1.0 + sin * 0.1)
                    put(c)
                    advance(advance)
                }) {
                xy(50, 250)

            }
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
    }
}

fun CreateColorTextRenderer(
    getVersion: () -> Int = { 0 },
    handler: TextRendererActions.(text: String, n: Int, c: Int, c1: Int, g: GlyphMetrics, advance: Double) -> Unit
): TextRenderer<String> = object : TextRenderer<String> {
    override val version: Int get() = getVersion()

    private fun TextRendererActions.runStep(n: Int, c: Int, c1: Int, text: String) {
        if (c == '\n'.code) {
            newLine(lineHeight)
        } else {
            val g = getGlyphMetrics(c)
            transform.identity()
            handler(text, n, c, c1, g, (g.xadvance + getKerning(c, c1)))
        }
    }

    override fun TextRendererActions.run(text: String, size: Double, defaultFont: Font) {
        reset()
        setFont(defaultFont, size)
        var lastCodePoint = -1
        val length = text.forEachCodePoint { index, codePoint, error ->
            if (index > 0) {
                runStep(index - 1, lastCodePoint, codePoint, text)
            }
            lastCodePoint = codePoint
        }
        runStep(length - 1, lastCodePoint, 0, text)
    }
}

class ColorTextRendererActions : TextRendererActions() {
    var verticalAlign: VerticalAlign = VerticalAlign.TOP
    var horizontalAlign: HorizontalAlign = HorizontalAlign.LEFT
    private val arrayTex = arrayListOf<BmpSlice>()
    private val arrayX = doubleArrayListOf()
    private val arrayY = doubleArrayListOf()
    private val arraySX = doubleArrayListOf()
    private val arraySY = doubleArrayListOf()
    private val arrayRot = doubleArrayListOf()
    private val tr = Matrix.Transform()
    val size get() = arrayX.size

    data class Entry(
        var tex: BmpSlice = Bitmaps.transparent,
        var x: Double = 0.0,
        var y: Double = 0.0,
        var sx: Double = 1.0,
        var sy: Double = 1.0,
        var rot: Angle = 0.radians
    ) {
        override fun toString(): String = buildString {
            append("Entry(")
            append("'${tex.name}', ${x.toInt()}, ${y.toInt()}, ${tex.width}, ${tex.height}")
            if (sx != 1.0) append(", ${sx.niceStr}")
            if (sy != 1.0) append(", ${sy.niceStr}")
            if (rot != 0.radians) append(", ${rot.degrees.niceStr}")
            append(")")
        }
    }

    fun readAll(): List<Entry> = (0 until size).map { read(it) }

    fun read(n: Int, out: Entry = Entry()): Entry {
        out.tex = arrayTex[n]
        out.x = arrayX[n]
        out.y = arrayY[n]
        out.sx = arraySX[n]
        out.sy = arraySY[n]
        out.rot = arrayRot[n].radians
        return out
    }

    fun mreset() {
        arrayTex.clear()
        arrayX.clear()
        arrayY.clear()
        arraySX.clear()
        arraySY.clear()
        arrayRot.clear()
    }

    override fun put(codePoint: Int): GlyphMetrics {
        val bf = font as BitmapFont
        val m = getGlyphMetrics(codePoint)
        val g = bf[codePoint]
        val x = g.xoffset.toDouble()
        val y = g.yoffset.toDouble() - when (verticalAlign) {
            VerticalAlign.BASELINE -> bf.base
            else -> bf.lineHeight * verticalAlign.ratio
        }

        val fontScale = fontSize / bf.fontSize

        tr.setMatrixNoReturn(transform)
        //println("x: ${this.x}, y: ${this.y}")
        arrayTex.add(g.texture)
        arrayX.add(this.x + transform.transformX(x, y) * fontScale)
        arrayY.add(this.y + transform.transformY(x, y) * fontScale)
        arraySX.add(tr.scaleX * fontScale)
        arraySY.add(tr.scaleY * fontScale)
        arrayRot.add(tr.rotation.radians)
        return m
    }
}