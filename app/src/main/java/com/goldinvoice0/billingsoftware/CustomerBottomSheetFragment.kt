package com.yourpackage.ui.bottomsheet

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.FilterQueryProvider
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.goldinvoice0.billingsoftware.Model.Customer
import com.goldinvoice0.billingsoftware.R
import com.goldinvoice0.billingsoftware.databinding.BottomSheetAddCustomerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomerBottomSheetFragment : BottomSheetDialogFragment(),
    LoaderManager.LoaderCallbacks<Cursor> {

    private var _binding: BottomSheetAddCustomerBinding? = null
    private val binding get() = _binding!!

    // Add a property to hold the customer being edited
    var customerToEdit: Customer? = null

    private var contactsAdapter: SimpleCursorAdapter? = null
    private var imageUri: Uri? = null
    private val CONTACTS_LOADER_ID = 1001

    // Customer data listener interface
    interface CustomerDataListener {
        fun onCustomerSaved(
            firstName: String,
            lastName: String,
            phone: String,
            customerId: String,
            streetAddress: String,
            city: String,
            state: String,
            imageUri: Uri
        )

        fun handleCustomerDeletion(customer: Customer, position: Int)
        fun handleCustomerEdit(customer: Customer)
    }

    private var customerDataListener: CustomerDataListener? = null


    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Add this line to get persistable permissions
            requireContext().contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            imageUri = it
            binding.ivProfilePhoto.setImageURI(it)
            checkForPP()
        }
    }


    // Permission request launcher
    private val requestContactsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupContactsAutocomplete()
        } else {
            Toast.makeText(context, "Contacts permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerToEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_CUSTOMER, Customer::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(ARG_CUSTOMER)
            }
        }
    }

    override fun getTheme(): Int = R.style.ThemeOverlay_App_BottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        // Check for contacts permission
        checkContactsPermission()
        // Restore data if available
        // If we're editing, populate the fields
        if (savedInstanceState == null) {
            customerToEdit?.let { populateFields(it) }
        } else {
            restoreData(savedInstanceState)
        }
    }


    private fun populateFields(customer: Customer) {
        // Split the name into first and last name
        val nameParts = customer.name.split(" ")
        val firstName = nameParts.firstOrNull() ?: ""
        val lastName = nameParts.drop(1).joinToString(" ")

        binding.etFirstName.setText(firstName)
        binding.etLastName.setText(lastName)
        binding.etPhone.setText(customer.number)
        binding.etCustomerId.setText(customer.customerId)

        // Split address into components if possible
        val addressLines = customer.address.split(",")
        binding.etStreet.setText(addressLines.firstOrNull()?.trim() ?: customer.address)

        // Try to parse city and state from address2
        val cityStateParts = customer.address2.split(",")
        binding.etCity.setText(cityStateParts.firstOrNull()?.trim() ?: "")
        binding.etState.setText(cityStateParts.getOrNull(1)?.trim() ?: "")

        // Load image if available
        if (customer.imageUrl.isNotEmpty()) {
            try {
                imageUri = Uri.parse(customer.imageUrl)
                binding.ivProfilePhoto.setImageURI(imageUri)
                checkForPP()
            } catch (e: Exception) {
                // Handle error loading image
                binding.ivProfilePhoto.setImageResource(R.drawable.account_circle_24px)
            }
        }

        // Update the save button text to indicate editing
        binding.btnSave.text = "Update"
    }





    private fun checkContactsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                setupContactsAutocomplete()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                // Show rationale dialog explaining why we need contacts permission
                Toast.makeText(
                    context,
                    "Contacts permission is needed to suggest contacts as you type",
                    Toast.LENGTH_LONG
                ).show()
                requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }

            else -> {
                requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }


    private fun setupContactsAutocomplete() {
        val autoCompleteView = binding.etFirstName as? AutoCompleteTextView
        if (autoCompleteView == null) {
            Toast.makeText(context, "AutoCompleteTextView not available", Toast.LENGTH_SHORT).show()
            return
        }


        // Define columns to retrieve from ContactsProvider
        val from = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val to = intArrayOf(android.R.id.text1)

        // Create adapter
        contactsAdapter = SimpleCursorAdapter(
            requireContext(),
            R.layout.dropdown_item,
            null,
            from,
            to,
            0
        )

        autoCompleteView.threshold = 1
        autoCompleteView.setAdapter(contactsAdapter)

        // Set up query provider for filtering contacts
        contactsAdapter?.filterQueryProvider = FilterQueryProvider { constraint ->
            if (constraint.isNullOrEmpty()) return@FilterQueryProvider null

            val uri = ContactsContract.Contacts.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            val selection =
                "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ? AND " +
                        "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1"
            val selectionArgs = arrayOf("%$constraint%")
            val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"

            return@FilterQueryProvider context?.contentResolver?.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
        }

        // Handle contact selection
        autoCompleteView.setOnItemClickListener { _, _, position, _ ->
            val cursor = contactsAdapter?.cursor
            cursor?.let {
                if (it.moveToPosition(position)) {
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)

                    if (nameIndex != -1 && idIndex != -1) {
                        val contactName = it.getString(nameIndex)
                        val contactId = it.getString(idIndex)

                        val nameParts = contactName.split(" ")
                        val firstName = nameParts.firstOrNull() ?: ""
                        val lastName = nameParts.drop(1).joinToString(" ")

                        binding.etFirstName.setText(firstName)
                        binding.etLastName.setText(lastName)

                        // Load phone number and profile photo
                        loadContactPhoneNumber(contactId)
                        loadContactPhoto(contactId)
                    }
                }
            }
        }


        binding.etFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.etFirstName.top)
                }
            }
        }
    }

    /**
     * Loads the contact's profile photo into ImageView
     */
    private fun loadContactPhoto(contactId: String) {
        val uri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        val projection = arrayOf(ContactsContract.Contacts.PHOTO_URI)

        context?.contentResolver?.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val photoUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
                val photoUri = if (photoUriIndex != -1) cursor.getString(photoUriIndex) else null

                if (!photoUri.isNullOrEmpty()) {
                    imageUri = Uri.parse(photoUri)
                    binding.ivProfilePhoto.setImageURI(Uri.parse(photoUri))
                    checkForPP()
                } else {
                    // Set a default placeholder if no image is available
                    binding.ivProfilePhoto.setImageResource(R.drawable.account_circle_24px)
                }
            }
        }
    }

    private fun checkForPP() {
        if (imageUri.toString().isNotEmpty()) {
            binding.fabAddPhoto.visibility = View.GONE
        } else if (imageUri.toString().isEmpty()) {
            binding.fabAddPhoto.visibility = View.VISIBLE
        }
    }


    private fun loadContactPhoneNumber(contactId: String) {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId)

        context?.contentResolver?.query(
            uri,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (numberIndex != -1) {
                    val phoneNumber = cursor.getString(numberIndex)
                    binding.etPhone.setText(phoneNumber)
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Setup photo selection
        binding.fabAddPhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Setup cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // Setup save button
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveCustomerData()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate first name
        if (binding.etFirstName.text.isNullOrBlank()) {
            binding.etFirstName.error = "First name is required"
            isValid = false
        }

        // Validate phone number
        if (binding.etPhone.text.isNullOrBlank()) {
            binding.etPhone.error = "Phone number is required"
            isValid = false
        }

        // Validate address
        if (binding.etStreet.text.isNullOrBlank()) {
            binding.etStreet.error = "Street address is required"
            isValid = false
        }

        // Validate phone number
        if (binding.etCity.text.isNullOrBlank()) {
            binding.etCity.error = "City is required"
            isValid = false
        }
        // Validate phone number
        if (binding.etState.text.isNullOrBlank()) {
            binding.etState.error = "State is required"
            isValid = false
        }

        return isValid
    }

    private fun saveCustomerData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val phone = binding.etPhone.text.toString()
        val customerId = binding.etCustomerId.text.toString()
        val streetAddress = binding.etStreet.text.toString()
        val city = binding.etCity.text.toString()
        val state = binding.etState.text.toString()

        // Generate customer ID if blank
        val finalCustomerId = if (customerId.isEmpty()) {
            generateCustomerId(firstName, lastName)
        } else {
            customerId
        }


        if (imageUri == null || imageUri.toString().isEmpty()) {
            imageUri =
                Uri.parse("android.resource://${context?.packageName}/${R.drawable.account_circle_24px}")
        }





        // Pass data back to the listener
        customerDataListener?.onCustomerSaved(
            firstName, lastName, phone, finalCustomerId,
            streetAddress, city, state, imageUri!!
        )

        Toast.makeText(
            context,
            if (customerToEdit == null) "Customer saved successfully" else "Customer updated successfully",
            Toast.LENGTH_SHORT
        ).show()
        dismiss()
    }


    private fun generateCustomerId(firstName: String, lastName: String): String {
        val firstInitial = if (firstName.isNotEmpty()) firstName[0] else ""
        val lastInitial = if (lastName.isNotEmpty()) lastName[0] else ""
        val timestamp = System.currentTimeMillis().toString().takeLast(6)
        return "${firstInitial}${lastInitial}$timestamp".uppercase()
    }

    private fun restoreData(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            binding.etFirstName.setText(it.getString(KEY_FIRST_NAME, ""))
            binding.etLastName.setText(it.getString(KEY_LAST_NAME, ""))
            binding.etPhone.setText(it.getString(KEY_PHONE, ""))
            binding.etCustomerId.setText(it.getString(KEY_CUSTOMER_ID, ""))
            binding.etStreet.setText(it.getString(KEY_STREET, ""))
            binding.etCity.setText(it.getString(KEY_CITY, ""))
            binding.etState.setText(it.getString(KEY_STATE, ""))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_FIRST_NAME, binding.etFirstName.text.toString())
        outState.putString(KEY_LAST_NAME, binding.etLastName.text.toString())
        outState.putString(KEY_PHONE, binding.etPhone.text.toString())
        outState.putString(KEY_CUSTOMER_ID, binding.etCustomerId.text.toString())
        outState.putString(KEY_STREET, binding.etStreet.text.toString())
        outState.putString(KEY_CITY, binding.etCity.text.toString())
        outState.putString(KEY_STATE, binding.etState.text.toString())
    }

    fun setCustomerDataListener(listener: CustomerDataListener) {
        this.customerDataListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // We're not using the loader callbacks anymore, but keeping the interface
    // implementation for future expansion if needed
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        throw UnsupportedOperationException("Loader not used in this implementation")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // Not used
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Not used
    }

    companion object {
        private const val KEY_FIRST_NAME = "key_first_name"
        private const val KEY_LAST_NAME = "key_last_name"
        private const val KEY_PHONE = "key_phone"
        private const val KEY_CUSTOMER_ID = "key_customer_id"
        private const val KEY_STREET = "key_street"
        private const val KEY_CITY = "key_city"
        private const val KEY_STATE = "key_state"
        private const val ARG_CUSTOMER = "arg_customer"

        fun newInstance(): CustomerBottomSheetFragment {
            return CustomerBottomSheetFragment()
        }
        fun newInstance(customer: Customer): CustomerBottomSheetFragment {
            val fragment = CustomerBottomSheetFragment()
            val args = Bundle()
            args.putParcelable(ARG_CUSTOMER, customer)
            fragment.arguments = args
            return fragment
        }
    }
}