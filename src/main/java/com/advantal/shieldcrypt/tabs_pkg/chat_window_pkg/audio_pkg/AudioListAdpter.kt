package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.audio_pkg

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.AudioItemViewBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.audio_pkg.audio_model.AudioModel

/**
 * Created by Sonam on 05-07-2022 14:32.
 */
class AudioListAdpter
    (
    val context: Context,
    private val mList: MutableList<AudioModel>,
    var listener: AudioSelected

) :
    RecyclerView.Adapter<AudioListAdpter.ViewHolder>(), Filterable {
    var audioFilterList = ArrayList<AudioModel>()
    var selectedItemPositionList = ArrayList<String>()
    private var audioPlayPos: Int = -1
    private var audioLever: Int = 1
    private var tempAudioPlayPos: Boolean = false
    private var prAudioPlayPos: Boolean = false


    init {
        audioFilterList = mList as ArrayList<AudioModel>
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            AudioItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(binding)
    }
    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audioModel = audioFilterList[position]
        with(holder) {
            with(audioModel) {

                binding.txtName.text = ""
                binding.txtSize.text = ""

                binding.displayName = this.displayname
                binding.audioFileSize = this.duration + "  " + this.size


                if (audioFilterList[position].audioPlayState.equals("0")){
                    binding.audioPlayImg.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_play_audio,
                            null))
                    }else{

                    binding.audioPlayImg.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.ic_audio_resume,
                            null))
                }

                binding.audioPlayImg.setOnClickListener {

                    Log.e("audioPlayPos",audioPlayPos.toString())

                    if (audioFilterList[position].audioPlayState.equals("0")) {

                        if (audioPlayPos!=-1)audioFilterList[audioPlayPos].audioPlayState = "0"

                        audioFilterList[position].audioPlayState = "1"
                    } else if (audioFilterList[position].audioPlayState.equals("1")) {
                        audioFilterList[position].audioPlayState = "0"
                    }


                    audioPlayPos = position
                    notifyDataSetChanged()

                    listener.playAudio(
                        audioFilterList[position].path,
                        audioFilterList[position].audioPlayState
                    )


                }


                binding.relativeMain.setOnClickListener {
                    if (!selectedItemPositionList.contains(audioFilterList[position].path))
                        selectedItemPositionList.add(audioFilterList[position].path)
                    else {
                        selectedItemPositionList.remove(audioFilterList[position].path)
                    }
                    notifyDataSetChanged()

                    listener.getSelectMultipleAudio(selectedItemPositionList)
                }

                if (selectedItemPositionList.contains(audioFilterList[position].path))
                    binding.relativeMain.setBackgroundColor(android.graphics.Color.parseColor("#C2E9F8"))
                else
                    binding.relativeMain.setBackgroundColor(android.graphics.Color.parseColor("#F5F6F8"))
            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return audioFilterList.size
    }

    interface AudioSelected {
        fun getSelectMultipleAudio(listSelected: ArrayList<String>)
        fun playAudio(playAudio: String, pauseResume: String)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                audioFilterList = if (charSearch.isEmpty()) {
                    mList as ArrayList<AudioModel>
                } else {
                    val resultList = ArrayList<AudioModel>()

                    for (row in mList) {
                        Log.e("Name: ", row.displayname)

                        if (row.displayname.lowercase()
                                .contains(constraint.toString().lowercase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = audioFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                audioFilterList = results?.values as ArrayList<AudioModel>
                notifyDataSetChanged()
            }
        }
    }


    class ViewHolder(val binding: AudioItemViewBinding) : RecyclerView.ViewHolder(binding.root)


}