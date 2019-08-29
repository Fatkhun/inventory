package com.fatkhun.inventory.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fatkhun.inventory.R;
import com.fatkhun.inventory.data.ApiClient;
import com.fatkhun.inventory.data.ApiService;
import com.fatkhun.inventory.data.DbHelper;
import com.fatkhun.inventory.data.model.DataProvince;
import com.fatkhun.inventory.data.model.Product;
import com.fatkhun.inventory.data.model.ProvinceResponse;
import com.fatkhun.inventory.data.model.ZipCodeResponse;
import com.fatkhun.inventory.ui.dialog.ViewDialogFragment;
import com.fatkhun.inventory.util.LoadProductPhotoAsync;
import com.fatkhun.inventory.util.PhotoHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailProductActivity extends AppCompatActivity {

    private static final String VIEW_DIALOG_TAG = "VIEW_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";

    private ImageView mProductPhotoImageView;
    private TextView mProductQuantityTextView;
    private DbHelper mDbHelper;
    private Product mProduct;
    private Spinner spZipCode;

    Context context;

    ApiService apiService;
    CompositeDisposable disposable = new CompositeDisposable();

    List<ZipCodeResponse> zipCodeResponseList;
    DataProvince provinceResponseList;


    private DialogInterface.OnClickListener mOnPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            mDbHelper.deleteProduct(mProduct.getId());
            PhotoHelper.deleteCapturedPhotoFile(mProductPhotoImageView.getTag());
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        mProductPhotoImageView = findViewById(R.id.product_photo_image_view);
        mProductQuantityTextView = findViewById(R.id.product_quantity_text_view);
        spZipCode = findViewById(R.id.sp_zip_code);

        // Get an instance of the database helper.
        mDbHelper = DbHelper.getInstance(getApplicationContext());

        // Get the product object which was sent from MainActivity.
        Product product = getIntent().getParcelableExtra(INTENT_EXTRA_PRODUCT);
        // Get the rest of the product information from the database.
        mProduct = mDbHelper.queryProductDetails(product);

        // Populate views with the product details data.
        populateViewsWithProductData();

        getZipCode();
        getProvince();

    }

    private void getProvince() {
        disposable.add(
                apiService
                        .getProvince()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DataProvince>() {

                            @Override
                            public void onSuccess(DataProvince provinceResponses) {
                                provinceResponseList = provinceResponses;
//                                showListinSpinner2();
                                Log.d("DATA", "onSuccess: " + provinceResponseList.getListProvince());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("ERROR", "onError: " + e.getMessage());
                            }
                        }));
    }

    private void getZipCode() {
        disposable.add(
                apiService
                        .getZipCode()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<ZipCodeResponse>>() {

                            @Override
                            public void onSuccess(List<ZipCodeResponse> zipCodeResponses) {
                                zipCodeResponseList = zipCodeResponses;
                                showListinSpinner();
//                                Log.d("DATA", "onSuccess: " + str.get(0));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("ERROR", "onError: " + e.getMessage());
                            }
                        }));
    }

    private void showListinSpinner(){
        List<String> strings = new ArrayList<>();
        for(int i=0; i<zipCodeResponseList.size(); i++){
            strings.add("Kec. " + zipCodeResponseList.get(i).getKecamatan() + ", Kel. " + zipCodeResponseList.get(i).getKelurahan() + ", " + zipCodeResponseList.get(i).getKodepos());
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
        //setting adapter to spinner
        spZipCode.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // Navigate to CreateActivity and pass the current product object as an argument.
                Intent intent = new Intent(this, CreateProductActivity.class);
                intent.putExtra(INTENT_EXTRA_PRODUCT, mProduct);
                startActivity(intent);
                break;
            case R.id.action_delete:
                // Show a confirmation dialog before deleting the product from the database.
                ViewDialogFragment dialogFragment = ViewDialogFragment.newInstance(
                        R.string.title_dialog_confirm_delete, R.string.message_dialog_confirm_delete);
                dialogFragment.setOnPositiveClickListener(mOnPositiveClickListener);
                dialogFragment.show(getSupportFragmentManager(), VIEW_DIALOG_TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void modifyProductQuantity(View view) {
        int productQty = mProduct.getQuantity();
        if (view.getId() == R.id.increase_qty_button) {
            // Increase the quantity of the product by 1.
            productQty = productQty + 1;
            updateProductQuantity(productQty);
        } else {
            // Decrease the quantity of the product by 1 only if it wouldn't result a negative qty.
            if (productQty > 0) {
                productQty = productQty - 1;
                updateProductQuantity(productQty);
            }
        }
    }


    public void contactSupplier(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mProduct.getName()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_order_request, mProduct.getName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void populateViewsWithProductData() {
        String photoPath = mProduct.getPhotoPath();
        mProductPhotoImageView.setTag(photoPath);
        new LoadProductPhotoAsync(this, mProductPhotoImageView).execute(photoPath);

        TextView productNameTextView = findViewById(R.id.product_name_text_view);
        productNameTextView.setText(mProduct.getName());

        TextView productCodeTextView = findViewById(R.id.product_code_text_view);
        productCodeTextView.setText(getString(R.string.string_format_product_code, mProduct.getCode()));

        mProductQuantityTextView = findViewById(R.id.product_quantity_text_view);
        mProductQuantityTextView.setText(getString(R.string.string_format_product_quantity_details, mProduct.getQuantity()));
    }


    private void updateProductQuantity(int newQuantity) {
        mProductQuantityTextView.setText(getString(R.string.string_format_product_quantity_details, newQuantity));
        mProduct.setQuantity(newQuantity);
        mDbHelper.updateProductQuantity(mProduct.getId(), mProduct.getQuantity());
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
