package com.beyondthecode.eventsdl.ui.onboarding


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.beyondthecode.eventsdl.R
import dagger.android.support.DaggerFragment

/**
 * A simple [Fragment] subclass.
 */
class OnboardingSignInFragment : DaggerFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_signin, container, false)
    }


}
