package bilbao.ivanlis.kobeta.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException
import java.lang.IllegalStateException

class DeletionDialogFragment(val message: String = "",
                             val positiveMessage: String = "", val negativeMessage: String = ""): DialogFragment() {

    internal lateinit var listener: DeletionDialogListener

    interface DeletionDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as DeletionDialogListener

        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DeletionDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {fragmentActivity ->
            val builder = AlertDialog.Builder(fragmentActivity)

            builder.setMessage(message)
                .setPositiveButton(positiveMessage) { _, _ -> // instead of dialog, id
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(negativeMessage) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}