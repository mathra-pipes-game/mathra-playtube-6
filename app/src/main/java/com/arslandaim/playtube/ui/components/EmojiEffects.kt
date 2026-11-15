/*
 * PlayTube Project Original (2026)
 * arslandaim-hub (GitHub.com/arslandaim-hub)
 * Licenced Under GPL-3.0+
*/
package com.arslandaim.playtube.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.platform.LocalDensity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.sin
import kotlin.random.Random

/**
 * Emoji Effect styles the user can pick from in Settings.
 * Each style is a themed set of emojis that fall across the whole app.
 * All styles are free.
 */
enum class EffectMotion { FALL, RISE, DRIFT }

data class EmojiEffectStyle(
    val id: String,
    val displayName: String,
    val emojis: List<String>,
    val premium: Boolean = false,
    /** FALL = top to bottom, RISE = bottom to top (bubbles, balloons), DRIFT = falls while sweeping sideways. */
    val motion: EffectMotion = EffectMotion.FALL
)

val EmojiEffectStyles = listOf(
    EmojiEffectStyle("fire", "Fire", listOf("\uD83D\uDD25", "\u2728")),
    EmojiEffectStyle("hearts", "Hearts", listOf("\u2764\uFE0F", "\uD83D\uDC95", "\uD83D\uDC96")),
    EmojiEffectStyle("stars", "Stars", listOf("\u2B50", "\u2728", "\uD83C\uDF1F")),
    EmojiEffectStyle("notes", "Music Notes", listOf("\uD83C\uDFB5", "\uD83C\uDFB6", "\uD83C\uDFA7")),
    EmojiEffectStyle("rain", "Rain", listOf("\uD83D\uDCA7", "\uD83D\uDCA6")),
    EmojiEffectStyle("confetti", "Confetti", listOf("\uD83C\uDF89", "\uD83C\uDF8A", "\uD83C\uDF88")),
    EmojiEffectStyle("bubbles", "Bubbles", listOf("\uD83E\uDEE7", "\uD83D\uDD35")),
    EmojiEffectStyle("lightning", "Lightning", listOf("\u26A1", "\uD83D\uDCA5")),
    EmojiEffectStyle("snow", "Snow", listOf("\u2744\uFE0F", "\uD83C\uDF28\uFE0F")),
    EmojiEffectStyle("sparkle", "Sparkle", listOf("\uD83C\uDF1F", "\uD83D\uDCAB", "\u2728")),
    EmojiEffectStyle("funny", "Funny", listOf("\uD83D\uDE02", "\uD83E\uDD23", "\uD83D\uDE06", "\uD83D\uDE39", "\uD83D\uDE1D", "\uD83E\uDD2A", "\uD83D\uDE1C", "\uD83E\uDD73", "\uD83D\uDE01", "\uD83D\uDE0B")),
    EmojiEffectStyle("crown", "Royal Crown", listOf("\uD83D\uDC51", "\uD83D\uDC8E", "\u2728")),
    EmojiEffectStyle("diamond", "Diamond Rain", listOf("\uD83D\uDC8E", "\u2728", "\uD83D\uDCAB")),
    EmojiEffectStyle("sakura", "Sakura", listOf("\uD83C\uDF38", "\uD83C\uDF3A", "\uD83D\uDC9E")),
    EmojiEffectStyle("galaxy", "Galaxy", listOf("\uD83C\uDF19", "\u2B50", "\uD83D\uDCAB", "\uD83C\uDF1F")),
    EmojiEffectStyle("money", "Money Rain", listOf("💸", "💰", "🤑")),
    EmojiEffectStyle("party", "Party", listOf("🥳", "🎂", "🎁", "🎉")),
    EmojiEffectStyle("cats", "Cute Cats", listOf("🐱", "😺", "😻", "🐾")),
    EmojiEffectStyle("space", "Space", listOf("🚀", "👽", "🌍", "⭐")),
    EmojiEffectStyle("ocean", "Ocean", listOf("🐠", "🐬", "🐳", "🌊")),
    EmojiEffectStyle("halloween", "Halloween", listOf("🎃", "👻", "🦇")),
    EmojiEffectStyle("flowers", "Flowers", listOf("🌹", "🌷", "🌻", "🌼")),
    EmojiEffectStyle("gaming", "Gaming", listOf("🎮", "👾", "🕹️")),
    EmojiEffectStyle("rainbow", "Rainbow", listOf("🌈", "☀️", "☁️")),
    // ---- New effects ----
    EmojiEffectStyle("balloons", "Balloons", listOf("🎈", "🎈", "🎉", "✨"), motion = EffectMotion.RISE),
    EmojiEffectStyle("fireworks", "Fireworks", listOf("🎆", "🎇", "✨", "💥"), motion = EffectMotion.RISE),
    EmojiEffectStyle("butterflies", "Butterflies", listOf("🦋", "🌸", "🌼"), motion = EffectMotion.DRIFT),
    EmojiEffectStyle("autumn", "Autumn Leaves", listOf("🍂", "🍁", "🍃"), motion = EffectMotion.DRIFT),
    EmojiEffectStyle("magic", "Magic", listOf("🪄", "🔮", "✨", "🌟"), motion = EffectMotion.RISE),
    EmojiEffectStyle("love", "Love Letters", listOf("💌", "💘", "💝", "💗"), motion = EffectMotion.RISE),
    EmojiEffectStyle("candy", "Candy Shower", listOf("🍭", "🍬", "🍩", "🍪")),
    EmojiEffectStyle("icecream", "Ice Cream", listOf("🍦", "🍧", "🍨", "🍓")),
    EmojiEffectStyle("pizza", "Foodie", listOf("🍕", "🍔", "🍟", "🌮")),
    EmojiEffectStyle("sports", "Sports", listOf("⚽", "🏏", "🏀", "🏆")),
    EmojiEffectStyle("dogs", "Happy Dogs", listOf("🐶", "🐕", "🦴", "🐾")),
    EmojiEffectStyle("dragon", "Dragon Fire", listOf("🐉", "🔥", "⚔️", "🛡️")),
    EmojiEffectStyle("avurudu", "Avurudu", listOf("🪔", "🌸", "🎆", "🥁"), motion = EffectMotion.DRIFT),
    EmojiEffectStyle("lanterns", "Lanterns", listOf("🏮", "🪔", "✨"), motion = EffectMotion.RISE),
    EmojiEffectStyle("christmas", "Christmas", listOf("🎄", "🎅", "⛄", "🎁")),
    EmojiEffectStyle("tropical", "Tropical", listOf("🌴", "🥥", "🍍", "🌺"), motion = EffectMotion.DRIFT)
)

