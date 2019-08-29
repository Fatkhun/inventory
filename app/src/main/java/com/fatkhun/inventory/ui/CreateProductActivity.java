package com.fatkhun.inventory.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatkhun.inventory.R;
import com.fatkhun.inventory.data.DbHelper;
import com.fatkhun.inventory.data.model.Product;
import com.fatkhun.inventory.ui.dialog.PhotoDialogFragment;
import com.fatkhun.inventory.util.LoadProductPhotoAsync;
import com.fatkhun.inventory.util.PhotoHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;

public class CreateProductActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    private static final String TAG = CreateProductActivity.class.getSimpleName();
    private static final String PRODUCT_PHOTO_DIALOG_TAG = "PRODUCT_PHOTO_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private ImageView mProductPhotoImageView;
    private TextInputLayout mProductNameTIL;
    private TextInputEditText mProductNameTextField;
    private TextInputLayout mProductCodeTIL;
    private TextInputEditText mProductCodeTextField;
    private TextInputLayout mProductQuantityTIL;
    private TextInputEditText mProductQuantityTextField;

    private DbHelper mDbHelper;
    private Product mPassedProduct;
    private String mTempPhotoFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        mProductPhotoImageView = findViewById(R.id.product_photo_image_view);
        mProductNameTIL = findViewById(R.id.product_name_text_input_layout);
        mProductNameTextField = findViewById(R.id.product_name_text_field);
        mProductCodeTIL = findViewById(R.id.product_code_text_input_layout);
        mProductCodeTextField = findViewById(R.id.product_code_text_field);
        mProductQuantityTIL = findViewById(R.id.product_quantity_text_input_layout);
        mProductQuantityTextField = findViewById(R.id.product_quantity_text_field);

        addTextWatcherToTextFields(mProductNameTIL, mProductNameTextField);
        addTextWatcherToTextFields(mProductCodeTIL, mProductCodeTextField);
        addTextWatcherToTextFields(mProductQuantityTIL, mProductQuantityTextField);

        mDbHelper = DbHelper.getInstance(getApplicationContext());
        mPassedProduct = getIntent().getParcelableExtra(INTENT_EXTRA_PRODUCT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle((mPassedProduct == null) ? R.string.title_action_bar_add_product : R.string.title_action_bar_edit_product);
        }

        if (mPassedProduct != null) {
            populateViewsWithPassedProductData();
        }

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CreateProductActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(CreateProductActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(CreateProductActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(CreateProductActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                // Take photo successful. If the user has previously set a captured photo on the
                // ImageView, that photo file needs to be deleted since it will be replaced now.
                PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
                // Save the file uri as a tag and display the captured photo on the ImageView.
                mProductPhotoImageView.setTag(mTempPhotoFilePath);
                new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(mTempPhotoFilePath);
            } else if (resultCode == RESULT_CANCELED) {
                // The user cancelled taking a photo. The photo file created from the camera intent
                // is just an empty file so delete it since we don't need it anymore.
                File photoFile = new File(mTempPhotoFilePath);
                boolean deletePhotoSuccess = photoFile.delete();
                Log.d(TAG, "onActivityResult: deletePhotoSuccess = " + deletePhotoSuccess);
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                // Choose photo successful. Delete previously captured photo file if there's any.
                PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
                // Get the picture path and load it into the ImageView.
                Uri selectedPhotoUri = data.getData();
                mProductPhotoImageView.setTag(selectedPhotoUri.toString());
                new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(selectedPhotoUri.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            // The user presses the 'Done' action button. Validate user inputs before proceeding.
            if (validateUserInput()) {
                Intent previousActivityIntent;
                // If passed product object is present, update the product with the new data.
                // Otherwise, create a new product object with the data and save it to the database.
                if (mPassedProduct != null) {
                    buildProductWithUserInputData(mPassedProduct);
                    mDbHelper.updateProduct(mPassedProduct);
                    // Set previousActivityIntent to DetailActivity and pass back the updated product object.
                    previousActivityIntent = new Intent(this, DetailProductActivity.class);
                    previousActivityIntent.putExtra(INTENT_EXTRA_PRODUCT, mPassedProduct);
                } else {
                    Product product = new Product();
                    buildProductWithUserInputData(product);
                    mDbHelper.insertProduct(product);
                    // Set previousActivityIntent to MainActivity.
                    previousActivityIntent = new Intent(this, MainActivity.class);
                }
                // Return to the previous activity which called this activity.
                NavUtils.navigateUpTo(this, previousActivityIntent);
            }
        } else if (item.getItemId() == android.R.id.home) {

            if (mPassedProduct == null) {
                PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            // The user selects 'Take photo' option. Dispatch the camera intent.
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create a File where the photo will be saved to.
                File photoFile = null;
                try {
                    photoFile = PhotoHelper.createPhotoFile(this);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                if (photoFile != null) {
                    // Save the photo file path globally.
                    mTempPhotoFilePath = photoFile.getAbsolutePath();
                    // Get the file content URI using FileProvider to avoid FileUriExposedException.
                    Uri photoUri = FileProvider.getUriForFile(this, getString(R.string.authority), photoFile);
                    // Set the file content URI as an intent extra and dispatch the camera intent.
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PHOTO);
                }
            }
        } else {
            // The user selects 'Choose photo' option. Dispatch the choose photo intent.
            Intent choosePhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            choosePhotoIntent.addCategory(Intent.CATEGORY_OPENABLE);
            choosePhotoIntent.setType("image/*");
            if (choosePhotoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(choosePhotoIntent, REQUEST_CODE_CHOOSE_PHOTO);
            }
        }
    }


    public void showProductPhotoDialog(View view) {
        PhotoDialogFragment dialogFragment = new PhotoDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), PRODUCT_PHOTO_DIALOG_TAG);
    }


    private void populateViewsWithPassedProductData() {
        String photoPath = mPassedProduct.getPhotoPath();
        mProductPhotoImageView.setTag(photoPath);
        new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(photoPath);

        mProductNameTextField.setText(mPassedProduct.getName());
        mProductCodeTextField.setText(mPassedProduct.getCode());
        mProductQuantityTextField.setText(String.valueOf(mPassedProduct.getQuantity()));
    }

    private void addTextWatcherToTextFields(@NonNull final TextInputLayout til, @NonNull TextInputEditText textField) {
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start >= 0 && til.isErrorEnabled()) {
                    til.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private boolean validateUserInput() {
        boolean isProductNameSet = !TextUtils.isEmpty(mProductNameTextField.getText());
        boolean isProductCodeSet = !TextUtils.isEmpty(mProductCodeTextField.getText());
        boolean isProductQtySet = !TextUtils.isEmpty(mProductQuantityTextField.getText());
        if (!isProductNameSet) {
            mProductNameTIL.setError(getString(R.string.error_msg_product_name_empty));
            mProductNameTIL.setErrorEnabled(true);
        }
        if (!isProductCodeSet) {
            mProductCodeTIL.setError(getString(R.string.error_msg_product_sku_empty));
            mProductCodeTIL.setErrorEnabled(true);
        }
        if (!isProductQtySet) {
            mProductQuantityTIL.setError(getString(R.string.error_msg_product_quantity_empty));
            mProductQuantityTIL.setErrorEnabled(true);
        }
        return isProductNameSet && isProductCodeSet && isProductQtySet;
    }


    private void buildProductWithUserInputData(Product product) {
        product.setName(mProductNameTextField.getText().toString());
        product.setCode(mProductCodeTextField.getText().toString());
        Object imageViewTag = mProductPhotoImageView.getTag();
        product.setPhotoPath((imageViewTag != null) ? imageViewTag.toString() : "");
        product.setQuantity(Integer.valueOf(mProductQuantityTextField.getText().toString()));
    }
}
