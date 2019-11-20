package com.beyondthecode.eventsdl.ui.signin

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.beyondthecode.eventsdl.databinding.DialogSignInBinding
import com.beyondthecode.eventsdl.util.executeAfter
import com.beyondthecode.eventsdl.util.signin.SignInHandler
import com.beyondthecode.shared.result.EventObserver
import com.beyondthecode.shared.util.viewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

/**
 * Dialog that tells the user to sign in to continue the operation.
 * */
class SignInDialogFragment : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var signInHandler: SignInHandler

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var signInViewModel: SignInViewModel

    private lateinit var binding: DialogSignInBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /**We want tot create a dialog, but we also want to use DataBinding for the content view.
         * We can do that by making an empty dialog and adding the content later.
         */
        return MaterialAlertDialogBuilder(requireContext()).create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //In case we're showing as a dialog, use getLayoutInflater() instead.
        binding = DialogSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        signInViewModel = viewModelProvider(viewModelFactory)
        signInViewModel.performSignInEvent.observe(this, EventObserver { request ->
            if(request == SignInEvent.RequestSignIn){
                activity?.let{ activity ->
                    val signInIntent = signInHandler.makeSignInIntent()
                    val observer = object : Observer<Intent?>{
                        override fun onChanged(it: Intent?) {
                            activity.startActivityForResult(it, REQUEST_CODE_SIGN_IN)
                            signInIntent.removeObserver(this)
                        }
                    }
                    signInIntent.observeForever(observer)
                }
                dismiss()
            }
        })

        binding.executeAfter {
            viewModel = signInViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        if(showsDialog){
            (requireDialog() as AlertDialog).setView(binding.root)
        }
    }

    companion object{
        const val DIALOG_SIGN_IN = "dialog_sign_in"
        const val REQUEST_CODE_SIGN_IN = 42
    }
}