fun emojiStyleById(id: String): EmojiEffectStyle =
    EmojiEffectStyles.firstOrNull { it.id == id } ?: EmojiEffectStyles.first()

/** Emoji bitmaps are rendered once at this size and only scaled with a matrix afterwards. */
private const val EMOJI_BITMAP_DP = 56

/** Wait this long after app launch before the first effect starts, so Home can load smoothly. */
private const val STARTUP_DELAY_MS = 1400L

/** After the first run the startup delay is skipped (e.g. when the effect is toggled back on). */
@Volatile private var startupGateOpen = false

private class EmojiParticle(
    var x: Float,
    var y: Float,
    var emojiIndex: Int,
    val sizeDp: Float,
    val speedMul: Float,
    val drift: Float,
    val seed: Float,
    var rotation: Float,
    val spin: Float
)

private fun newParticle(emojiCount: Int, startAnywhere: Boolean) = EmojiParticle(
    x = Random.nextFloat(),
    y = if (startAnywhere) Random.nextFloat() else -0.08f - Random.nextFloat() * 0.15f,
    emojiIndex = Random.nextInt(emojiCount.coerceAtLeast(1)),
    sizeDp = Random.nextFloat() * 22f + 22f,          // 22dp..44dp
    speedMul = Random.nextFloat() * 0.5f + 0.85f,     // every emoji falls at its own pace
    drift = Random.nextFloat() * 0.6f - 0.3f,
    seed = Random.nextFloat() * 1000f,
    rotation = Random.nextFloat() * 40f - 20f,
    spin = Random.nextFloat() * 90f - 45f
)

