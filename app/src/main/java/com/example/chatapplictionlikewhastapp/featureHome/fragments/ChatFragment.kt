package com.example.chatapplictionlikewhastapp.featureHome.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.chatapplictionlikewhastapp.databinding.FragmentChatBinding
import com.example.chatapplictionlikewhastapp.featureHome.adapters.ChatMessageAdapter
import com.example.chatapplictionlikewhastapp.featureHome.repository.ChatRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirebaseClientRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirestoreChatRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.ChatViewModel
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.ChatViewModelFactory
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.HomeViewModel
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.HomeViewModelFactory

private val LOG_TAG = "INSIDE_CHAT_FRAGMENT"

class ChatFragment : Fragment() {

    private lateinit var chatViewModel: ChatViewModel
    private val homeViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(UsersRepository(requireActivity()), FirebaseClientRepository())
    }
    private lateinit var chatsAdapter: ChatMessageAdapter
    private lateinit var binding: FragmentChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatViewModelFactory = ChatViewModelFactory(ChatRepository(), FirestoreChatRepository())
        chatViewModel = ViewModelProvider(this, chatViewModelFactory)[ChatViewModel::class.java]
        chatsAdapter = ChatMessageAdapter(requireActivity(), homeViewModel.currentUser)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "Destroyeddsadasd")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setChats()
        handleUiInteractions()
    }

    private fun setChats() {
        chatViewModel.setRoomId(homeViewModel.selectedChat?.uid!!)
        chatViewModel.roomId.observe(viewLifecycleOwner) {
            if (it == "") {
                Toast.makeText(activity, "No Room found", Toast.LENGTH_SHORT).show()
                return@observe
            }
            Toast.makeText(activity, "Room found $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun prepareChatsListAdapter() {

        binding.recyclerviewMessages.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = chatsAdapter
        }

    }

    private fun handleUiInteractions() {
        onBackPressAction()
        observeMessagesList()
        setTopBarData()
        sendMessage()
    }

    private fun sendMessage() {
        binding.btnSend.setOnClickListener {
            val message = binding.edtMessageBox.text
            binding.edtMessageBox.setText("")
            chatViewModel.sendMessageToUser(homeViewModel.selectedChat?.uid!!, message.toString())
        }
    }

    private fun setTopBarData() {
        binding.tvUserName.text = homeViewModel.selectedChat?.name
        if(homeViewModel.selectedChat?.profileImage != null)
            binding.imgProfileImage.load(homeViewModel.selectedChat?.profileImage)
    }

    private fun observeMessagesList() {
        chatViewModel.messageList.observe(viewLifecycleOwner) {
            prepareChatsListAdapter()
            chatsAdapter.setMessages(ArrayList(it))
        }
    }

    private fun onBackPressAction() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressCallback)
    }

}