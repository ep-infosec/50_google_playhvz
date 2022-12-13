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

package com.app.playhvz.utils

import android.widget.ImageView
import com.app.playhvz.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class ImageDownloaderUtils {
    companion object {
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
        const val PICK_IMAGE_REQUEST_CODE = 2

        /** Function that handles async downloading an image from a url.
         * @param imageView the view to show the image in once downloaded
         * @param imageUrl the image url to download
         * @param radius the radius of how rounded the corners should be
         */
        fun downloadCircularImage(imageView: ImageView, imageUrl: String, radius: Int) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .transform(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(imageView)
        }

        /** Function that handles async downloading an image from a url.
         * @param imageView the view to show the image in once downloaded
         * @param imageUrl the image url to download
         */
        fun downloadSquareImage(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .transform(CenterCrop())
                .placeholder(R.drawable.ic_hourglass)
                .error(R.drawable.ic_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(imageView)
        }

        /** Function that handles async downloading an image from a url.
         * @param imageView the view to show the image in once downloaded
         * @param imageUrl the image url to download
         */
        fun downloadSquareImage(imageView: ImageView, imageUrl: String, withCache: Boolean) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .transform(CenterCrop())
                .placeholder(R.drawable.ic_hourglass)
                .error(R.drawable.ic_error)
                .diskCacheStrategy(if (withCache) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE)
                .priority(Priority.HIGH)
                .into(imageView)
        }
    }
}
