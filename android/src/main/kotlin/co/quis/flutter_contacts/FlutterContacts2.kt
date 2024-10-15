package android.src.main.kotlin.co.quis.flutter_contacts

import android.app.Activity
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.GroupMembership
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.provider.ContactsContract.CommonDataKinds.Note
import android.provider.ContactsContract.CommonDataKinds.Organization
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.provider.ContactsContract.CommonDataKinds.Website
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.Groups
import android.provider.ContactsContract.RawContacts
import co.quis.flutter_contacts.AccountInfo
import co.quis.flutter_contacts.Contact
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream
import co.quis.flutter_contacts.properties.Account as PAccount
import co.quis.flutter_contacts.properties.Address as PAddress


//
class FlutterContacts2 {
    companion object {

        fun getAccountInfos(
            resolver: ContentResolver,
            unifiedContacts: Boolean,
        ): List<Map<String, Any?>> {

            // All fields we care about – ID and display name are always included.
            var projection = mutableListOf(
                Data.CONTACT_ID,
                Data.MIMETYPE,
                Contacts.DISPLAY_NAME_PRIMARY,
            )

            projection.addAll(listOf(StructuredPostal.FORMATTED_ADDRESS))

            projection.addAll(
                listOf(
                    Data.RAW_CONTACT_ID,
                    RawContacts.ACCOUNT_TYPE,
                    RawContacts.ACCOUNT_NAME
                )
            )

            var selectionArgs = arrayOf<String>()


            // Query contact database.
            val cursor = resolver.query(
                Data.CONTENT_URI,
                projection.toTypedArray(),
                null,
                selectionArgs,
                /*sortOrder=*/null
            )

            // List of all contacts.
            var contacts = mutableListOf<Contact>()
            if (cursor == null) {
                return listOf()
            }

            // Maps contact ID to its index in `contacts`.
            var index = mutableMapOf<String, Int>()

            fun getString(col: String): String = cursor.getString(cursor.getColumnIndex(col)) ?: ""
            fun getInt(col: String): Int = cursor.getInt(cursor.getColumnIndex(col)) ?: 0
            fun getBool(col: String): Boolean = getInt(col) == 1

            while (cursor.moveToNext()) {
                // ID and display name.
                val id = if (unifiedContacts) getString(Data.CONTACT_ID) else getString(Data.RAW_CONTACT_ID)
                if (id !in index) {
                    var contact = Contact(
                        /*id=*/id,
                        /*displayName=*/getString(Contacts.DISPLAY_NAME_PRIMARY),
                    )

                    index[id] = contacts.size
                    contacts.add(contact)
                }
                var contact: Contact = contacts[index[id]!!]

                // The MIME type of the data in current row (e.g. phone, email, etc).
                val mimetype = getString(Data.MIMETYPE)

                // Raw IDs are IDs of the contact in different accounts (e.g. the
                // same contact might have Google, WhatsApp and Skype accounts, each
                // with its own raw ID).
                val rawId = getString(Data.RAW_CONTACT_ID)
                val accountType = getString(RawContacts.ACCOUNT_TYPE)
                val accountName = getString(RawContacts.ACCOUNT_NAME)
                var accountSeen = false
                for (account in contact.accounts) {
                    if (account.rawId == rawId) {
                        accountSeen = true
                        account.mimetypes =
                            (account.mimetypes + mimetype).toSortedSet().toList()
                    }
                }
                if (!accountSeen) {
                    val account = PAccount(
                        rawId,
                        accountType,
                        accountName,
                        listOf(mimetype)
                    )
                    contact.accounts += account
                }

                // All properties (phones, emails, etc).
                when (mimetype) {
                    StructuredPostal.CONTENT_ITEM_TYPE -> {
                        val address = PAddress(
                            getString(StructuredPostal.FORMATTED_ADDRESS),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                        contact.addresses += address
                    }
                }
            }

            cursor.close()

            val accountInfoMap = mutableMapOf<String, AccountInfo>()

//            var accountInfos = mutableListOf<AccountInfo>()

            for (contact in contacts) {
                for (account in contact.accounts) {
                    // Générer l'accountId en combinant le nom et le type
                    val accountId = account.accountId;

                    // Vérifier si l'accountId existe déjà dans le map
                    val currentAccountInfo = accountInfoMap[accountId]

                    if (currentAccountInfo != null) {
                        // Si l'account existe, incrémenter contactCount et addressCount
                        currentAccountInfo.contactCount += 1
                        currentAccountInfo.addressCount += contact.addresses.size
                    } else {
                        // Sinon, créer un nouvel AccountInfo et l'ajouter au map
                        accountInfoMap[accountId] = AccountInfo(
                            name = account.name,
                            type = account.type,
                            mimetypes = account.mimetypes,
                            accountId = accountId,
                            contactCount = 1, // Premier contact associé à ce compte
                            addressCount = contact.addresses.size // Nombre d'adresses pour ce contact
                        )
                    }
                }
            }


//            return accountInfos.map { it.toMap() }
            return accountInfoMap.values.map { it.toMap() }

        }
    }
}


