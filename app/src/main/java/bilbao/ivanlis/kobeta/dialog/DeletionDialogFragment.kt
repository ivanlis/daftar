package bilbao.ivanlis.kobeta.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import bilbao.ivanlis.kobeta.LessonDetailsViewModel
import java.lang.ClassCastException
import java.lang.IllegalStateException

class DeletionDialogFragment(private val message: String = "",
                             private val positiveMessage: String = "",
                             private val negativeMessage: String = "",
                             private val viewModel: LessonDetailsViewModel
                            ): DialogFragment() {

    //internal lateinit var listener: DeletionDialogListener

//    interface DeletionDialogListener {
//        fun onDialogPositiveClick(dialog: DialogFragment)
//        fun onDialogNegativeClick(dialog: DialogFragment)
//    }
    private var _lastResult: Boolean = false
    val lastResult: Boolean
        get() = _lastResult

    override fun onAttach(context: Context) {
        super.onAttach(context)

//        try {
//            listener = context as DeletionDialogListener
//
//        } catch (e: ClassCastException) {
//            throw ClassCastException("$context must implement DeletionDialogListener")
//        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {fragmentActivity ->
            val builder = AlertDialog.Builder(fragmentActivity)

            builder.setMessage(message)
                .setPositiveButton(positiveMessage) { _, _ -> // instead of dialog, id
                    //listener.onDialogPositiveClick(this)
                    _lastResult = true

                    viewModel.onExecuteDelete()

                }
                .setNegativeButton(negativeMessage) { _, _ ->
                    //listener.onDialogNegativeClick(this)
                    _lastResult = false
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

