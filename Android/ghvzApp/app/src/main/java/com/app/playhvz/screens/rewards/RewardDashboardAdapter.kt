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

package com.app.playhvz.screens.rewards

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.app.playhvz.R
import com.app.playhvz.firebase.classmodels.Reward

class RewardDashboardAdapter(
    private val activity: FragmentActivity,
    private var gameId: String,
    private var items: List<Reward>,
    val context: Context,
    private val navController: NavController,
    val onGenerateClaimCodes: (rewardId: String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isAdmin: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RewardViewHolder(
            activity,
            gameId,
            LayoutInflater.from(context).inflate(
                R.layout.card_reward,
                parent,
                false
            ),
            navController,
            onGenerateClaimCodes
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RewardViewHolder).onBind(items[position], isAdmin)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(data: List<Reward>) {
        items = data
    }

    fun setIsAdmin(isAdmin: Boolean) {
        this.isAdmin = isAdmin
    }
}