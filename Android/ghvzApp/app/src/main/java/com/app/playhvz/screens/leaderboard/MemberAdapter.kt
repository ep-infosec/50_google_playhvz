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

package com.app.playhvz.screens.leaderboard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.playhvz.R
import com.app.playhvz.firebase.classmodels.Player

class MemberAdapter(
    private var items: List<Player>,
    val context: Context,
    private val viewProfile: ((playerId: String) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MemberViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_leaderboard_player,
                parent,
                false
            ),
            viewProfile
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MemberViewHolder).onBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(data: List<Player?>) {
        val cleansedList = mutableListOf<Player>()
        for (player in data) {
            if (player != null) {
                cleansedList.add(player)
            }
        }
        items = cleansedList
        notifyDataSetChanged()
    }
}