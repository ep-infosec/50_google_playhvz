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

package com.app.playhvz.firebase.classmodels

/** Android data model representing Firebase Game documents. */
class Stat {
    companion object {
        const val FIELD__CURRENT_HUMAN_COUNT = "currentHumanCount"
        const val FIELD__CURRENT_ZOMBIE_COUNT = "currentZombieCount"
        const val FIELD__STARTER_ZOMBIE_COUNT = "starterZombieCount"
        const val FIELD__STATS_OVER_TIME = "statsOverTime"
        const val FIELD__OVER_TIME_TIMESTAMP = "timestamp"
        const val FIELD__OVER_TIME_INFECTION_COUNT = "infectionCount"
    }

    var id: String? = null
    var currentHumanCount: Int = 0
    var currentZombieCount: Int = 0
    var starterZombieCount: Int = 0
    var isOutOfData = false
    var statsOverTime: List<StatOverTime> = listOf()

    class StatOverTime(var interval: Long = 0, var infectionCount: Int = 0) {}
}