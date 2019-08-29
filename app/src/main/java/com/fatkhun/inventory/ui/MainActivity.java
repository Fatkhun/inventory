package com.fatkhun.inventory.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.fatkhun.inventory.R;
import com.fatkhun.inventory.data.DbHelper;
import com.fatkhun.inventory.data.model.Product;
import com.fatkhun.inventory.ui.adapter.ProductListAdapter;
import com.fatkhun.inventory.ui.dialog.ViewDialogFragment;
import com.fatkhun.inventory.util.PhotoHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductListAdapter.OnItemClickListener,
        ProductListAdapter.OnItemSaleListener, ProductListAdapter.OnItemDeleteListener{

    private static final String VIEW_DIALOG_TAG = "VIEW_DIALOG_TAG";
    private static final String INTENT_EXTRA_PRODUCT = "INTENT_EXTRA_PRODUCT";

    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private DbHelper mDbHelper;
    private ProductListAdapter mAdapter;


    private DialogInterface.OnClickListener mOnPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // Delete all products in the database and clear the adapter.
            mDbHelper.deleteAllProducts();
            mAdapter.emptyData();
            checkEmptyData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);

        mDbHelper = DbHelper.getInstance(getApplicationContext());
        mAdapter = new ProductListAdapter(this, new ArrayList<Product>());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemSaleListener(this);
        mAdapter.setOnItemDeleteListener(this);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_empty_products) {
            ViewDialogFragment dialogFragment = ViewDialogFragment.newInstance(
                    R.string.title_dialog_confirm_empty_products, R.string.message_dialog_confirm_empty_products);
            dialogFragment.setOnPositiveClickListener(mOnPositiveClickListener);
            dialogFragment.show(getSupportFragmentManager(), VIEW_DIALOG_TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, DetailProductActivity.class);
        intent.putExtra(INTENT_EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    @Override
    public void onItemSale(final int position) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Product product = mAdapter.decreaseProductQuantity(position);
                if (product != null) {
                    mDbHelper.updateProductQuantity(product.getId(), product.getQuantity());
                }
            }
        };
        ViewDialogFragment dialogFragment = ViewDialogFragment.newInstance(
                R.string.title_dialog_confirm_sale, R.string.message_dialog_confirm_sale);
        dialogFragment.setOnPositiveClickListener(onClickListener);
        dialogFragment.show(getSupportFragmentManager(), VIEW_DIALOG_TAG);
    }

    @Override
    public void onItemDelete(final Product product, final int position) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.deleteProduct(position);
                mDbHelper.deleteProduct(product.getId());
                PhotoHelper.deleteCapturedPhotoFile(product.getPhotoPath());
                checkEmptyData();
            }
        };
        ViewDialogFragment dialogFragment = ViewDialogFragment.newInstance(
                R.string.title_dialog_confirm_delete, R.string.message_dialog_confirm_delete);
        dialogFragment.setOnPositiveClickListener(onClickListener);
        dialogFragment.show(getSupportFragmentManager(), VIEW_DIALOG_TAG);
    }

    public void navigateToCreateActivity(View view) {
        Intent intent = new Intent(this, CreateProductActivity.class);
        startActivity(intent);
    }

    private void checkEmptyData() {
        boolean isDataEmpty = (mAdapter.getItemCount() == 0);
        mRecyclerView.setVisibility(isDataEmpty ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(isDataEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Product> products = mDbHelper.queryProducts();
        mAdapter.refreshData(products);
        checkEmptyData();
    }
}
