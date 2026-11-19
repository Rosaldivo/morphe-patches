/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.morphe.patches.music.interaction.autoplay

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val keepAutoplaySettingPatch = bytecodePatch(
    name = "Keep autoplay setting",
    description = "Fixes the autoplay setting (Settings > Playback > Dynamic queue) turning itself " +
            "back on when a new track starts."
) {
    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        ApplyDefaultAutoplayModeFingerprint.let {
            // Make the "user has changed autoplay" check always true, so the saved
            // setting is used and is never replaced with the server's default.
            val moveResultIndex = it.instructionMatches.last().index
            val register = it.method.getInstruction<OneRegisterInstruction>(moveResultIndex).registerA

            it.method.addInstruction(moveResultIndex + 1, "const/4 v$register, 0x1")
        }
    }
}
