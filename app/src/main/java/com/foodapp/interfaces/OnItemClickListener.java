package com.foodapp.interfaces;

/**
 * Interface xử lý sự kiện click vào item trong RecyclerView
 *
 * @param <T> Loại dữ liệu của item được click
 */
public interface OnItemClickListener<T> {
    /**
     * Xử lý khi item được click
     *
     * @param item     Dữ liệu của item
     * @param position Vị trí của item trong list
     */
    void onItemClick(T item, int position);
}