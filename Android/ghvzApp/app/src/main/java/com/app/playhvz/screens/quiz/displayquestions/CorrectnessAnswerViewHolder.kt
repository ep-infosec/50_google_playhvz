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
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.app.playhvz.R
import com.app.playhvz.firebase.classmodels.QuizQuestion

class CorrectnessAnswerViewHolder(
    val view: View,
    val onSelect: (adapterPosition: Int) -> Unit?
) : RecyclerView.ViewHolder(view) {

    private val answerCheckbox = view.findViewById<CheckBox>(R.id.answer_check)!!
    private var answer: QuizQuestion.Answer? = null

    fun onBind(position: Int, answer: QuizQuestion.Answer) {
        this.answer = answer
        answerCheckbox.text = answer.text
        answerCheckbox.setOnClickListener {
            onSelect(position)
        }
        itemView.setOnClickListener {
            answerCheckbox.performClick()
        }
    }
}