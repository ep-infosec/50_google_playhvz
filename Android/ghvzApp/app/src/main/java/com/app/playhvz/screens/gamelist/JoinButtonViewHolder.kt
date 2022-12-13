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

package com.app.playhvz.screens.gamelist

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.app.playhvz.R

class JoinButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val button = view.findViewById<Button>(R.id.join_button)

    fun onBind(labelId: Int, onClickListener: View.OnClickListener) {
        button.setText(labelId)
        button.setOnClickListener(onClickListener)
    }
}