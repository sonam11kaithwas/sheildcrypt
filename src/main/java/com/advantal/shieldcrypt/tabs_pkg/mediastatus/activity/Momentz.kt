package com.advantal.shieldcrypt.tabs_pkg.mediastatus.activity

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentStatusBinding
import com.advantal.shieldcrypt.databinding.ProgressStoryViewBinding
import java.lang.Exception


/**
 * Created by Prashant Lal on 01-11-2022 20:52.
 */
class Momentz : ConstraintLayout {
    private var currentlyShownIndex = 0
    private lateinit var currentView: View
    private var momentzViewList: List<MomentzView>
    private var libSliderViewList = mutableListOf<MyProgressBar>()
    private var momentzCallback : MomentzCallback
    private lateinit var view: View
    lateinit var binding: ProgressStoryViewBinding
    private val passedInContainerView: ViewGroup
    private var mProgressDrawable : Int = R.drawable.green_lightgrey_drawable
    private var pausedState : Boolean = false
    lateinit var gestureDetector: GestureDetector

    constructor(
        context: Context,
        momentzViewList: List<MomentzView>,
        passedInContainerView: ViewGroup,
        momentzCallback: MediaStoryStatus,
        @DrawableRes mProgressDrawable: Int = R.drawable.green_lightgrey_drawable
    ) : super(context) {
        this.momentzViewList = momentzViewList
        this.momentzCallback = momentzCallback
        this.passedInContainerView = passedInContainerView
        this.mProgressDrawable = mProgressDrawable
        initView()
        init()
    }

    private fun init() {
        momentzViewList.forEachIndexed { index, sliderView ->
            val myProgressBar = MyProgressBar(
                context,
                index,
                sliderView.durationInSeconds,
                object : ProgressTimeWatcher {
                    override fun onEnd(indexFinished: Int) {
                        currentlyShownIndex = indexFinished + 1
                        next()
                    }
                },
                mProgressDrawable)
            libSliderViewList.add(myProgressBar)
            binding.linearProgressIndicatorLay.addView(myProgressBar)
        }
        //start()
    }

    fun callPause(pause : Boolean){
        try {
            if(pause){
                if(!pausedState){
                    this.pausedState = !pausedState
                    pause(false)
                }
            } else {
                if(pausedState){
                    this.pausedState = !pausedState
                    resume()
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private fun initView() {
        view = inflate(context, R.layout.progress_story_view, this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        gestureDetector = GestureDetector(context, SingleTapConfirm())

        val touchListener = object  : OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (gestureDetector.onTouchEvent(event)) {
                    // single tap
                    if(v?.id == binding.rightLay.id){
                        next()
                    } else if(v?.id == binding.leftLay.id){
                        prev()
                    }
                    return true
                } else {
                    // your code for move and drag
                    when(event?.action){
                        MotionEvent.ACTION_DOWN -> {
                            callPause(true)
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            callPause(false)
                            return true
                        }
                        else -> return false
                    }
                }
            }
        }

//        view.leftLay.setOnClickListener { prev() }
//        view.rightLay.setOnClickListener { next() }
        binding.leftLay.setOnTouchListener(touchListener)
        binding.rightLay.setOnTouchListener(touchListener)
        //view.container.setOnTouchListener(touchListener)

        this.layoutParams = params
        passedInContainerView.addView(this)


    }

    fun show() {
        binding.loaderProgressbar.visibility = GONE
        if (currentlyShownIndex != 0) {
            for (i in 0..Math.max(0, currentlyShownIndex - 1)) {
                libSliderViewList[i].progress = 100
                libSliderViewList[i].cancelProgress()
            }
        }

        if (currentlyShownIndex != libSliderViewList.size - 1) {
            for (i in currentlyShownIndex + 1..libSliderViewList.size - 1) {
                libSliderViewList[i].progress = 0
                libSliderViewList[i].cancelProgress()
            }
        }

        currentView = momentzViewList[currentlyShownIndex].view

        libSliderViewList[currentlyShownIndex].startProgress()

        momentzCallback.onNextCalled(currentView, this, currentlyShownIndex)

        binding.currentlyDisplayedView.removeAllViews()
        binding.currentlyDisplayedView.addView(currentView)
        val params = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT, 1f
        )
        //params.gravity = Gravity.CENTER_VERTICAL
        if(currentView is ImageView) {
            (currentView as ImageView).scaleType = ImageView.ScaleType.FIT_CENTER
            (currentView as ImageView).adjustViewBounds = true
        }
        currentView.layoutParams = params
    }

    fun start() {
//            Handler().postDelayed({
//                show()
//            }, 2000)
        show()
    }

    fun editDurationAndResume(index: Int, newDurationInSecons : Int){
        binding.loaderProgressbar.visibility = GONE
        libSliderViewList[index].editDurationAndResume(newDurationInSecons)
    }

    fun pause(withLoader : Boolean) {
        if(withLoader){
            binding.loaderProgressbar.visibility = VISIBLE
        }
        libSliderViewList[currentlyShownIndex].pauseProgress()
        if(momentzViewList[currentlyShownIndex].view is VideoView){
            (momentzViewList[currentlyShownIndex].view as VideoView).pause()
        }
    }

    fun resume() {
        binding.loaderProgressbar.visibility = GONE
        libSliderViewList[currentlyShownIndex].resumeProgress()
        if(momentzViewList[currentlyShownIndex].view is VideoView){
            (momentzViewList[currentlyShownIndex].view as VideoView).start()
        }
    }

    private fun stop() {

    }

    fun next() {
        try {
            if (currentView == momentzViewList[currentlyShownIndex].view) {
                currentlyShownIndex++
                if (momentzViewList.size <= currentlyShownIndex) {
                    finish()
                    return
                }
            }
            show()
        } catch (e: IndexOutOfBoundsException) {
            finish()
        }
    }

    private fun finish() {
        momentzCallback.done()
        for (progressBar in libSliderViewList) {
            progressBar.cancelProgress()
            progressBar.progress = 100
        }
    }

    fun prev() {
        try {
            if (currentView == momentzViewList[currentlyShownIndex].view) {
                currentlyShownIndex--
                if (0 > currentlyShownIndex) {
                    currentlyShownIndex = 0
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            currentlyShownIndex -= 2
        } finally {
            show()
        }
    }

    private inner class SingleTapConfirm : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }


}