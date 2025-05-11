package com.foodapp.fragments.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminTableFragment extends Fragment {

    private RecyclerView recyclerViewTables;
    private FloatingActionButton fabAddTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_table, container, false);

        recyclerViewTables = view.findViewById(R.id.recyclerViewTables);
        fabAddTable = view.findViewById(R.id.fabAddTable);

        // Set up RecyclerView with a GridLayoutManager
        recyclerViewTables.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Load table data
        loadTables();

        // Set up FAB click listener
        fabAddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTableDialog();
            }
        });

        return view;
    }

    private void loadTables() {
        // TODO: Load table data from database and set adapter
    }

    private void showAddTableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm bàn mới");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_table, null);
        builder.setView(dialogView);

        final EditText etTableNumber = dialogView.findViewById(R.id.etTableNumber);

        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tableNumber = etTableNumber.getText().toString();

                if (tableNumber.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập số bàn", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Add table to database

                loadTables();
                Toast.makeText(getContext(), "Đã thêm bàn mới", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }
}