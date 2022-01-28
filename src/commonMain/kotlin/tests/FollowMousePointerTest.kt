package tests

import MainMenu
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.seconds
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
sealed class Animal() {
    abstract val name: String
}

@Serializable
data class Cat(override val name: String, val age: Int) : Animal()

@Serializable
class Dog(override val name: String, val color: String, @Serializable(with = TimeSpanAsDoubleSerializer::class) val timeSpan: TimeSpan = TimeSpan(1000.0) ) : Animal() {
    @Transient
    var fleas: Int = 0

}

object TimeSpanAsDoubleSerializer : KSerializer<TimeSpan> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimeSpan", PrimitiveKind.DOUBLE)
    override fun serialize(encoder: Encoder, value: TimeSpan) = encoder.encodeDouble(value.milliseconds)
    override fun deserialize(decoder: Decoder): TimeSpan = TimeSpan(decoder.decodeDouble())
}

class FollowMousePointerTest : Scene() {

    lateinit var cat: Cat
    lateinit var dog: Dog
    lateinit var animals: List<Animal>

    val rect = RoundRect(25.0,25.0,0.3,0.3)
    override suspend fun Container.sceneInit() {

        cat = Cat("tiddles", 6)
        dog = Dog("rufus","brown")

        animals = listOf(cat,dog)
    }

    override suspend fun Container.sceneMain() {

        val format = Json {
            prettyPrint = true
        }

        println(message = format.encodeToString(cat))
        println(format.encodeToString(animals))

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
