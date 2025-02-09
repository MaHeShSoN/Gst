package com.goldinvoice0.billingsoftware

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldinvoice0.billingsoftware.Adapter.ContactsAdapter
import com.goldinvoice0.billingsoftware.databinding.FragmentContactListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactListFragment : Fragment() {
    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactManager: ContactManager
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactManager = ContactManager(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setUpSearchView()
        binding.progressBar.visibility = View.VISIBLE
        setupRefreshLayout()
        lifecycleScope.launch(Dispatchers.Main) {
            loadContacts()
        }
    }


    private fun setUpSearchView() {
        val searchBar = binding.topAppBar.menu.findItem(R.id.action_search).actionView as androidx.appcompat.widget.SearchView
        searchBar.queryHint = "Search Contact..."  // Updated hint text
        searchBar.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS

        searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactsAdapter.filter(newText ?: "")
                return true
            }
        })

        searchBar.setOnCloseListener {
            searchBar.onActionViewCollapsed()
            contactsAdapter.filter("")  // Reset to show all customers
            true
        }
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter { contact ->
            onContactClicked(contact)
        }

        binding.recyclerViewContacts.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadContacts()
        }
    }

    private fun loadContacts() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE

        contactManager.getContacts(
            onSuccess = { contacts ->
                if (!isAdded) return@getContacts

                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false

                if (contacts.isEmpty()) {
                    binding.tvNoContacts.visibility = View.VISIBLE
                } else {
                    binding.tvNoContacts.visibility = View.GONE
                    contactsAdapter.setData(contacts)
                }
            },
            onError = { error ->
                if (!isAdded) return@getContacts

                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                binding.tvError.apply {
                    visibility = View.VISIBLE
                    text = error
                }

                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun onContactClicked(contact: Contact) {
        val bundle = Bundle().apply {
            putString("CustomerName", contact.displayName)
            putString("CustomerNumber", contact.phoneNumber)
        }

        findNavController().navigate(
            R.id.action_contactListFragment_to_customerList,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}