package com.example.simpletodo;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button btnAdd;
    EditText edItem;
    RecyclerView rvItems;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        edItem = findViewById(R.id.edItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();


        ItemAdapter.OnlongClickListener onlongClickListener = new ItemAdapter.OnlongClickListener(){

            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify item remove
                itemAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
        ItemAdapter.OnClickListener onClickListener = new ItemAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity","Single click at position");
                //Create the new activity
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);

            }
        };
        itemAdapter = new ItemAdapter(items,onlongClickListener,onClickListener);
        rvItems.setAdapter(itemAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //fuction to add a new item
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = edItem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                //Notify adapter that an item is inserted
                itemAdapter.notifyItemInserted(items.size()-1);
                edItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        });
    }
    //handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            //Retreive the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model at the right position with new item text
            items.set(position, itemText);
            //notify the adapter
            itemAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(),"Item updated successfully!",Toast.LENGTH_SHORT).show();

        }else{
            Log.w("MainActivity", "Unknown call to onActivityResult");

        }
    }

    private File getDatafile(){
        return new File(getFilesDir(),"data.txt");
    }
    //This function will load items by reading every line od the data file
    private void loadItems(){
        try{
            items = new ArrayList<>(FileUtils.readLines(getDatafile(), Charset.defaultCharset()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e("MainActivity","error reading items", e);
            items = new ArrayList<>();
        }
    }
    //This function will save data by wrinting them into the data file
    private void saveItems(){
        try{
            FileUtils.writeLines(getDatafile(),items);
        }catch (IOException e){
            Log.e("MainActivity","error writing items", e);
        }

    }

}