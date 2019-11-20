package com.beyondthecode.eventsdl.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.beyondthecode.eventsdl.R
import com.beyondthecode.eventsdl.ui.signin.SignInDialogFragment
import com.beyondthecode.eventsdl.util.doOnApplyWindowInsets
import com.beyondthecode.shared.result.EventObserver
import com.beyondthecode.shared.util.inTransaction
import com.beyondthecode.shared.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class OnboardingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewModel = viewModelProvider(viewModelFactory)

        //immersive mode so images can draw behind the status bar
        val decor = window.decorView
        val flags = decor.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        decor.systemUiVisibility = flags

        val container: FrameLayout = findViewById(R.id.fragment_container)
        container.doOnApplyWindowInsets{ v, insets, padding ->
            v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
        }

        if(savedInstanceState == null){
            supportFragmentManager.inTransaction {
                add(R.id.fragment_container, OnboardingFragment())
            }
        }

        viewModel.navigateToSignInDialogAction.observe(this, EventObserver {
            openSignInDialog()
        })
    }

    private fun openSignInDialog(){
        SignInDialogFragment().show(supportFragmentManager, DIALOG_SIGN_IN)
    }

    companion object{
        private const val DIALOG_SIGN_IN = "dialog_sign_in"
    }
}
