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

package com.app.playhvz.screens.gamedashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.playhvz.R
import com.app.playhvz.common.globals.SharedPreferencesConstants
import com.app.playhvz.firebase.classmodels.Game
import com.app.playhvz.firebase.classmodels.Player
import com.app.playhvz.firebase.viewmodels.GameViewModel
import com.app.playhvz.navigation.NavigationUtil
import com.app.playhvz.screens.gamedashboard.cards.*
import com.app.playhvz.utils.PlayerUtils
import com.app.playhvz.utils.SystemUtils

/** Fragment for showing a list of Games the user is registered for.*/
class GameDashboardFragment : Fragment() {
    companion object {
        private val TAG = GameDashboardFragment::class.qualifiedName
    }

    private lateinit var firestoreViewModel: GameViewModel
    private lateinit var declareAllegianceCard: DeclareAllegianceCard
    private lateinit var infectCard: InfectCard
    private lateinit var leaderboardCard: LeaderboardCard
    private lateinit var lifeCodeCard: LifeCodeCard
    private lateinit var missionCard: MissionCard

    var gameId: String? = null
    var playerId: String? = null
    var game: Game? = null
    var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestoreViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)

        val sharedPrefs = activity?.getSharedPreferences(
            SharedPreferencesConstants.PREFS_FILENAME,
            0
        )!!
        gameId = sharedPrefs.getString(SharedPreferencesConstants.CURRENT_GAME_ID, null)
        playerId = sharedPrefs.getString(SharedPreferencesConstants.CURRENT_PLAYER_ID, null)

        if (gameId == null || playerId == null) {
            SystemUtils.clearSharedPrefs(requireActivity())
            NavigationUtil.navigateToGameList(findNavController(), requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupObservers()
        setupCards()
        val view = inflater.inflate(R.layout.fragment_game_dashboard, container, false)
        declareAllegianceCard.onCreateView(view)
        infectCard.onCreateView(view)
        leaderboardCard.onCreateView(view)
        lifeCodeCard.onCreateView(view)
        missionCard.onCreateView(view)
        setupToolbar()
        return view
    }

    fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        if (toolbar != null) {
            toolbar.title =
                if (game == null || game?.name.isNullOrEmpty()) requireContext().getString(R.string.app_name)
                else game?.name
        }
    }

    private fun setupObservers() {
        if (gameId == null || playerId == null) {
            return
        }
        firestoreViewModel.getGame(this, gameId!!)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { serverGame: Game ->
                updateGame(serverGame)
            })
        PlayerUtils.getPlayer(gameId!!, playerId!!)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { serverPlayer ->
                updatePlayer(serverPlayer)
            })
    }

    private fun setupCards() {
        declareAllegianceCard = DeclareAllegianceCard(this, gameId!!, playerId!!)
        infectCard = InfectCard(this, gameId!!, playerId!!)
        leaderboardCard = LeaderboardCard(this, gameId!!, playerId!!)
        lifeCodeCard = LifeCodeCard(this, gameId!!, playerId!!)
        missionCard = MissionCard(this, findNavController(), gameId!!, playerId!!)
    }

    private fun updateGame(serverGame: Game?) {
        game = serverGame
        setupToolbar()
    }

    private fun updatePlayer(serverPlayer: Player?) {
        if (serverPlayer == null) {
            NavigationUtil.navigateToGameList(findNavController(), requireActivity())
        }
        declareAllegianceCard.onPlayerUpdated(serverPlayer!!)
        infectCard.onPlayerUpdated(serverPlayer)
        leaderboardCard.onPlayerUpdated(serverPlayer)
        lifeCodeCard.onPlayerUpdated(serverPlayer)
    }
}