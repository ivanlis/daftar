package bilbao.ivanlis.kobeta.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import bilbao.ivanlis.kobeta.LessonDetailsViewModel
import java.lang.IllegalStateException

class DeletionDialogFragment(private val message: String = "",
                             private val positiveMessage: String = "",
                             private val negativeMessage: String = "",
                             private val observer: DeletionDialogListener
                            ): DialogFragment() {


    interface DeletionDialogListener {
        fun onConfirmedDeleteRequest()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {fragmentActivity ->
            val builder = AlertDialog.Builder(fragmentActivity)

            builder.setMessage(message)
                .setPositiveButton(positiveMessage) { _, _ -> // instead of dialog, id
                    //listener.onDialogPositiveClick(this)
                    //_lastResult = true

                    observer.onConfirmedDeleteRequest()

                }
                .setNegativeButton(negativeMessage) { _, _ ->
                    //listener.onDialogNegativeClick(this)
                    //_lastResult = false
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

