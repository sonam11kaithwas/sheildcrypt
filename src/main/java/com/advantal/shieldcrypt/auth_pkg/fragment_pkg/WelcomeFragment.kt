package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.core.content.ContextCompat
import androidx.core.text.underline
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentWelcomeBinding
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences


class WelcomeFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentWelcomeBinding
    var isAutoLoginStatus = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        isAutoLoginStatus = MySharedPreferences.getSharedprefInstance().getAutoLoginStatus()
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding.agreeContinueBtn.setOnClickListener {
            /*Old code*/
            Navigation.findNavController(it).navigate(R.id.action_welcomeFragment_to_userlogin)
        }
        customTextView(binding.termsPolicyTxt)

        binding.termsPolicyTxt.setOnClickListener {}

        if (isAutoLoginStatus) {
            findNavController().navigate(R.id.action_welcomeFragment_to_tabLayoutFragment)
        }else{
//            preferences.edit().putBoolean("hide_offline", false).apply()

        }
        return binding.root

    }


    private fun customTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
            "Read our"
        )
        spanTxt.setSpan(ForegroundColorSpan(Color.WHITE), 0, spanTxt.length, 0)
        spanTxt.append("  Privacy Policy")
        spanTxt.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(requireActivity(), R.color.color_yellow)
            ), 9, spanTxt.length, 0
        )
// Comment by prashant on 28 dec 2022
//        spanTxt.setSpan(object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                Navigation.findNavController(widget)
//                    .navigate(R.id.action_welcomeFragment_to_webview)
//            }
//        }, 24 - " Privacy Policy".length, spanTxt.length, 0)


        spanTxt.append(".")
        spanTxt.setSpan(
            ForegroundColorSpan(Color.WHITE), spanTxt.length - 1, spanTxt.length, 0
        )

        spanTxt.append(" Tap  \"Agree and continue\" to accept the ")
        spanTxt.setSpan(
            ForegroundColorSpan(Color.WHITE), 26, spanTxt.length, 0
        )


        spanTxt.append(" Terms of Service")
        spanTxt.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(requireActivity(), R.color.color_yellow)
            ), spanTxt.length - 17, spanTxt.length, 0
        )

        spanTxt.underline {
            false
        }

        // Comment by prashant on 28 dec 2022
//        spanTxt.setSpan(object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                Toast.makeText(
//                    activity, " Terms of Service Clicked",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }, spanTxt.length - " Terms of Service".length, spanTxt.length, 0)


        spanTxt.append(".")
        spanTxt.setSpan(
            ForegroundColorSpan(Color.WHITE), spanTxt.length - 1, spanTxt.length, 0
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, BufferType.SPANNABLE)
    }

    private fun stripUnderlines(textView: TextView) {
        val s: Spannable = SpannableString(textView.text)
        val spans = s.getSpans(10, 20, URLSpan::class.java)
        for (span in spans) {
            val start = s.getSpanStart(span)
            val end = s.getSpanEnd(span)
            s.removeSpan(span)

            var spanss = URLSpanNoUnderline(span.url)

            s.setSpan(spanss, start, end, 0)
        }
        textView.text = s
    }


    class URLSpanNoUnderline(var clicked: String?) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.color = R.color.color_yellow
        }

        override fun onClick(widget: View) {

        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            com.advantal.shieldcrypt.R.id.my_text_view -> {
//                binding.termsPolicyTxt.makeLinks(
//                    Pair("Please", View.OnClickListener {
//                        Toast.makeText(activity, "Terms of Service Clicked", Toast.LENGTH_SHORT)
//                            .show()
//                    }),
//                    Pair("Privacy Policy", View.OnClickListener {
//                        Toast.makeText(activity, "Privacy Policy Clicked", Toast.LENGTH_SHORT)
//                            .show()
//                    })
//                )
//
//            }
        }
    }


}