/** Draws one emoji into a square bitmap. Done once per emoji, off the main thread. */
private fun renderEmojiBitmap(emoji: String, px: Int): Bitmap {
    val bmp = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
    val c = android.graphics.Canvas(bmp)
    val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = px * 0.78f
    }
    val fm = p.fontMetrics
    c.drawText(emoji, px / 2f, px / 2f - (fm.ascent + fm.descent) / 2f, p)
    return bmp
}

/**
 * Falling emoji particles overlaid on top of a screen (home, library, player...).
 * Touches pass straight through - the canvas has no pointer handling.
 *
 * Performance notes (why this is no longer slow):
 *  - emojis are pre-rendered to bitmaps once; each frame only draws scaled bitmaps
 *    (before, colour-emoji text was re-rasterised every frame at a changing size)
 *  - the first start waits until Home has had time to load
 *  - the animation stops while the app is in the background
 *  - fewer particles by default
 *
 * When [beatReactive] is true, fall speed swells from soft to strong.
 * Effects are silent: [soundEnabled] is kept only for API compatibility and ignored.
 */
@Composable
fun EmojiEffectOverlay(
    enabled: Boolean,
    isPlaying: Boolean = true,
    style: EmojiEffectStyle,
    beatReactive: Boolean,
    modifier: Modifier = Modifier,
    soundEnabled: Boolean = false,
    particleCount: Int = 20
) {
    if (!enabled) return

    var ready by remember { mutableStateOf(startupGateOpen) }
    if (!ready) {
        LaunchedEffect(Unit) {
            delay(STARTUP_DELAY_MS)
            startupGateOpen = true
            ready = true
        }
        return
    }

    EmojiEffectContent(
        isPlaying = isPlaying,
        style = style,
        beatReactive = beatReactive,
        modifier = modifier,
        soundEnabled = soundEnabled,
        particleCount = particleCount
    )
}

