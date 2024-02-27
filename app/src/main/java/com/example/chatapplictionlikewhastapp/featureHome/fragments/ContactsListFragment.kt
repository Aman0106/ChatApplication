package com.example.chatapplictionlikewhastapp.featureHome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplictionlikewhastapp.databinding.FragmentContactsListBinding
import com.example.chatapplictionlikewhastapp.featureHome.adapters.ContactsListAdapter
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.HomeViewModel
import com.example.chatapplictionlikewhastapp.featureHome.viewModels.HomeViewModelFactory
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.coroutineContext


class ContactsListFragment : Fragment() {

    private lateinit var binding: FragmentContactsListBinding

    private val homeViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(UsersRepository(requireActivity()))
    }

    private lateinit var contactsListAdapter: ContactsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactsListAdapter = ContactsListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareContactsListAdapter()
        handleUiInteractions()
    }

    private fun prepareContactsListAdapter() {
        binding.recyclerContacts.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = contactsListAdapter
        }

        contactsListAdapter.onItemClicked = {

        }
    }

    private fun handleUiInteractions() {
        onBackPressAction()
        observeContactsList()
    }

    private fun observeContactsList() {

        homeViewModel.getContactsList()
        homeViewModel.contactsList.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            contactsListAdapter.setContactsList(ArrayList(it))
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