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

package com.app.playhvz.screens.chatroom

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.emoji.widget.EmojiEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.playhvz.R
import com.app.playhvz.app.EspressoIdlingResource
import com.app.playhvz.common.globals.SharedPreferencesConstants
import com.app.playhvz.common.globals.SharedPreferencesConstants.Companion.CURRENT_GAME_ID
import com.app.playhvz.common.globals.SharedPreferencesConstants.Companion.CURRENT_PLAYER_ID
import com.app.playhvz.firebase.classmodels.ChatRoom
import com.app.playhvz.firebase.classmodels.Message
import com.app.playhvz.firebase.classmodels.Player
import com.app.playhvz.firebase.operations.ChatDatabaseOperations
import com.app.playhvz.firebase.viewmodels.ChatRoomViewModel
import com.app.playhvz.navigation.NavigationUtil
import com.app.playhvz.utils.PlayerUtils
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.runBlocking

/** Fragment for showing a list of Chatrooms the user is a member of.*/
class ChatRoomFragment : Fragment() {
    companion object {
        private val TAG = ChatRoomFragment::class.qualifiedName
    }

    lateinit var chatViewModel: ChatRoomViewModel
    lateinit var messageAdapter: MessageAdapter

    private lateinit var chatRoomId: String
    private lateinit var playerId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var messageInputView: EmojiEditText
    private lateinit var sendButton: MaterialButton

    private val args: ChatRoomFragmentArgs by navArgs()
    private var gameId: String? = null
    private var toolbar: ActionBar? = null
    private var isChatWithAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatRoomId = args.chatRoomId
        playerId = args.playerId
        chatViewModel = ChatRoomViewModel()
        messageAdapter = MessageAdapter(listOf(), requireContext(), this)

        val sharedPrefs = activity?.getSharedPreferences(
            SharedPreferencesConstants.PREFS_FILENAME,
            0
        )!!
        gameId = sharedPrefs.getString(CURRENT_GAME_ID, null)

        toolbar = (activity as AppCompatActivity).supportActionBar
        setupObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)
        progressBar = view.findViewById(R.id.progress_bar)
        sendButton = view.findViewById(R.id.send_button)
        messageInputView = view.findViewById(R.id.message_input)
        messageInputView.requestFocus()
        messageInputView.doOnTextChanged { text, _, _, _ ->
            when {
                text.isNullOrEmpty() || text.isBlank() -> {
                    sendButton.isEnabled = false
                }
                else -> {
                    sendButton.isEnabled = true
                }
            }
        }
        sendButton.setOnClickListener {
            sendMessage()
        }
        val messageRecyclerView = view.findViewById<RecyclerView>(R.id.message_list)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager
        messageRecyclerView.adapter = messageAdapter
        progressBar.visibility = View.GONE
        setupToolbar()
        return view
    }

    override fun onStart() {
        super.onStart()
        // Must wait to show keyboard until the Fragment is started
        showKeyboard()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        val name = chatViewModel.getChatName()
        if (name != null && name != toolbar?.title) {
            toolbar?.title = name
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chat, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.chat_info) {
            NavigationUtil.navigateToChatInfo(findNavController(), chatRoomId, playerId, isChatWithAdmin)
        }
        return super.onOptionsItemSelected(item)
    }

    fun setupToolbar() {
        toolbar?.title = ""
        setHasOptionsMenu(true)
    }

    private fun setupObservers() {
        if (gameId.isNullOrEmpty() || playerId.isNullOrEmpty()) {
            return
        }
        chatViewModel.getChatRoomObserver(gameId!!, chatRoomId)
            .observe(this, androidx.lifecycle.Observer { serverChatRoom ->
                onChatRoomUpdated(serverChatRoom)
            })
        chatViewModel.getMessagesObserver(gameId!!, chatRoomId)
            .observe(this, androidx.lifecycle.Observer { serverMessageList ->
                onMessagesUpdated(serverMessageList)
            })
    }

    /** Update data and notify view and adapter of change. */
    private fun onChatRoomUpdated(updatedChatRoom: ChatRoom) {
        if (updatedChatRoom.name != toolbar?.title) {
            toolbar?.title = updatedChatRoom.name
            messageInputView.hint = getString(R.string.chat_input_hint, updatedChatRoom.name)
        }
        isChatWithAdmin = updatedChatRoom.withAdmins
    }

    private fun onMessagesUpdated(updatedMessageList: List<Message>) {
        val players = mutableMapOf<String, LiveData<Player>>()
        for (message in updatedMessageList) {
            if (!players.containsKey(message.senderId)) {
                players[message.senderId] = PlayerUtils.getPlayer(gameId!!, message.senderId)
            }

        }
        messageAdapter.setData(updatedMessageList, players)
        messageAdapter.notifyDataSetChanged()
    }

    private fun updateView() {
        if (this.view == null) {
            return
        }
        progressBar.visibility = View.GONE
    }

    private fun sendMessage() {
        val message = messageInputView.text.toString()
        if (message.isEmpty() || message.isBlank()) {
            return
        }
        messageInputView.text.clear()
        runBlocking {
            EspressoIdlingResource.increment()
            ChatDatabaseOperations.sendChatMessage(
                gameId!!,
                chatRoomId,
                playerId,
                message,
                {
                    EspressoIdlingResource.decrement()
                },
                {
                    EspressoIdlingResource.decrement()
                })
        }

    }

    private fun showKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(messageInputView, SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(messageInputView.windowToken, 0)
    }
}