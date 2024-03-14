package com.advantal.shieldcrypt.tabs_pkg.mediastatus.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.advantal.shieldcrypt.R


class MediaStoryStatus(override val container: ViewGroup) : AppCompatActivity(), MomentzCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_story_status)

        val internetLoadedImageView = ImageView(this)

        val listOfViews = listOf(
            MomentzView(internetLoadedImageView, 10)

        )

        Momentz(this, listOfViews, container, this).start()
    }

    override fun onNextCalled(view: View, momentz: Momentz, index: Int) {
//        if (view is VideoView) {
//            momentz.pause(true)
//            playVideo(view, index, momentz)
//        }
//        else
        if ((view is ImageView) && (view.drawable == null)) {
            momentz.pause(true)
            Glide.with(this)
                .load("https://i.pinimg.com/564x/14/90/af/1490afa115fe062b12925c594d93a96c.jpg")
                .into(view)
//                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .into(view, object : Callback {
//                    override fun onSuccess() {
//                        momentz.resume()
//                        Toast.makeText(this@MediaStoryStatus, "Image loaded from the internet", Toast.LENGTH_LONG).show()
//                    }
//
//                    override fun onError(e: Exception?) {
//                        Toast.makeText(this@MediaStoryStatus,e?.localizedMessage, Toast.LENGTH_LONG).show()
//                        e?.printStackTrace()
//                    }
//                })
        }
    }

    override fun done() {
        Toast.makeText(this@MediaStoryStatus, "Finished!", Toast.LENGTH_LONG).show()
    }
}