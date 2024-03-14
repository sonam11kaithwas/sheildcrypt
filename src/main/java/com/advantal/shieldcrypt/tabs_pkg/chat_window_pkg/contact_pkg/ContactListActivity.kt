package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.contact_pkg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityContactListBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.contact_pkg.contact_model.ContactModel
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg.ViewContactActivity
import com.advantal.shieldcrypt.utils_pkg.AppConstants.Companion.PERMISSIONS_REQUEST_READ_CONTACTS
import com.advantal.shieldcrypt.utils_pkg.MyApp


@Suppress("IMPLICIT_CAST_TO_ANY")
class ContactListActivity : AppCompatActivity(), ContactListAdpter.ContactSelected,
    View.OnClickListener, ContactAlphBetsAdpter.SelectAlphbets {
    lateinit var binding: ActivityContactListBinding
    private var adapter: ContactListAdpter? = null
    private var contactAlphList = ArrayList<String>()


    /**
     * Show the contacts in the ListView.
     */
    private fun showContacts() =
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            val contactModelList: ArrayList<ContactModel>? = getContactNames()
            adapter = ContactListAdpter(applicationContext, contactModelList, this)

            binding.recycleContact.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recycleContact.adapter = adapter


            val myListAdapter = ContactAlphBetsAdpter(this, contactAlphList, this)
            binding.recycleAlphabets.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)



            binding.recycleAlphabets.adapter = myListAdapter


        }

    private fun getContactNames(): ArrayList<ContactModel>? {
        val contacts: ArrayList<ContactModel> = ArrayList()
        // Get the ContentResolver
//        val cr = contentResolver
        // Get the Cursor of all the contacts
        val cursor =
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.LABEL,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
                ),

                ContactsContract.Contacts.HAS_PHONE_NUMBER + ">0 AND LENGTH(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ")>0",
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME

            )

        // Move the cursor to first. Also check whether the cursor is empty or not.
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Iterate through the cursor
                do {
                    // Get the contacts name
                    val name: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val photoUri =
                        if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)) != null) {
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))
                        } else {
                            ""
                        }

                    contacts.add(
                        ContactModel(
                            name,
                            number,
                            photoUri
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        // Close the curosor
        cursor?.close()
        return contacts
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        contactAlphList.add("A")
        contactAlphList.add("B")
        contactAlphList.add("C")
        contactAlphList.add("D")
        contactAlphList.add("E")
        contactAlphList.add("F")
        contactAlphList.add("G")
        contactAlphList.add("H")
        contactAlphList.add("I")
        contactAlphList.add("J")
        contactAlphList.add("K")
        contactAlphList.add("L")
        contactAlphList.add("M")
        contactAlphList.add("N")
        contactAlphList.add("O")
        contactAlphList.add("P")
        contactAlphList.add("Q")
        contactAlphList.add("R")
        contactAlphList.add("S")
        contactAlphList.add("T")
        contactAlphList.add("U")
        contactAlphList.add("V")
        contactAlphList.add("W")
        contactAlphList.add("X")
        contactAlphList.add("Y")
        contactAlphList.add("Z")

//        getContactList()
        setActionBarViews()
//        showContacts()
    }

    private fun setActionBarViews() {
        binding.threeDotsImg.visibility = View.GONE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Contact"
        showContacts()
    }

    private fun updateACtionBar() {

        binding.threeDotsImg.visibility = View.GONE
        if (adapter != null && (adapter?.selectedItemPositionList?.size)!! > 0) {

            setSupportActionBar(binding.toolbar)
            supportActionBar?.title =
                adapter?.selectedItemPositionList?.size.toString() + " Selected"

            binding.txtSend.visibility = View.VISIBLE

            binding.searchView.visibility = View.GONE

        } else {

            setSupportActionBar(binding.toolbar)

            supportActionBar?.title = getString(R.string.contact_title)
            binding.searchView.visibility = View.VISIBLE
            binding.txtSend.visibility = View.GONE
        }


    }


    override fun getSelectMultipleCon(contactModel: ContactModel) {
        val intent = Intent(this@ContactListActivity, ViewContactActivity::class.java)
        intent.putExtra("chatUser", Gson().toJson(contactModel))
        startActivity(intent)

    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.contact_fab -> {
                 MyApp.getAppInstance().showToastMsg("Click")
                MyApp.getAppInstance().showToastMsg("Click")

            }
            R.id.three_dots_img -> {
                 MyApp.getAppInstance().showToastMsg("Click")
            }
            R.id.search_view -> {
                hideShowTitleAndSearchBar(3)

            }
            R.id.search_back -> {
                hideShowTitleAndSearchBar(2)
            }
            R.id.ic_backarrow -> {
                hideShowTitleAndSearchBar(1)
            }


        }

    }


    private fun hideShowTitleAndSearchBar(type: Int) {
        when (type) {
            1 -> {
                if (binding.searchLay.visibility == View.VISIBLE) {
                    binding.searchLay.visibility = View.GONE
                    binding.menuLayout.visibility = View.VISIBLE
                } else {
                    if (binding.txtSend.visibility == View.VISIBLE) {
                        adapter?.selectedItemPositionList?.clear()
                        adapter?.notifyDataSetChanged()
                    } else {
                        this.finish()
                    }
                }
            }
            2 -> {
                binding.searchLay.visibility = View.GONE
                binding.menuLayout.visibility = View.VISIBLE
            }
            3 -> {
                binding.searchLay.visibility = View.VISIBLE
                binding.menuLayout.visibility = View.GONE
            }
        }

    }


    override fun onBackPressed() {
        if (binding.searchLay.visibility == View.VISIBLE || binding.txtSend.visibility == View.VISIBLE) {
            hideShowTitleAndSearchBar(1)
        } else //if (binding.searchLayout.txtSend.visibility == View.VISIBLE) {

            this.finish()
//        }
    }


    override fun getSelectedAlphabets(alpha: String) {
//        adapter?.filter?.filter(alpha)
        Log.e("TEXT", alpha)

        for (row in adapter?.mList!!) {

            if (row.contactNm.lowercase()
                    .startsWith(alpha.toString().lowercase())
            ) {
                val index: Int = adapter?.mList!!.indexOf(row)
                Log.e("TEXT", row.contactNm)
                Log.e("TEXT", index.toString())

//                binding.recycleContact.smoothScrollToPosition(index)
                binding.recycleContact.scrollToPosition(index)

                break
            }
        }
    }


}