package com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.WhatsappContactActivityBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.qr_code_pkg.QRCodeActivity
import com.advantal.shieldcrypt.service.SyncMyContacts2
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.WhatsAppContactAdpter.ContactSelected
import com.advantal.shieldcrypt.utils_pkg.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase

@AndroidEntryPoint
class WhatsAppContactsActivity : AppCompatActivity(), ContactSelected, View.OnClickListener {
    lateinit var binding: WhatsappContactActivityBinding

    val mainViewModel: MainViewModel by viewModels()
    private var adapter: WhatsAppContactAdpter? = null
    private val syncingService: SyncMyContacts2 by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WhatsappContactActivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        setSupportActionBar(binding.searchLayout.toolbar)

        supportActionBar?.title = getString(R.string.selected_contact)

        syncingService.refreshListCallBack.observe(this, Observer {
            binding.progressBar.visibility = View.GONE
            val myContact: MutableList<ContactDataModel>? =
                MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.contactDao()
                    ?.getAllNotes(
                        MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
                    )

            myContact?.add(0, (ContactDataModel(0, "A", "A", "", "", "", false)))
            adapter?.addList(myContact)
            adapter?.notifyDataSetChanged()
        })

        syncingService.refreshListCallBackError.observe(this, Observer {
            binding.progressBar.visibility = View.GONE
            MyApp.getAppInstance().showToastMsg(it)
        })

        setContactDataInList()

