package com.foodapp.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.FoodAdapter;
import com.foodapp.database.dao.FoodDao;
import com.foodapp.models.Food;

import java.util.ArrayList;
import java.util.List;

public class UserProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private FoodDao foodDao;
    private List<Food> foodList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);

        // Thiết lập RecyclerView với GridLayoutManager (2 cột)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Khởi tạo danh sách và adapter
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), foodList);
        recyclerView.setAdapter(adapter);

        // Lấy dữ liệu từ database
        loadData();

        return view;
    }

    private void loadData() {
        // Khởi tạo FoodDao để lấy dữ liệu từ database
        foodDao = new FoodDao(getContext());

        // Lấy tất cả món ăn
        foodList = foodDao.getAll();

        // Cập nhật adapter
        adapter.updateData(foodList);
    }
}