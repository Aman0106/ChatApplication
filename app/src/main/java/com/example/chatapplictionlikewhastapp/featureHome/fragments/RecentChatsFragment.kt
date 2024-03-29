package com.example.chatapplictionlikewhastapp.featureHome.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplictionlikewhastapp.R
import com.example.chatapplictionlikewhastapp.databinding.FragmentRecentChatsBinding
import com.example.chatapplictionlikewhastapp.featureHome.adapters.RecentChatsAdapter
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.HomeViewModel
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.HomeViewModelFactory
import com.example.chatapplictionlikewhastapp.featureSignIn.activities.AuthActivity
import com.example.chatapplictionlikewhastapp.utils.HelperFunctions
import com.google.firebase.auth.FirebaseAuth


class RecentChatsFragment : Fragment() {

    private lateinit var binding: FragmentRecentChatsBinding
    private lateinit var recentChatsAdapter: RecentChatsAdapter
    private val homeViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(UsersRepository(requireActivity()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recentChatsAdapter = RecentChatsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleLogOut()

        observeRecentChats()
        prepareRecentChatsAdapter()

        handleUInteractions()
    }

    private fun handleUInteractions() {
        openNewChatFragment()
        val num = HelperFunctions.normalisePhoneNumber("+919814190131")
        Log.d("Helper verification", num)
    }

    private fun openNewChatFragment() {
        binding.fabNewChat.setOnClickListener {
            findNavController().navigate(R.id.action_recentChatsFragment_to_contactsListFragment)

        }
    }


    private fun observeRecentChats() {
        homeViewModel.getRecentChats()
        homeViewModel.recentChatsList.observe(viewLifecycleOwner) { recentChats ->
            recentChatsAdapter.setRecentChats(ArrayList(recentChats))
        }
    }

    private fun prepareRecentChatsAdapter() {

        binding.recViewRecentChats.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = recentChatsAdapter
        }

        recentChatsAdapter.onItemClicked = {
            homeViewModel.selectedChat = ContactsUserinfo(uid = it.uid, name = it.userName, profileImage = it.profileImage)
            findNavController().navigate(R.id.action_recentChatsFragment_to_chatFragment)
        }
    }

    private fun handleLogOut() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }


}