@Composable
private fun EmojiEffectContent(
    isPlaying: Boolean,
    style: EmojiEffectStyle,
    beatReactive: Boolean,
    modifier: Modifier,
    soundEnabled: Boolean,
    particleCount: Int
) {
    // Fall speed chosen in Settings (Slow 0.6x / Normal 1x / Fast 1.6x / Super Fast 2.4x).
    val speedScale by androidx.hilt.navigation.compose.hiltViewModel<com.arslandaim.playtube.MainViewModel>()
        .emojiEffectsSpeed
        .collectAsStateWithLifecycle()

    val fadeOut by androidx.hilt.navigation.compose.hiltViewModel<com.arslandaim.playtube.MainViewModel>()
        .emojiEffectsFadeOut
        .collectAsStateWithLifecycle()

    val density = LocalDensity.current.density
    val lifecycleOwner = LocalLifecycleOwner.current

    val particles = remember(style.id, particleCount) {
        List(particleCount) { newParticle(style.emojis.size, startAnywhere = true) }
    }

    val bitmapPx = remember(density) { (EMOJI_BITMAP_DP * density).toInt().coerceIn(48, 192) }
    val bitmaps by produceState<List<Bitmap>>(emptyList(), style.id, bitmapPx) {
        value = emptyList()
        value = withContext(Dispatchers.Default) {
            style.emojis.map { renderEmojiBitmap(it, bitmapPx) }
        }
    }

    var frame by remember { mutableLongStateOf(0L) }
    var rhythm by remember { mutableFloatStateOf(0.75f) }

    LaunchedEffect(isPlaying, style.id, beatReactive, bitmaps.isNotEmpty()) {
        if (!isPlaying || bitmaps.isEmpty()) return@LaunchedEffect
        // Only animate while the app is actually visible.
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var elapsed = 0f
            var last = withFrameNanos { it }
            while (true) {
                withFrameNanos { now ->
                    val dt = ((now - last) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    last = now
                    elapsed += dt

                    // Soft -> strong -> soft rhythm built from two sine waves.
                    val r = if (beatReactive) {
                        val slow = (sin(elapsed * 1.1f) + 1f) / 2f
                        val fast = (sin(elapsed * 3.7f) + 1f) / 2f
                        0.35f + 0.65f * (0.6f * slow + 0.4f * fast)
                    } else {
                        0.5f
                    }
                    rhythm = r

                    // Falling speed in screen-heights per second.
                    val speed = (0.15f + r * 0.30f) * speedScale
                    for (p in particles) {
                        when (style.motion) {
                            EffectMotion.FALL -> p.y += speed * p.speedMul * dt
                            EffectMotion.RISE -> p.y -= speed * p.speedMul * dt * 0.85f
                            EffectMotion.DRIFT -> {
                                p.y += speed * p.speedMul * dt * 0.7f
                                p.x += p.drift * 0.10f * dt * speedScale
                                if (p.x > 1.04f) p.x = -0.04f
                                if (p.x < -0.04f) p.x = 1.04f
                            }
                        }
                        p.rotation += p.spin * dt * 0.5f
                        val rising = style.motion == EffectMotion.RISE
                        if (!rising && p.y > 1.08f) {
                            p.y = -0.08f
                            p.x = Random.nextFloat()
                            p.emojiIndex = Random.nextInt(style.emojis.size)
                        } else if (rising && p.y < -0.08f) {
                            p.y = 1.08f
                            p.x = Random.nextFloat()
                            p.emojiIndex = Random.nextInt(style.emojis.size)
                        }
                    }
                    frame = now // invalidates the Canvas draw block below
                }
            }
        }
    }

    val paint = remember { Paint().apply { isFilterBitmap = true } }
    val matrix = remember { Matrix() }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tickRead = frame
            val list = bitmaps
            if (list.isEmpty()) return@Canvas

            val r = rhythm
            val t = tickRead / 1_000_000_000f
            val scale = 0.9f + r * 0.4f
            val alpha = (0.7f + r * 0.3f).coerceIn(0f, 1f)
            val canvas = drawContext.canvas.nativeCanvas
            paint.alpha = (alpha * 255).toInt()

            for (p in particles) {
                val bmp = list[p.emojiIndex % list.size]
                // Journey progress 0 (start edge) .. 1 (end edge); rising effects travel upwards.
                val journey = (if (style.motion == EffectMotion.RISE) 1f - p.y else p.y).coerceIn(0f, 1f)
                var life = 1f
                if (fadeOut) {
                    // Soft fade-in at the start, smooth fade-out and shrink over the last ~45% of the path.
                    val fin = (journey / 0.08f).coerceIn(0f, 1f)
                    val fout = (1f - (journey - 0.55f) / 0.45f).coerceIn(0f, 1f)
                    life = (fin * (fout * fout * (3f - 2f * fout))).coerceIn(0f, 1f)
                }
                if (life <= 0.01f) continue
                paint.alpha = (alpha * life * 255).toInt().coerceIn(0, 255)
                val wobble = sin(t * 1.4f + p.seed) * p.drift * 0.06f
                val drawX = (p.x + wobble).coerceIn(0.02f, 0.98f) * size.width
                val drawY = p.y * size.height
                val s = (p.sizeDp * density * scale * (0.7f + 0.3f * life)) / bmp.width

                matrix.reset()
                matrix.postTranslate(-bmp.width / 2f, -bmp.height / 2f)
                matrix.postScale(s, s)
                matrix.postRotate(p.rotation)
                matrix.postTranslate(drawX, drawY)
                canvas.drawBitmap(bmp, matrix, paint)
            }
        }
    }
}
