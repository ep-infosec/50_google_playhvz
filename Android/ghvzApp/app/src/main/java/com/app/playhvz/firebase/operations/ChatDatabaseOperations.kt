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

package com.app.playhvz.firebase.operations

import android.util.Log
import com.app.playhvz.firebase.classmodels.Message
import com.app.playhvz.firebase.constants.ChatPath
import com.app.playhvz.firebase.constants.ChatPath.Companion.MESSAGE_COLLECTION
import com.app.playhvz.firebase.constants.PlayerPath
import com.app.playhvz.firebase.firebaseprovider.FirebaseProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.google.firestore.v1.DocumentTransform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatDatabaseOperations {
    companion object {
        private val TAG = ChatDatabaseOperations::class.qualifiedName

        /** Returns a document reference to the given chatRoomId. */
        fun getChatRoomDocumentReference(
            gameId: String,
            chatRoomId: String
        ): DocumentReference {
            return ChatPath.CHAT_DOCUMENT_REFERENCE(gameId, chatRoomId)
        }

        /** Returns a collection reference to the given chatRoomId. */
        fun getChatRoomMessagesReference(
            gameId: String,
            chatRoomId: String
        ): CollectionReference {
            return MESSAGE_COLLECTION(gameId, chatRoomId)
        }

        suspend fun sendChatMessage(
            gameId: String,
            chatRoomId: String,
            playerId: String,
            messageText: String,
            successListener: () -> Unit,
            failureListener: () -> Unit
        ) = withContext(Dispatchers.Default) {

            val message = Message.createFirebaseObject(playerId, messageText)
            MESSAGE_COLLECTION(gameId, chatRoomId).add(message).addOnSuccessListener {
                successListener.invoke()
            }.addOnFailureListener {
                Log.e(TAG, "Failed to send message: " + it)
                failureListener.invoke()
            }
        }

        /** Create Chat Room. */
        suspend fun asyncCreateChatRoom(
            gameId: String,
            playerId: String,
            chatName: String,
            allegianceFilter: String,
            successListener: () -> Unit,
            failureListener: () -> Unit
        ) = withContext(Dispatchers.Default) {
            val data = hashMapOf(
                "gameId" to gameId,
                "ownerId" to playerId,
                "chatName" to chatName,
                "allegianceFilter" to allegianceFilter
            )

            FirebaseProvider.getFirebaseFunctions()
                .getHttpsCallable("createChatRoom")
                .call(data)
                .continueWith { task ->
                    if (!task.isSuccessful) {
                        failureListener.invoke()
                        return@continueWith
                    }
                    successListener.invoke()
                }
        }

        /** Create or fetch the player's chat room with admins. */
        suspend fun asyncCreateOrGetAdminChat(
            gameId: String,
            playerId: String,
            successListener: (chatRoomId: String) -> Unit,
            failureListener: () -> Unit
        ) = withContext(Dispatchers.Default) {
            val data = hashMapOf(
                "gameId" to gameId,
                "playerId" to playerId
            )

            FirebaseProvider.getFirebaseFunctions()
                .getHttpsCallable("createOrGetChatWithAdmin")
                .call(data)
                .continueWith { task ->
                    if (!task.isSuccessful) {
                        failureListener.invoke()
                        return@continueWith
                    }
                    val adminChatId = task.result?.data as String
                    successListener.invoke(adminChatId)
                }
        }

        /** Adds list of players to chat room. */
        suspend fun asyncAddPlayersToChat(
            gameId: String,
            playerIdList: List<String>,
            groupId: String,
            chatRoomId: String,
            successListener: () -> Unit,
            failureListener: () -> Unit
        ) = withContext(Dispatchers.Default) {
            val data = hashMapOf(
                "gameId" to gameId,
                "groupId" to groupId,
                "chatRoomId" to chatRoomId,
                "playerIdList" to playerIdList
            )

            FirebaseProvider.getFirebaseFunctions()
                .getHttpsCallable("addPlayersToChat")
                .call(data)
                .continueWith { task ->
                    if (!task.isSuccessful) {
                        failureListener.invoke()
                        return@continueWith
                    }
                    successListener.invoke()
                }
        }

        /** Remove player from Chat Room. */
        suspend fun asyncRemovePlayerFromChatRoom(
            gameId: String,
            playerId: String,
            chatRoomId: String,
            successListener: () -> Unit,
            failureListener: () -> Unit
        ) = withContext(Dispatchers.Default) {
            val data = hashMapOf(
                "gameId" to gameId,
                "playerId" to playerId,
                "chatRoomId" to chatRoomId
            )

            FirebaseProvider.getFirebaseFunctions()
                .getHttpsCallable("removePlayerFromChat")
                .call(data)
                .continueWith { task ->
                    if (!task.isSuccessful) {
                        Log.e(TAG, "Could not remove player from chat: ${task.exception}")
                        failureListener.invoke()
                        return@continueWith
                    }
                    successListener.invoke()
                }
        }

    }
}