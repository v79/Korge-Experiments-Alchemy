package tests

import MainMenu
import com.soywiz.klock.seconds
import com.soywiz.korag.shader.FragmentShader
import com.soywiz.korag.shader.Uniform
import com.soywiz.korag.shader.VarType
import com.soywiz.korag.shader.storageFor
import com.soywiz.korge.debug.uiEditableValue
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.IdentityFilter
import com.soywiz.korge.view.filter.ShaderFilter
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korui.UiContainer

class PixelShaderTest : Scene() {

	lateinit var sword: Bitmap

	override suspend fun Container.sceneInit() {
		val swordSpriteMap: Bitmap = resourcesVfs["sword.png"].readBitmap()
		sword = swordSpriteMap
	}

	override suspend fun Container.sceneMain() {

		val rectangle = SolidRect(width = 80.0, height = 80.0, color = Colors.WHITE).apply {
			xy(100.0, 100.0)

		}
		container {
			addChild(rectangle)
			sprite(sword) {
				xy(110.0,110.0)
				scale = 2.0
				onOver {
					filter = PixelOutlineFilter(color = Colors.AQUA)
				}
				onOut {
					filter = IdentityFilter
				}
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

/**
 * Outlines a view with a pixel outline.
 * @author Colton Daily
 * Lifted from https://github.com/LeHaine/kiwi/blob/master/src/commonMain/kotlin/com/lehaine/kiwi/korge/filter/PixelOutlineFilter.kt
 */
class PixelOutlineFilter(color: RGBA = Colors.BLACK) : ShaderFilter() {

	companion object {
		val u_texelSize = Uniform("texelSize", VarType.Float2)
		val u_outlineColor = Uniform("outlineColor", VarType.Float4)

		private val FRAGMENT_SHADER = FragmentShader {
			val currentColor = tex(fragmentCoords)

			// outline color multiplier based on transparent surrounding pixels
			val edge = ((1f.lit - currentColor.a) * max(
				tex(vec2(fragmentCoords.x + u_texelSize.x, fragmentCoords.y)).a, // left
				max(
					tex(vec2(fragmentCoords.x - u_texelSize.x, fragmentCoords.y)).a, // right
					max(
						tex(vec2(fragmentCoords.x, fragmentCoords.y + u_texelSize.y)).a, // top
						tex(vec2(fragmentCoords.x, fragmentCoords.y - u_texelSize.y)).a // bottom
					)
				)
			))
			val a = max(edge * u_outlineColor.a, min(1f.lit, currentColor.a))
			SET(out, vec4(mix(currentColor["rgb"], u_outlineColor["rgb"], edge) * a, a))
		}
	}

	private val texelSize = uniforms.storageFor(u_texelSize)
	private val outlineColor = uniforms.storageFor(u_outlineColor)

	var texelSizeX by texelSize.intDelegateX(default = 1)
	var texelSizeY by texelSize.intDelegateY(default = 1)

	var outlineColorR by outlineColor.floatDelegateX(default = color.rf)
	var outlineColorG by outlineColor.floatDelegateY(default = color.gf)
	var outlineColorB by outlineColor.floatDelegateZ(default = color.bf)
	var outlineColorA by outlineColor.floatDelegateW(default = color.af)


	override val programProvider: ShaderFilter.ProgramProvider = object : BaseProgramProvider() {
		override val fragment: FragmentShader = FRAGMENT_SHADER
	}

//	override val fragment: FragmentShader = FRAGMENT_SHADER
	override val border: Int = 1

	init {
		filtering = false
	}

	override fun buildDebugComponent(views: Views, container: UiContainer) {
		container.uiEditableValue(::texelSizeX)
		container.uiEditableValue(::texelSizeY)
		container.uiEditableValue(::outlineColorR)
		container.uiEditableValue(::outlineColorG)
		container.uiEditableValue(::outlineColorB)
		container.uiEditableValue(::outlineColorA)
	}
}
