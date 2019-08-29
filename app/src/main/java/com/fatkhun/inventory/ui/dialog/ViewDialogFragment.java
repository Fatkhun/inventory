package com.fatkhun.inventory.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ViewDialogFragment extends DialogFragment {
    private static final String ARGS_TITLE_ID = "ARGS_TITLE_ID";
    private static final String ARGS_MESSAGE_ID = "ARGS_MESSAGE_ID";

    private DialogInterface.OnClickListener mOnPositiveClickListener;
    private DialogInterface.OnClickListener mOnNegativeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

    public static ViewDialogFragment newInstance(int titleId, int messageId) {
        Bundle args = new Bundle();
        args.putInt(ARGS_TITLE_ID, titleId);
        args.putInt(ARGS_MESSAGE_ID, messageId);
        ViewDialogFragment dialogFragment = new ViewDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    public void setOnPositiveClickListener(DialogInterface.OnClickListener listener) {
        mOnPositiveClickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        if (getArguments() != null) {
            builder.setTitle(getArguments().getInt(ARGS_TITLE_ID));
            builder.setMessage(getArguments().getInt(ARGS_MESSAGE_ID));
            builder.setPositiveButton(android.R.string.ok, mOnPositiveClickListener);
            builder.setNegativeButton(android.R.string.cancel, mOnNegativeClickListener);
        }
        return builder.create();
    }
}