        mainViewModel.responceCallBack.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtills.closeProgressDialog()
                    it.data?.let { users ->
                        Log.e("ssssssssssssss:", "ssssssssssssss:---data")

                        when (users.requestCode) {
                            RequestApis.USER_LOGIN_REQ -> {

                            }
                        }
                    }
                }

                Status.LOADING -> {
                    AppUtills.setProgressDialog(this)
                }
                Status.ERROR -> {
                    AppUtills.closeProgressDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
        showContacts()

//        binding.searchLayout.searchEdt.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//            }
//
//        })

        binding.searchLayout.searchEdt.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                filterCallList(it.toString())
            } else {
                adapter?.listReload()
            }
        }


    }

    private fun filterCallList(query: String) {
        adapter?.filter?.filter(query)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (allPermissionsGranted(arrayOf(Manifest.permission.READ_CONTACTS))) {
                setContactDataInList()
            } else {
                AppUtills.setDialog(this,
                    "Permissions required for Contact",
                    resources.getString(R.string.ok),
                    object : CallbackAlertDialog {
                        override fun onPossitiveCall() {
                            showContacts()
                        }

                        override fun onNegativeCall() {

                        }
                    })
                finish()
            }
        } else if (requestCode == AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA) {
            if (allPermissionsGranted(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                )
            ) {
                openQrCode()
            } else {
                AppUtills.setDialog(this,
                    "Permissions required for QR code",
                    resources.getString(R.string.ok),
                    object : CallbackAlertDialog {
                        override fun onPossitiveCall() {
                            cameraPermission()
                        }

                        override fun onNegativeCall() {

                        }
                    })
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allPermissionsGranted(arrayOfPermission: Array<String>) = arrayOfPermission.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Show the contacts in the ListView.
     */
    private fun showContacts() =
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS
            )
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.

            setContactDataInList()

        }

    private fun setContactDataInList() {
//        var contactModelList: ArrayList<ContactModel>? =
//            displayWhatsAppContacts()
//         var mList: List<ContactDataModel>?
//         mList.add
        val myContact: MutableList<ContactDataModel>? =
            MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.contactDao()
                ?.getAllNotes(
                    MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
                )

        myContact?.add(0, (ContactDataModel(0, "A", "A", "", "", "", false)))
//        myContact?.add(1, (ContactDataModel(1, "A", "A", "", "", "", false)))

        binding.whatsapContactRecycler.layoutManager = LinearLayoutManager(this)

        adapter = WhatsAppContactAdpter(
            applicationContext, myContact, this
        )

        binding.whatsapContactRecycler.adapter = adapter
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.three_dots_img -> {
                binding.progressBar.visibility = View.VISIBLE
                syncingService.callApp()
//                showCustomPopUp()
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

    override fun onBackPressed() {
        if (binding.searchLayout.searchLay.visibility == View.VISIBLE || binding.searchLayout.txtSend.visibility == View.VISIBLE) {
            hideShowTitleAndSearchBar(1)
        } else //if (binding.searchLayout.txtSend.visibility == View.VISIBLE) {

            this.finish()
//        }
    }

    private fun showCustomPopUp() {
        var mypopupWindow: PopupWindow
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val view: ViewGroup = inflater.inflate(
            R.layout.contact_popup_layout, null, false
        ) as ViewGroup


        mypopupWindow = PopupWindow(
            view, 360, RelativeLayout.LayoutParams.WRAP_CONTENT, true
        )
        var new_group = view.findViewById(R.id.contact) as TextView
        var refresh = view.findViewById(com.advantal.shieldcrypt.R.id.refresh) as TextView
        var help = view.findViewById(com.advantal.shieldcrypt.R.id.help) as TextView


        new_group.setOnClickListener {
            Toast.makeText(
                this@WhatsAppContactsActivity, "Contact", Toast.LENGTH_SHORT
            ).show()
            mypopupWindow.dismiss()
        }

        refresh.setOnClickListener {
            Toast.makeText(
                this@WhatsAppContactsActivity, "Refresh", Toast.LENGTH_SHORT
            ).show()
            mypopupWindow.dismiss()
        }


        help.setOnClickListener {
            Toast.makeText(
                this@WhatsAppContactsActivity, "Help", Toast.LENGTH_SHORT
            ).show()
            mypopupWindow.dismiss()
        }


        mypopupWindow.showAtLocation(
            binding.searchLayout.searchView, Gravity.TOP, 300, 230
        )
        mypopupWindow.contentView.setOnClickListener {


            mypopupWindow.dismiss()

        }


    }

    private fun hideShowTitleAndSearchBar(type: Int) {
        when (type) {
            1 -> {
                if (binding.searchLayout.searchLay.visibility == View.VISIBLE) {
                    binding.searchLayout.searchLay.visibility = View.GONE
                    binding.searchLayout.menuLayout.visibility = View.VISIBLE
                } else {
                    if (binding.searchLayout.txtSend.visibility == View.VISIBLE) {
                        adapter?.selectedItemPositionList?.clear()
                        adapter?.notifyDataSetChanged()
                    } else {
                        this.finish()
                    }
                }
            }
            2 -> {
                binding.searchLayout.searchLay.visibility = View.GONE
                binding.searchLayout.menuLayout.visibility = View.VISIBLE
                adapter?.listReload()
                closeKeyboard(this, binding.searchLayout.searchEdt)
            }
            3 -> {
                binding.searchLayout.searchLay.visibility = View.VISIBLE
                binding.searchLayout.menuLayout.visibility = View.GONE
            }
        }

    }


/*
    private fun displayWhatsAppContacts(): ArrayList<ContactModel>? {


        val crs = contentResolver

        val contactCursor = crs.query(
            RawContacts.CONTENT_URI, arrayOf(RawContacts._ID, RawContacts.CONTACT_ID),
            RawContacts.ACCOUNT_TYPE + "= ?", arrayOf("com.whatsapp"), null
        )



        if (contactCursor != null && contactCursor.moveToFirst()) {
            var whatsappContactId =
                contactCursor.getString(contactCursor.getColumnIndex(RawContacts.CONTACT_ID))
            val photoId: Uri =
                ContentUris.withAppendedId(RawContacts.CONTENT_URI, whatsappContactId.toLong())
            val photoUri: Uri =
                Uri.withAppendedPath(photoId, RawContacts.DisplayPhoto.CONTENT_DIRECTORY)

        }


        val contacts: ArrayList<ContactModel> = ArrayList()
        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.PHOTO_URI,
            "account_type",
            ContactsContract.Data.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
        )
        val selection = ContactsContract.Data.MIMETYPE + " =? and account_type=?"
        val selectionArgs = arrayOf(
            "vnd.android.cursor.item/vnd.com.whatsapp.profile",
            "com.whatsapp"
        )
        val cr = contentResolver
        val c: Cursor? = cr.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var number: String? = ""


        while (c?.moveToNext() == true) {
            val id: String = c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID))
            val name: String = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            Log.v("WhatsApp", "name $name - number - $number")


            if (c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    .toInt() > 0
            ) {
                // Query phone here. Covered next
                val phones = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null,
                    null
                )
                while (phones!!.moveToNext()) {
                    val phoneNumber =
                        phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.i("Number", phoneNumber)
                    number = phoneNumber
                }
                phones.close()
            }


            val photoId: Uri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, id.toLong())
            val myphotoUris: Uri =
                Uri.withAppendedPath(photoId, RawContacts.DisplayPhoto.CONTENT_DIRECTORY)
//            Log.e("IMAGES----", myphotoUris.toString())
//            var photoUri :String=""
//            photoUris.let {
//                photoUri=photoUris.toString()
//
//            }

            val photoUri =
                if (c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)) != null) {
                    c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                } else {
                    ""
                }

//            Log.e("photoUri", photoUri)
            contacts.add(
                ContactModel(
                    name,
                    number!!,
                    myphotoUris.toString()
                )
            )
        }
//        Log.v("WhatsApp", "Total WhatsApp Contacts: " + c?.count)
        c?.close()
        return contacts
    }
*/

    override fun getSelectMultipleCon(contactDataModel: ContactDataModel) {
        val intent = Intent(this@WhatsAppContactsActivity, ChatsActivity::class.java)
        intent.putExtra("chatUser", Gson().toJson(contactDataModel))
        startActivity(intent)
    }


    private fun openQrCode() {
        var intent = Intent(this@WhatsAppContactsActivity, QRCodeActivity::class.java)
        startActivity(intent)
    }

    override fun getQrCodeScanner() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermission()
        } else {
            openQrCode()
        }
    }

    override fun getNewGrpraete() {
    }


    private fun cameraPermission() =
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA
            )
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            openQrCode()
        }

    private fun closeKeyboard(context: Context, view: View?) {
        //val view = this.currentFocus
        if (view != null) {
            Handler().postDelayed({
                val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }, 200)
        }
    }

}