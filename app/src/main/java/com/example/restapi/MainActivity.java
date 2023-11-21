package com.example.restapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

private Button buttonSubmit;
private Button buttonModify;
private Button buttonBack;
private Button buttonFormShow;

private EditText editTextName;
private EditText editTextId;
private EditText editTextEmail;
private EditText editTextAge;
private LinearLayout linearLayoutForm;
private ProgressBar progressBar;
private ListView listView;
private List<Person> people = new ArrayList<>();

private String url = "https://retoolapi.dev/FVXqW0/person";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    public void init() {

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonModify = findViewById(R.id.buttonModify);
        buttonBack = findViewById(R.id.buttonBack);
        buttonFormShow = findViewById(R.id.buttonShowForm);
        editTextName = findViewById(R.id.editTextName);
        editTextId = findViewById(R.id.editTextId);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAge = findViewById(R.id.editTextAge);
        linearLayoutForm = findViewById(R.id.personForm);
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.listViewData);



        linearLayoutForm.setVisibility(View.GONE);
        buttonModify.setVisibility(View.GONE);

    }

    private class PersonAdapter extends ArrayAdapter<Person>{


        public PersonAdapter(){
            super(MainActivity.this,R.layout.person_list_item,people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_item,null,false);
            Person actualPerson = people.get(position);
            TextView textViewName = view.findViewById(R.id.textViewName);
            TextView textViewAge = view.findViewById(R.id.textViewAge);
            TextView textViewModify = view.findViewById(R.id.textViewModify);
            TextView textViewDelete = view.findViewById(R.id.textViewDelete);

            textViewName.setText(actualPerson.getName());
            textViewAge.setText(actualPerson.getAge());


            textViewModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayoutForm.setVisibility(View.VISIBLE);
                    buttonModify.setVisibility(View.VISIBLE);
                    editTextId.setText(String.valueOf(actualPerson.getId()));
                    editTextAge.setText(String.valueOf(actualPerson.getAge()));
                    editTextEmail.setText(actualPerson.getEmail());
                    editTextName.setText(actualPerson.getName());
                    buttonSubmit.setVisibility(View.GONE);
                    buttonBack.setVisibility(View.GONE);
                }
            });

            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestTask task = new RequestTask(url,"DELETE",String.valueOf(actualPerson.getId()));
                    this.execute();
                }
            });

            return view;
        }
    }

}


