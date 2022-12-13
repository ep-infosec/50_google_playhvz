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

package com.app.playhvz.screens.quiz.editablequestions

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.playhvz.R
import com.app.playhvz.common.globals.CrossClientConstants
import com.app.playhvz.common.globals.SharedPreferencesConstants
import com.app.playhvz.common.ui.MarkdownEditText
import com.app.playhvz.firebase.classmodels.QuizQuestion
import com.app.playhvz.utils.SystemUtils

class InfoQuestionFragment : Fragment() {
    companion object {
        val TAG = InfoQuestionFragment::class.qualifiedName
    }

    val args: InfoQuestionFragmentArgs by navArgs()

    private lateinit var descriptionText: MarkdownEditText
    private lateinit var draftHelper: QuestionDraftHelper
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbarMenu: Menu

    private var gameId: String? = null
    private var playerId: String? = null
    private var questionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = activity?.getSharedPreferences(
            SharedPreferencesConstants.PREFS_FILENAME,
            0
        )!!
        gameId = sharedPrefs.getString(SharedPreferencesConstants.CURRENT_GAME_ID, null)
        playerId = sharedPrefs.getString(SharedPreferencesConstants.CURRENT_PLAYER_ID, null)
        questionId = args.questionId
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_quiz_question_info, container, false)
        progressBar = view.findViewById(R.id.progress_bar)
        descriptionText = view.findViewById(R.id.description_text)
        descriptionText.doOnTextChanged { text, _, _, _ ->
            when {
                text.isNullOrEmpty() -> {
                    disableActions()
                }
                else -> {
                    enableActions()
                }
            }
        }

        setupToolbar()
        setupDraftHelper()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save_settings, menu)
        toolbarMenu = menu
        disableActions()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_option) {
            saveChanges()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        SystemUtils.hideKeyboard(requireView())
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        SystemUtils.hideKeyboard(requireView())
    }

    private fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        if (toolbar != null) {
            toolbar.title = requireContext().getString(R.string.quiz_info_question_title)
        }
    }

    private fun setupDraftHelper() {
        draftHelper =
            QuestionDraftHelper(requireContext(), findNavController(), gameId!!, questionId)
        draftHelper.setDisableActions {
            disableActions()
        }
        draftHelper.setEnableActions {
            enableActions()
        }
        draftHelper.setProgressBar(progressBar)
        draftHelper.initializeDraft(
            CrossClientConstants.QUIZ_TYPE_INFO,
            args.nextAvailableIndex,
            { draft -> initUI(draft) })
    }

    private fun initUI(draft: QuizQuestion) {
        descriptionText.setText(draft.text)
    }

    private fun saveChanges() {
        val info = descriptionText.text.toString()
        draftHelper.questionDraft.text = info
        draftHelper.persistDraftToServer()
    }

    private fun disableActions() {
        SystemUtils.hideKeyboard(requireView())
        val menuItem = toolbarMenu.findItem(R.id.save_option)
        menuItem.icon.mutate().alpha = 130
        menuItem.isEnabled = false
    }

    private fun enableActions() {
        if (view == null || toolbarMenu.findItem(R.id.save_option) == null) {
            // Fragment was killed
            return
        }
        val menuItem = toolbarMenu.findItem(R.id.save_option)
        menuItem.icon.mutate().alpha = 255
        menuItem.isEnabled = true
    }
}