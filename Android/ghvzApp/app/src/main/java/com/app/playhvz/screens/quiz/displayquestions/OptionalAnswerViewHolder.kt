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

package com.app.playhvz.screens.quiz.displayquestions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.playhvz.R
import com.app.playhvz.common.ui.MarkdownTextView
import com.app.playhvz.firebase.classmodels.QuizQuestion

class OptionalAnswerViewHolder(
    val view: View,
    val onSelect: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val answerText = view.findViewById<MarkdownTextView>(R.id.answer_button)
    private var answer: QuizQuestion.Answer? = null

    fun onBind(position: Int, answer: QuizQuestion.Answer) {
        this.answer = answer
        answerText.text = answer.text
        answerText.setOnClickListener {
            onSelect(position)
        }
        itemView.setOnClickListener {
            answerText.performClick()
        }
    }
}