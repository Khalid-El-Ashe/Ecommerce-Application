package com.example.ecommerceapp.Dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.ecommerceapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

// i need to create function to make bottom sheet dialog
fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val editEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val cancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)
    val send = view.findViewById<Button>(R.id.buttonSendResetPassword)

    send.setOnClickListener {
        val email = editEmail.text.toString().toString()
        onSendClick(email)
        dialog.dismiss()
    }

    cancel.setOnClickListener {
        dialog.dismiss()
    }
}