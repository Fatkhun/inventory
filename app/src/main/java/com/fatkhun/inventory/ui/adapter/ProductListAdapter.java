package com.fatkhun.inventory.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.fatkhun.inventory.R;
import com.fatkhun.inventory.data.model.Product;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>{

    private Context mContext;
    private List<Product> mProducts;
    private OnItemClickListener mOnItemClickListener;
    private OnItemSaleListener mOnItemSaleListener;
    private OnItemDeleteListener mOnItemDeleteListener;

    public ProductListAdapter(Context context, List<Product> products) {
        mContext = context;
        mProducts = products;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemSaleListener(OnItemSaleListener listener) {
        mOnItemSaleListener = listener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        mOnItemDeleteListener = listener;
    }

    public void refreshData(List<Product> products) {
        mProducts.clear();
        mProducts.addAll(products);
        notifyDataSetChanged();
    }

    public void emptyData() {
        mProducts.clear();
        notifyDataSetChanged();
    }


    public Product decreaseProductQuantity(int position) {
        Product product = mProducts.get(position);
        int quantity = product.getQuantity();
        if (quantity > 0) {
            product.setQuantity(quantity - 1);
            notifyItemChanged(position);
            return product;
        }
        return null;
    }

    public void deleteProduct(int position) {
        mProducts.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Product product = mProducts.get(position);
        holder.mProductNameTextView.setText(product.getName());

        holder.mProductSkuTextView.setText(product.getCode());

        Bitmap bitmap = BitmapFactory.decodeFile(product.getPhotoPath());
        holder.mImageView.setImageBitmap(bitmap);

        String formattedQuantity = mContext.getString(R.string.string_format_product_quantity, product.getQuantity());
        holder.mProductQuantityTextView.setText(formattedQuantity);


        holder.mOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int position = holder.getAdapterPosition();
                        switch (item.getItemId()) {
                            case R.id.action_sale:
                                mOnItemSaleListener.onItemSale(position);
                                return true;
                            case R.id.action_delete:
                                mOnItemDeleteListener.onItemDelete(product, position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_popup);
                popupMenu.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public interface OnItemClickListener {

        void onItemClick(Product product);
    }

    public interface OnItemSaleListener {

        void onItemSale(int position);
    }

    public interface OnItemDeleteListener {

        void onItemDelete(Product product, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mProductNameTextView;
        private TextView mProductSkuTextView;
        private TextView mProductQuantityTextView;
        private ImageView mImageView;
        private ImageButton mOverflowButton;

        ViewHolder(View itemView) {
            super(itemView);
            mProductNameTextView = itemView.findViewById(R.id.product_name_text_view);
            mProductSkuTextView = itemView.findViewById(R.id.product_sku_text_view);
            mProductQuantityTextView = itemView.findViewById(R.id.product_quantity_text_view);
            mImageView = itemView.findViewById(R.id.iv_product);
            mOverflowButton = itemView.findViewById(R.id.overflow_button);
        }
    }
}
