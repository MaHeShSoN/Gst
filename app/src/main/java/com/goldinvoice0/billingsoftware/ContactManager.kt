package com.goldinvoice0.billingsoftware

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment


data class Contact(
    val id: String,
    val displayName: String,
    val phoneNumber: String?,
    val email: String?
)


class ContactManager(private val fragment: Fragment) {

    private var onPermissionResult: ((Boolean) -> Unit)? = null

    // Register the permission launcher in the Fragment's lifecycle
    private val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult?.invoke(isGranted)
    }

    fun getContacts(
        onSuccess: (List<Contact>) -> Unit,
        onError: (String) -> Unit
    ) {
        when {
            // Check if permission is already granted
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchContacts(onSuccess, onError)
            }
            // Show rationale if needed
            fragment.shouldShowRequestPermissionRationale(
                Manifest.permission.READ_CONTACTS
            ) -> {
                onError("Contacts permission is required to access contacts")
            }
            // Request permission
            else -> {
                onPermissionResult = { isGranted ->
                    if (isGranted) {
                        fetchContacts(onSuccess, onError)
                    } else {
                        onError("Permission denied")
                    }
                }
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun fetchContacts(
        onSuccess: (List<Contact>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val contacts = mutableListOf<Contact>()

            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            )

            fragment.requireContext().contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
                    val hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0

                    var phoneNumber: String? = null
                    var email: String? = null

                    // Get phone number if available
                    if (hasPhoneNumber) {
                        fragment.requireContext().contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(id),
                            null
                        )?.use { phoneCursor ->
                            if (phoneCursor.moveToFirst()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                        }
                    }

                    // Get email if available
                    fragment.requireContext().contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.DATA),
                        "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                        arrayOf(id),
                        null
                    )?.use { emailCursor ->
                        if (emailCursor.moveToFirst()) {
                            email = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA))
                        }
                    }

                    contacts.add(Contact(id, displayName, phoneNumber, email))
                }
            }

            onSuccess(contacts)

        } catch (e: Exception) {
            onError("Error fetching contacts: ${e.localizedMessage}")
        }
    }
}