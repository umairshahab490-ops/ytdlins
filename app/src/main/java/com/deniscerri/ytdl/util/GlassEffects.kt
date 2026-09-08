package com.deniscerri.ytdl.util

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import com.deniscerri.ytdl.R

/**
 * Visual-only glassmorphism helpers.
 * Phase 1: layered XML backgrounds (specular + grain + stroke).
 * Phase 2: optional backdrop-style blur on API 31+ where supported.
 */
object GlassEffects {

    /** Apply Study Plan–style layered glass background to bottom nav / chrome. */
    fun applyNavGlass(view: View?) {
        view ?: return
        view.setBackgroundResource(R.drawable.bg_glass_nav_layered)
        // Soft elevation so the bar sits above content
        view.elevation = view.resources.getDimension(R.dimen.elevation_glass)
    }

    /** Apply layered glass card background. */
    fun applyCardGlass(view: View?) {
        view ?: return
        view.setBackgroundResource(R.drawable.bg_glass_card_layered)
    }

    /**
     * Best-effort frosted effect.
     * - API 31+: light blur on the view content as a soft frost accent
     *   (not true iOS backdrop refraction; safe fallback is layered XML only).
     * - Below API 31: no-op (layered drawables already applied).
     */
    fun applySoftFrost(view: View?, radiusPx: Float = 12f) {
        view ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                view.setRenderEffect(
                    RenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP)
                )
            } catch (_: Throwable) {
                // Device/GPU may reject; layered glass remains
            }
        }
    }

    /** Clear any RenderEffect (e.g. when recycling views). */
    fun clearFrost(view: View?) {
        view ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        }
    }

    /**
     * Window-level background blur behind translucent dialogs/sheets when the OS supports it.
     * Safe no-op on unsupported devices.
     */
    fun applyWindowBackgroundBlur(window: Window?, radiusPx: Int = 24) {
        window ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                @Suppress("DEPRECATION")
                // Available on API 31+; some OEMs gate this
                window.setBackgroundBlurRadius(radiusPx)
            } catch (_: Throwable) {
                // Ignore – glass XML still applies
            }
        }
    }
}
