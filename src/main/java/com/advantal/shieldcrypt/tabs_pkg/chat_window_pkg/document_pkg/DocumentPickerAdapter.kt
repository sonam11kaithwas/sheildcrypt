package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.document_pkg

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.DocumentItemViewBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.document_pkg.doc_model_pkg.DocumentModel

/**
 * Created by Sonam on 30-06-2022 11:17.
 */
class DocumentPickerAdapter(
    val context: Context,
    private val mList: MutableList<DocumentModel>,
    var listener: DocumentSelected

) :
    RecyclerView.Adapter<DocumentPickerAdapter.ViewHolder>(), Filterable {

    var selectedItemPositionList = ArrayList<String>()
    var documentFilterList = ArrayList<DocumentModel>()

    init {
        documentFilterList = mList as ArrayList<DocumentModel>
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            DocumentItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val documentModel = documentFilterList[position]
        with(holder) {
            with(documentModel) {

                binding.txtName.text = this.name
                binding.txtSize.text = this.fileSize
                binding.pdfDate.text = this.fileCreateDate

                binding.relativeMain.setOnClickListener {
                    if (!selectedItemPositionList.contains(documentFilterList[position].path))
                        selectedItemPositionList.add(documentFilterList[position].path)
                    else {
                        selectedItemPositionList.remove(documentFilterList[position].path)
                    }
                    notifyDataSetChanged()

                    listener.getSelectMultipleDoc(selectedItemPositionList)
                }

                if (selectedItemPositionList.contains(documentFilterList[position].path))
                    binding.relativeMain.setBackgroundColor(Color.parseColor("#C2E9F8"))
                else
                    binding.relativeMain.setBackgroundColor(Color.parseColor("#F5F6F8"))

            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return documentFilterList.size
    }

    interface DocumentSelected {
        fun getSelectMultipleDoc(listSelected: ArrayList<String>)
    }

    class ViewHolder(val binding: DocumentItemViewBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                documentFilterList = if (charSearch.isEmpty()) {
                    mList as ArrayList<DocumentModel>
                } else {
                    val resultList = ArrayList<DocumentModel>()

                    for (row in mList) {
                        Log.e("Name: ", row.name)

                        if (row.name.lowercase()
                                .contains(constraint.toString().lowercase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = documentFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                documentFilterList = results?.values as ArrayList<DocumentModel>
                notifyDataSetChanged()
            }
        }
    }

}