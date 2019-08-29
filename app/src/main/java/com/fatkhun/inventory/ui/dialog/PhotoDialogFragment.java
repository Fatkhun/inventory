package com.fatkhun.inventory.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.fatkhun.inventory.R;

public class PhotoDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.title_dialog_product_photo);
        builder.setItems(R.array.product_photo_dialog_options, (DialogInterface.OnClickListener) getActivity());
        return builder.create();
    }
}
