package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.document_pkg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityDocumentPickerBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.document_pkg.doc_model_pkg.DocumentModel
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.CallbackAlertDialog
import com.advantal.shieldcrypt.utils_pkg.MyApp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DocumentPickerActivity : AppCompatActivity(), DocumentPickerAdapter.DocumentSelected,
    View.OnClickListener {
    var filesToBeDisplayed = ArrayList<File>()
    var filesList = ArrayList<DocumentModel>()
    lateinit var binding: ActivityDocumentPickerBinding
    private var adapter: DocumentPickerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllFiles(Environment.getExternalStorageDirectory())


        viewsInitializes()
    }

    fun filtercallsList(query: String) {
        adapter?.filter?.filter(query)
    }

    private fun viewsInitializes() {
        adapter = DocumentPickerAdapter(applicationContext, filesList, this)

        binding.recycleDocument.layoutManager = LinearLayoutManager(this)
        binding.recycleDocument.adapter = adapter

        getSelectedDocuments()


        binding.searchLayout.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtercallsList(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun passProductAsResult() {
        val data = Intent()
        data.putExtra("product", "product.toString()")

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun getAllFiles(parentDir: File?) {

        // Recursive function to list all files of the phone.
        try {
            if (parentDir != null) {
                val files = parentDir.listFiles()
                if (files != null) {
                    for (file in files) {
                        if (file.isDirectory) {
                            // If the current file is a directory function is called again.
                            getAllFiles(file)
                        } else {
//                            for (type in extensionToBeDisplayed) {
                            // the extension of file is checked corresponding the checkbox enabled at that time
                            if (file.name.endsWith(".pdf")) {
                                Log.v("ge", file.path)
                                val lastDate = Date(file.lastModified())


                                var exte: String
                                var mylength = file.length()
                                var fileSizeKb = mylength / 1024
                                exte = if (fileSizeKb < 1024) {
                                    " KB"
                                } else {
                                    fileSizeKb /= 1024
                                    " MB"
                                }


                                var documentModel = DocumentModel(
                                    "$fileSizeKb $exte",
                                    (SimpleDateFormat("MM/dd/yyyy").format(lastDate)),
                                    file.name,
                                    file.path
                                )

                                filesToBeDisplayed.add(file)
                                filesList.add(documentModel)
                            }
//                            }
//                            val s: String = FilesToBeDisplayed.size.toString() + ""
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return
    }

    override fun getSelectMultipleDoc(listSelected: ArrayList<String>) {
        listSelected.let {  MyApp.getAppInstance().showToastMsg("Document Selected") }
        getSelectedDocuments()
    }


    private fun getSelectedDocuments() {
        if (adapter?.selectedItemPositionList?.size == 0) {
            setSupportActionBar(binding.searchLayout.toolbar)

            supportActionBar?.title = "Document"
            binding.searchLayout.searchView.visibility = View.VISIBLE
            binding.searchLayout.threeDotsImg.visibility = View.VISIBLE
            binding.searchLayout.txtSend.visibility = View.GONE

        } else {
            var v = adapter?.selectedItemPositionList?.size.toString()

            setSupportActionBar(binding.searchLayout.toolbar)
            supportActionBar?.title = "$v Selected"

            binding.searchLayout.txtSend.visibility = View.VISIBLE
            binding.searchLayout.searchView.visibility = View.GONE
            binding.searchLayout.threeDotsImg.visibility = View.GONE

//            if (binding.searchLayout.searchBack.visibility == View.VISIBLE){
//
//            }
        }
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.txt_send -> AppUtills.setDialog(
                this,
                resources.getString(R.string.dialog_msg),
                resources.getString(R.string.ok),
                object : CallbackAlertDialog {
                    override fun onPossitiveCall() {

                    }

                    override fun onNegativeCall() {

                    }
                })
            R.id.three_dots_img ->  MyApp.getAppInstance().showToastMsg("Click")
            R.id.search_view -> hideShowTitleAndSearchBar(3)
            R.id.search_back -> hideShowTitleAndSearchBar(2)
            R.id.ic_backarrow -> hideShowTitleAndSearchBar(1)

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
                        getSelectedDocuments()
                    } else {
                        this.finish()
                    }
                }
            }
            2 -> {
                binding.searchLayout.searchLay.visibility = View.GONE
                binding.searchLayout.menuLayout.visibility = View.VISIBLE
            }
            3 -> {
                binding.searchLayout.searchLay.visibility = View.VISIBLE
                binding.searchLayout.menuLayout.visibility = View.GONE


            }
        }

    }


    override fun onBackPressed() {
        binding.searchLayout.searchEdt.setText("")

        if (binding.searchLayout.searchLay.visibility == View.VISIBLE || binding.searchLayout.txtSend.visibility == View.VISIBLE) {
            hideShowTitleAndSearchBar(1)
        } else //if (binding.searchLayout.txtSend.visibility == View.VISIBLE) {

            this.finish()
//        }
    }


}