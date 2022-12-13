/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.playhvz.common

import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.app.playhvz.R
import com.app.playhvz.common.globals.CrossClientConstants
import com.app.playhvz.firebase.classmodels.Player
import com.app.playhvz.utils.ImageDownloaderUtils
import com.app.playhvz.utils.PlayerUtils
import com.app.playhvz.utils.PlayerUtils.Companion.AliveStatus


class UserAvatarPresenter(avatarView: View, sizeDimenRes: Int) {
    private val totalHeightPixels: Int = avatarView.resources.getDimensionPixelSize(sizeDimenRes)
    private val borderWidthPixels: Int =
        avatarView.resources.getDimensionPixelSize(R.dimen.avatar_border_width)
    private val radius: Int = totalHeightPixels / 2
    private val avatarImageView = avatarView.findViewById<ImageView>(R.id.player_avatar_image)
    private val avatarBorderView = avatarView

    init {
        avatarView.layoutParams.height = totalHeightPixels
        avatarView.layoutParams.width = totalHeightPixels
        avatarImageView.layoutParams.height = totalHeightPixels - borderWidthPixels
        avatarImageView.layoutParams.width = totalHeightPixels - borderWidthPixels
    }

    fun renderAvatar(player: Player) {
        ImageDownloaderUtils.downloadCircularImage(avatarImageView, player.avatarUrl, radius)
        renderAllegiance(player)
    }

    private fun renderAllegiance(player: Player) {
        val aliveStatus: AliveStatus = PlayerUtils.getAliveStatus(player.allegiance)
        val unwrappedDrawable =
            AppCompatResources.getDrawable(avatarBorderView.context, R.drawable.border_circle)
        val wrappedDrawable =
            DrawableCompat.wrap(unwrappedDrawable!!)
        val ringColor = if (aliveStatus == AliveStatus.ALIVE) {
            CrossClientConstants.getAliveColor(avatarBorderView.context)
        } else {
            CrossClientConstants.getDeadColor(avatarBorderView.context)
        }
        DrawableCompat.setTint(wrappedDrawable, ringColor)
        avatarBorderView.background = wrappedDrawable

        avatarBorderView.contentDescription = avatarBorderView.resources.getString(
            if (aliveStatus == AliveStatus.ALIVE) {
                R.string.allegiance_status_alive
            } else {
                R.string.allegiance_status_dead
            }
        )
    }
}