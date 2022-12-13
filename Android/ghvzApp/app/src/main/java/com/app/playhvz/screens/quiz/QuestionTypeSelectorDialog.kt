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

package com.app.playhvz.screens.quiz

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.app.playhvz.R
import com.app.playhvz.navigation.NavigationUtil
import com.google.android.material.button.MaterialButton

class QuestionTypeSelectorDialog(
    private val gameId: String,
    private val nextIndex: Int
) : DialogFragment() {
    companion object {
        private val TAG = QuestionTypeSelectorDialog::class.qualifiedName
    }

    private lateinit var customView: View
    private lateinit var typeSelectorGroup: RadioGroup

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_quiz_question_type, null)
        typeSelectorGroup = customView.findViewById(R.id.radio_button_group)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.quiz_dashboard_dialog_title))
            .setView(customView)
            .create()

        val negativeButton = customView.findViewById<MaterialButton>(R.id.negative_button)
        negativeButton.setOnClickListener {
            dialog?.dismiss()
        }
        val positiveButton = customView.findViewById<MaterialButton>(R.id.positive_button)
        positiveButton.setOnClickListener {
            triggerActionFromTypeSelection()
            dialog?.dismiss()
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Return already inflated custom view
        return customView
    }

    private fun triggerActionFromTypeSelection() {
        val selected = typeSelectorGroup.checkedRadioButtonId
        if (selected == R.id.radio_multiple_choice) {
            NavigationUtil.navigateToQuizMultipleChoiceQuestion(
                findNavController(),
                /* questionId= */ null,
                nextIndex
            )
        } else if (selected == R.id.radio_true_false) {
            NavigationUtil.navigateToQuizTrueFalseQuestion(
                findNavController(),
                /* questionId= */ null,
                nextIndex
            )
        } else if (selected == R.id.radio_order) {
            NavigationUtil.navigateToQuizOrderQuestion(
                findNavController(),
                /* questionId= */ null,
                nextIndex
            )
        } else {
            NavigationUtil.navigateToQuizInfoQuestion(
                findNavController(),
                /* questionId= */ null,
                nextIndex
            )
        }
    }
}