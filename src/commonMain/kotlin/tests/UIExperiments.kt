package tests

import com.soywiz.klock.seconds
import com.soywiz.korge.bus.SyncBus
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.ColorTransform
import com.soywiz.korim.color.transform
import com.soywiz.korim.font.readBitmapFont
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.interpolation.Easing
import goHomeButton

class UIExperiments(private val bus: SyncBus) : Scene() {

	override suspend fun Container.sceneInit() {

		uiSkin = UISkin {
			val colorTransform = ColorTransform(0.7, 0.9, 0.2)
			this.uiSkinBitmap = this.uiSkinBitmap.withColorTransform(colorTransform)
			this.buttonBackColor = this.buttonBackColor.transform(colorTransform)
			this.textSize = 14.0
		}
	}

	override suspend fun Container.sceneMain() {

		val yellowSkin = UISkin {
			val colorTransform = ColorTransform(0.9, 0.9, 0.1)
			this.uiSkinBitmap = this.uiSkinBitmap.withColorTransform(colorTransform)
			this.buttonBackColor = this.buttonBackColor.transform(colorTransform)
			this.textAlignment = TextAlignment.LEFT
			this.buttonTextAlignment = TextAlignment.LEFT
			this.textSize = 24.0
		}

		container {
			uiText(text = "Skin applies a green colour to items. Some items have a custom yellow skin.") {
				xy(20.0, sceneContainer.height - 50.0)
			}
			flowContainer(
				maxWidth = 1024.0, maxHeight = sceneContainer.height - 20.0, minPadding = 10.0,
				configuration = FlowContainer.FlowConfiguration(direction = FlowContainer.FlowLayout.Horizontal)
			) {
				flowContainer(
					maxWidth = 400.0,
					maxHeight = sceneContainer.height - 20.0,
					minPadding = 10.0,
					configuration = FlowContainer.FlowConfiguration(direction = FlowContainer.FlowLayout.Vertical)
				) {
//					xy(100.0, 100.0)
					uiButton(width = 100.0, height = 28.0, text = "uiButton") { }
					uiButton(width = 140.0, height = 28.0, text = "uiButton with icon") { }
					uiButton(width = 200.0, text = "Styled ui Button") {
						uiSkin = UISkin {
							val colorTransform = ColorTransform(0.9, 0.9, 0.1)
							this.uiSkinBitmap = this.uiSkinBitmap.withColorTransform(colorTransform)
							this.buttonBackColor = this.buttonBackColor.transform(colorTransform)
						}
					}
					uiButton(width = 140.0, height = 28.0, text = "disabled uiButton") {
						disable()
					}
					uiText("Text - combo box")
					uiComboBox(
						width = 200.0,
						height = 28.0,
						selectedIndex = 0,
						items = listOf("Apple", "Pear", "Banana")
					)
					uiText("Text - styled combo box")
					uiComboBox(
						width = 200.0,
						height = 28.0,
						selectedIndex = 0,
						items = listOf("Apple", "Pear", "Banana"),
					) {
						uiSkin = yellowSkin
					}
					uiText("Checkbox doesn't inherit my skin")
					uiCheckBox(checked = true, text = "Checkbox")
					uiText("Checkbox with custom skin")
					uiCheckBox(checked = false, text = "Checkbox") {
						uiSkin = yellowSkin
					}
				}
				flowContainer(
					maxWidth = 400.0,
					maxHeight = sceneContainer.height,
					minPadding = 10.0,
					configuration = FlowContainer.FlowConfiguration(direction = FlowContainer.FlowLayout.Vertical)
				) {
					uiText("Radio button")
					val radioGroup = UIRadioButtonGroup()
					uiRadioButton(text = "Apple", group = radioGroup)
					uiRadioButton(text = "Pear", group = radioGroup)
					uiText("Styled Radio button")
					val radioGroup2 = UIRadioButtonGroup()
					uiRadioButton(text = "Orange", group = radioGroup2) {
						uiSkin = yellowSkin
					}
					uiRadioButton(text = "Guava", group = radioGroup2) {
						uiSkin = yellowSkin
					}

					/*
					Animated progress bars completely break flowLayout
					uiText("Progress bar")
					val progress = uiProgressBar {
						position(100,100)
						current = 0.5

					}
					while (true) {
						tween(progress::ratio[1.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
						tween(progress::ratio[1.0, 0.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
					}*/
					uiText("Scrollable")
					uiScrollable(width = 100.0, height = 100.0) {
//						uiText("In this scrollable container, we have some words which don't fit. So it should scroll?")
						solidRect(25.0, 25.0) {
							xy(5.0, 5.0)
						}
						circle(25.0) {
							xy(75.0, 75.0)
						}
					}
					uiText("Scrollable with yellow style")
					uiScrollable(width = 100.0, height = 100.0) {
						uiSkin = yellowSkin
//						uiText("In this scrollable container, we have some words which don't fit. So it should scroll?")
						solidRect(25.0, 25.0) {
							xy(5.0, 5.0)
						}
						circle(25.0) {
							xy(75.0, 75.0)
						}
					}
				}
				flowContainer(
					maxWidth = 500.0,
					maxHeight = sceneContainer.height,
					minPadding = 10.0,
					configuration = FlowContainer.FlowConfiguration(direction = FlowContainer.FlowLayout.Vertical)
				) {
					val breadCrumbPath = listOf("Home", "About Us", "Help")
					uiText("Breadcrumb") {
					}
					uiBreadCrumb(path = breadCrumbPath)
					uiBreadCrumb(path = breadCrumbPath) {
						uiSkin = yellowSkin
					}

					uiText("Window")
					uiWindow("UIWindow",width = 200.0, height = 200.0) {
						uiText("Text inside window")
					}

					uiText("Text input")
					uiTextInput(initialText = "<Input>") {
						disable()
					}

					uiText("Styled text input")
					uiTextInput(initialText = "<Styled>") {
						uiSkin = yellowSkin
					}

					uiTooltipContainer { tooltips ->
						uiButton(text = "Hover me") {
							tooltip(tooltips, text = "Hovered over button")
						}
					}

				}
				/*text(
					"""
					uiSkinBitmap: Bitmap32 
					textFont: Font
					textSize: Double 
					textColor: RGBA 
					textAlignment: TextAlignment 
					shadowColor: RGBA 
					shadowPosition: IPoint g
					buttonNormal: NinePatchBmpSlice 
					buttonOver: NinePatchBmpSlice 
					buttonDown: NinePatchBmpSlice 
					buttonDisabled: NinePatchBmpSlice 
					radioNormal: NinePatchBmpSlice 
					radioOver: NinePatchBmpSlice 
					radioDown: NinePatchBmpSlice 
					radioDisabled: NinePatchBmpSlice 
					buttonBackColor: RGBA
					buttonTextAlignment: TextAlignment
					iconCheck: BitmapSlice<Bitmap32>
					iconUp: BitmapSlice<Bitmap32>
					iconRight: BitmapSlice<Bitmap32>
					iconDown: BitmapSlice<Bitmap32
					iconLeft: BitmapSlice<Bitmap32
					comboBoxShrinkIcon: BitmapSlice<Bitmap32> 
					comboBoxExpandIcon: BitmapSlice<Bitmap32> 
					checkBoxIcon: BitmapSlice<Bitmap32> 
					scrollbarIconLeft: BitmapSlice<Bitmap32>
					scrollbarIconRight: BitmapSlice<Bitmap32> 
					scrollbarIconUp: BitmapSlice<Bitmap32>
					scrollbarIconDown: BitmapSlice<Bitmap32>
				""".trimIndent()

				)
				 */
			}
		}


		goHomeButton(sceneContainer)
	}
}
