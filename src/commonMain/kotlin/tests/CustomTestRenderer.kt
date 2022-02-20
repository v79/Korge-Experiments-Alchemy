package tests

import MainMenu
import com.soywiz.kds.doubleArrayListOf
import com.soywiz.klock.seconds
import com.soywiz.korge.debug.UiTextEditableValue
import com.soywiz.korge.debug.uiCollapsibleSection
import com.soywiz.korge.debug.uiEditableValue
import com.soywiz.korge.html.Html
import com.soywiz.korge.input.onClick
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.render.TexturedVertexArray
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.*
import com.soywiz.korim.paint.Paint
import com.soywiz.korim.text.*
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.extensionLC
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.lang.forEachCodePoint
import com.soywiz.korio.lang.toCharArray
import com.soywiz.korio.resources.Resourceable
import com.soywiz.korio.util.niceStr
import com.soywiz.korma.geom.*
import com.soywiz.korui.UiContainer
import kotlin.math.max
import kotlin.random.Random

class CustomTestRenderer : Scene() {

    override suspend fun Container.sceneInit() {

    }

    override suspend fun Container.sceneMain() {
        container {

            val fontNamesToFile: Map<String, VfsFile> = SystemFont.listFontNamesWithFiles()
            val oxaniumRegular = localVfs(fontNamesToFile["Oxanium Regular"]!!.path).readTtfFont()
            val oxaniumBold = localVfs(fontNamesToFile["Oxanium Bold"]!!.path).readTtfFont()
            println(oxaniumRegular)

            val t = CustomText(text = "Hello", textSize = 28.0)
            this.addChild(t)
            t.centerOnStage()

            val randomValue = Random.Default
            var r = 100
            var g = 100
            var b = 100
            var tx = 200.0
            var ty = 200.0
            val message = "None of this works"
            container {
                xy(tx, ty)
                for (char in message.toCharArray()) {
                    var tw = 0.0
                    val tt = text(text = char.toString(), color = RGBA(r, g, b), textSize = 48.0) {
                        xy(tx, ty)
                        tw = this.font.get().getGlyphMetrics(textSize, char.code).xadvance
                    }
                    alignTopToBottomOf(t, 20.0)
                    r = randomValue.nextInt(0, 255)
                    g = randomValue.nextInt(0, 255)
                    b = randomValue.nextInt(0, 255)
                    tx += tt.width
                    if (char == ' ') {
                        tx += 12.0
                    }
                }
            }

            container {
                xy(300.0, 300.0)
                cText(
                    text = "Custom test renderer",
                    textSize = 48.0,
                    renderer = CreateMyCustomTextRenderer(
                        getVersion = { 1 },
                        handler = { text, n, code, nextCode, g, advance ->
                            put(code)
                            advance(advance)
                        }
                    ),
                    font = oxaniumBold,
                    color = Colors.MEDIUMPURPLE
                )
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
}

inline fun Container.cText(
    text: String, textSize: Double = Text.DEFAULT_TEXT_SIZE,
    color: RGBA = Colors.WHITE, font: Resourceable<out Font> = DefaultTtfFont,
    alignment: TextAlignment = TextAlignment.TOP_LEFT,
    renderer: TextRenderer<String> = MyCustomTextRenderer,
    autoScaling: Boolean = Text.DEFAULT_AUTO_SCALING,
    block: @ViewDslMarker ColorText.() -> Unit = {}
): ColorText = ColorText(text, textSize, color, font, alignment, renderer, autoScaling).addTo(this, block)


inline fun <reified T> CustomTextRenderer() = MyCustomTextRenderer

val MyCustomTextRenderer: TextRenderer<String> =
    CreateMyCustomTextRenderer { text, n, c, c1, g, advance ->
        put(c)
        advance(advance)
    }

fun CreateMyCustomTextRenderer(
    getVersion: () -> Int = { 0 },
    handler: TextRendererActions.(text: String, n: Int, code: Int, nextCode: Int, g: GlyphMetrics, advance: Double) -> Unit
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

class CustomTextRendererActions : TextRendererActions() {
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
        println("----- put $codePoint")
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


class CustomText(text: String, textSize: Double) : Text(text, textSize)


/**
 * Complete copy of Text()
 */
open class ColorText(
    text: String, textSize: Double = DEFAULT_TEXT_SIZE,
    color: RGBA = Colors.WHITE, font: Resourceable<out Font> = DefaultTtfFont,
    alignment: TextAlignment = TextAlignment.TOP_LEFT,
    renderer: TextRenderer<String> = MyCustomTextRenderer,
    autoScaling: Boolean = DEFAULT_AUTO_SCALING
) : Container(), ViewLeaf, IText {
    companion object {
        val DEFAULT_TEXT_SIZE = 16.0
        val DEFAULT_AUTO_SCALING = true
    }

    var smoothing: Boolean = true

    private var cachedVersion = -1
    private var cachedVersionRenderer = -1
    private var version = 0

    var lineCount: Int = 0; private set

    override var text: String = text
        set(value) {
            if (field != value) {
                field = value
                updateLineCount()
                version++
            }
        }

    private fun updateLineCount() {
        lineCount = text.count { it == '\n' } + 1
    }

    init {
        updateLineCount()
    }

    var color: RGBA = color
        set(value) {
            if (field != value) {
                field = value; version++
            }
        }
    var font: Resourceable<out Font> = font
        set(value) {
            if (field != value) {
                field = value; version++
            }
        }
    var textSize: Double = textSize
        set(value) {
            if (field != value) {
                field = value; version++
            }
        }
    var fontSize: Double
        get() = textSize
        set(value) {
            textSize = value
        }
    var renderer: TextRenderer<String> = renderer
        set(value) {
            if (field != value) {
                field = value; version++
            }
        }

    var alignment: TextAlignment = alignment
        set(value) {
            if (field != value) {
                field = value; version++
            }
        }
    var horizontalAlign: HorizontalAlign
        get() = alignment.horizontal
        set(value) {
            alignment = alignment.withHorizontal(value)
        }
    var verticalAlign: VerticalAlign
        get() = alignment.vertical
        set(value) {
            alignment = alignment.withVertical(value)
        }

    private lateinit var textToBitmapResult: TextToBitmapResult
    private val container = container()
    private val bitmapFontActions = CustomTextRendererActions()
    private var fontLoaded: Boolean = false
    var autoScaling = autoScaling
    var preciseAutoscaling = false
        set(value) {
            field = value
            if (value) autoScaling = true
        }
    var fontSource: String? = null
        set(value) {
            field = value
            fontLoaded = false
        }

    // @TODO: Use, font: Resourceable<out Font>
    suspend fun forceLoadFontSource(currentVfs: VfsFile, sourceFile: String?) {
        fontSource = sourceFile
        fontLoaded = true
        if (sourceFile != null) {
            font = currentVfs["$sourceFile"].readFont()
        }
    }

    private val _textBounds = Rectangle(0, 0, 2048, 2048)
    var autoSize = true
    private var boundsVersion = -1
    val textBounds: Rectangle
        get() {
            getLocalBounds(_textBounds)
            return _textBounds
        }

    fun setFormat(
        face: Resourceable<out Font>? = this.font,
        size: Int = this.size,
        color: RGBA = this.color,
        align: TextAlignment = this.alignment
    ) {
        this.font = face ?: DefaultTtfFont
        this.textSize = size.toDouble()
        this.color = color
        this.alignment = align
    }

    fun setFormat(format: Html.Format) {
        setFormat(format.computedFace, format.computedSize, format.computedColor, format.computedAlign)
    }

    fun setTextBounds(rect: Rectangle) {
        if (this._textBounds == rect && !autoSize) return
        this._textBounds.copyFrom(rect)
        autoSize = false
        boundsVersion++
        version++
    }

    fun unsetTextBounds() {
        if (autoSize) return
        autoSize = true
        boundsVersion++
        version++
    }

    override fun getLocalBoundsInternal(out: Rectangle) {
        _renderInternal(null)
        out.copyFrom(_textBounds)
    }

    private val tempMatrix = Matrix()

    //var newTvaRenderer = true
    var newTvaRenderer = false

    override fun renderInternal(ctx: RenderContext) {
        _renderInternal(ctx)
        while (imagesToRemove.isNotEmpty()) {
            ctx.agBitmapTextureManager.removeBitmap(imagesToRemove.removeLast())
        }
        //val tva: TexturedVertexArray? = null
        if (tva != null) {
            tempMatrix.copyFrom(globalMatrix)
            tempMatrix.pretranslate(container.x, container.y)
            ctx.useBatcher { batch ->
                batch.setStateFast((font as BitmapFont).baseBmp, smoothing, renderBlendMode.factors, null)
                batch.drawVertices(tva!!, tempMatrix)
            }
        } else {
            super.renderInternal(ctx)
        }
    }

    var cachedVersionGlyphMetrics = -1
    private var _textMetricsResult: TextMetricsResult? = null

    fun getGlyphMetrics(): TextMetricsResult {
        if (cachedVersionGlyphMetrics != version) {
            cachedVersionGlyphMetrics = version
            _textMetricsResult = font.getOrNull()?.measureTextGlyphs(fontSize, text, renderer)
        }
        return _textMetricsResult ?: error("Must ensure font is resolved before calling getGlyphMetrics")
    }

    private val tempBmpEntry = CustomTextRendererActions.Entry()
    private val fontMetrics = FontMetrics()
    private val textMetrics = TextMetrics()
    private var lastAutoScaling: Boolean? = null
    private var lastSmoothing: Boolean? = null
    private var lastNativeRendering: Boolean? = null
    private var tva: TexturedVertexArray? = null
    private val identityMat = Matrix()

    fun _renderInternal(ctx: RenderContext?) {
        if (ctx != null) {
            val fontSource = fontSource
            if (!fontLoaded && fontSource != null) {
                fontLoaded = true
                launchImmediately(ctx.coroutineContext) {
                    forceLoadFontSource(ctx.views!!.currentVfs, fontSource)
                }
            }
        }
        container.colorMul = color
        val font = this.font.getOrNull()

        if (autoSize && font is Font && boundsVersion != version) {
            boundsVersion = version
            val metrics = font.getTextBounds(textSize, text, out = textMetrics, renderer = renderer)
            _textBounds.copyFrom(metrics.bounds)
            _textBounds.height = font.getFontMetrics(textSize, metrics = fontMetrics).lineHeight * lineCount
            _textBounds.x = -alignment.horizontal.getOffsetX(_textBounds.width) + metrics.left
            _textBounds.y = alignment.vertical.getOffsetY(_textBounds.height, -metrics.ascent)
        }

        when (font) {
            null -> Unit
            is BitmapFont -> {
                val rversion = renderer.version
                if (lastSmoothing != smoothing || cachedVersion != version || cachedVersionRenderer != rversion) {
                    lastSmoothing = smoothing
                    cachedVersionRenderer = rversion
                    cachedVersion = version

                    _staticImage = null
                    bitmapFontActions.x = 0.0
                    bitmapFontActions.y = 0.0

                    bitmapFontActions.mreset()
                    bitmapFontActions.verticalAlign = verticalAlign
                    bitmapFontActions.horizontalAlign = horizontalAlign
                    renderer.invoke(bitmapFontActions, text, textSize, font)
                    while (container.numChildren < bitmapFontActions.size) {
                        container.image(Bitmaps.transparent)
                    }
                    while (container.numChildren > bitmapFontActions.size) {
                        container[container.numChildren - 1].removeFromParent()
                    }
                    //println(font.glyphs['H'.toInt()])
                    //println(font.glyphs['a'.toInt()])
                    //println(font.glyphs['g'.toInt()])

                    val textWidth = bitmapFontActions.x

                    val dx = -textWidth * horizontalAlign.ratio

                    if (newTvaRenderer) {
                        this.tva = TexturedVertexArray.forQuads(bitmapFontActions.size)
                    }

                    for (n in 0 until bitmapFontActions.size) {
                        val entry = bitmapFontActions.read(n, tempBmpEntry)
                        if (newTvaRenderer) {
                            tva?.quad(
                                n * 4,
                                entry.x + dx,
                                entry.y,
                                entry.tex.width * entry.sx,
                                entry.tex.height * entry.sy,
                                identityMat,
                                entry.tex,
                                renderColorMul,
                                renderColorAdd
                            )
                        } else {
                            val it = (container[n] as Image)
                            it.anchor(0, 0)
                            it.smoothing = smoothing
                            it.bitmap = entry.tex
                            it.x = entry.x + dx
                            it.y = entry.y
                            it.scaleX = entry.sx
                            it.scaleY = entry.sy
                            it.rotation = entry.rot
                        }
                    }

                    setContainerPosition(0.0, 0.0, font.base)
                }
            }
            else -> {
                val onRenderResult = autoscaling.onRender(autoScaling, preciseAutoscaling, this.globalMatrix)
                val lastAutoScalingResult = lastAutoScaling != autoScaling
                if (onRenderResult || lastAutoScalingResult || lastSmoothing != smoothing || lastNativeRendering != useNativeRendering) {
                    version++
                    //println("UPDATED VERSION[$this] lastAutoScaling=$lastAutoScaling, autoScaling=$autoScaling, onRenderResult=$onRenderResult, lastAutoScalingResult=$lastAutoScalingResult")
                    lastNativeRendering = useNativeRendering
                    lastAutoScaling = autoScaling
                    lastSmoothing = smoothing
                }

                if (cachedVersion != version) {
                    cachedVersion = version
                    val realTextSize = textSize * autoscaling.renderedAtScaleXY
                    //println("realTextSize=$realTextSize")
                    textToBitmapResult = when {
                        text.isNotEmpty() -> {
                            font.renderTextToBitmap(
                                realTextSize, text,
                                paint = color, fill = true, renderer = renderer,
                                //background = Colors.RED,
                                nativeRendering = useNativeRendering, drawBorder = true
                            )
                        }
                        else -> {
                            TextToBitmapResult(Bitmaps.transparent.bmp, FontMetrics(), TextMetrics(), emptyList())
                        }
                    }

                    //println("RENDER TEXT: '$text'")

                    val met = textToBitmapResult.metrics
                    val x = -horizontalAlign.getOffsetX(met.width) + met.left
                    val y = verticalAlign.getOffsetY(met.lineHeight, -(met.ascent))

                    if (_staticImage == null) {
                        container.removeChildren()
                        _staticImage = container.image(textToBitmapResult.bmp)
                    } else {
                        imagesToRemove.add(_staticImage!!.bitmap.bmpBase)
                        _staticImage!!.bitmap = textToBitmapResult.bmp.slice()
                    }
                    val mscale = 1.0 / autoscaling.renderedAtScaleXY
                    _staticImage!!.scale(mscale, mscale)
                    setContainerPosition(x * mscale, y * mscale, font.getFontMetrics(fontSize, fontMetrics).baseline)
                }
                _staticImage?.smoothing = smoothing
            }
        }
    }

    var useNativeRendering: Boolean = true

    private val autoscaling = ColorInternalViewAutoscaling()

    private fun setContainerPosition(x: Double, y: Double, baseline: Double) {
        if (autoSize) {
            setRealContainerPosition(x, y)
        } else {
            //staticImage?.position(x + alignment.horizontal.getOffsetX(textBounds.width), y + alignment.vertical.getOffsetY(textBounds.height, font.getFontMetrics(fontSize).baseline))
            setRealContainerPosition(
                x + alignment.horizontal.getOffsetX(_textBounds.width),
                y - alignment.vertical.getOffsetY(_textBounds.height, baseline)
            )
        }
    }

    private fun setRealContainerPosition(x: Double, y: Double) {
        container.position(x, y)
    }

    private val imagesToRemove = arrayListOf<Bitmap>()

    internal var _staticImage: Image? = null

    val staticImage: Image?
        get() {
            _renderInternal(null)
            return _staticImage
        }

    override fun buildDebugComponent(views: Views, container: UiContainer) {
        container.uiCollapsibleSection("Text") {
            uiEditableValue(::text)
            uiEditableValue(::textSize, min = 1.0, max = 300.0)
            uiEditableValue(::autoScaling)
            uiEditableValue(
                ::verticalAlign,
                values = {
                    listOf(
                        VerticalAlign.TOP,
                        VerticalAlign.MIDDLE,
                        VerticalAlign.BASELINE,
                        VerticalAlign.BOTTOM
                    )
                })
            uiEditableValue(
                ::horizontalAlign,
                values = {
                    listOf(
                        HorizontalAlign.LEFT,
                        HorizontalAlign.CENTER,
                        HorizontalAlign.RIGHT,
                        HorizontalAlign.JUSTIFY
                    )
                })
            uiEditableValue(::fontSource, UiTextEditableValue.Kind.FILE(views.currentVfs) {
                it.extensionLC == "ttf" || it.extensionLC == "fnt"
            })
        }
        super.buildDebugComponent(views, container)
    }
}

class ColorInternalViewAutoscaling {
    var renderedAtScaleXInv = 1.0; private set
    var renderedAtScaleYInv = 1.0; private set
    var renderedAtScaleX = 1.0; private set
    var renderedAtScaleY = 1.0; private set
    var renderedAtScaleXY = 1.0; private set
    private val matrixTransform = Matrix.Transform()

    fun onRender(autoScaling: Boolean, autoScalingPrecise: Boolean, globalMatrix: Matrix): Boolean {
        if (autoScaling) {
            matrixTransform.setMatrixNoReturn(globalMatrix)
            //val sx = kotlin.math.abs(matrixTransform.scaleX / this.scaleX)
            //val sy = kotlin.math.abs(matrixTransform.scaleY / this.scaleY)

            val sx = kotlin.math.abs(matrixTransform.scaleX)
            val sy = kotlin.math.abs(matrixTransform.scaleY)
            val sxy = max(sx, sy)

            val diffX = kotlin.math.abs((sx / renderedAtScaleX) - 1.0)
            val diffY = kotlin.math.abs((sy / renderedAtScaleY) - 1.0)

            val shouldUpdate = when (autoScalingPrecise) {
                true -> (diffX > 0.0 || diffY > 0.0)
                false -> diffX >= 0.1 || diffY >= 0.1
            }

            if (shouldUpdate) {
                //println("diffX=$diffX, diffY=$diffY")

                renderedAtScaleX = sx
                renderedAtScaleY = sy
                renderedAtScaleXY = sxy
                renderedAtScaleXInv = 1.0 / sx
                renderedAtScaleYInv = 1.0 / sy
                //println("renderedAtScale: $renderedAtScaleX, $renderedAtScaleY")
                return true
            }
        } else {
            renderedAtScaleX = 1.0
            renderedAtScaleY = 1.0
            renderedAtScaleXY = 1.0
            renderedAtScaleXInv = 1.0
            renderedAtScaleYInv = 1.0
        }
        return false
    }
}
