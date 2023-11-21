package com.example.restapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    private class PersonAdapter extends ArrayAdapter<Person> {


        public PersonAdapter() {
            super(MainActivity.this, R.layout.person_list_item, people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_item, null, false);
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
                    RequestTask task = new RequestTask(url, "DELETE", String.valueOf(actualPerson.getId()));
                    this.execute();
                }
            });

            return view;
        }
    }
}

private class RequestTask extends AsyncTask<Void, Void, Response> {
    String requestUrl;
    String requestType;
    String requestParams;

    public RequestTask(String requestUrl, String requestType, String requestParams) {
        this.requestUrl = requestUrl;
        this.requestType = requestType;
        this.requestParams = requestParams;
    }

    public RequestTask(String requestUrl, String requestType) {
        this.requestUrl = requestUrl;
        this.requestType = requestType;
    }

    @Override
    protected Response doInBackground(Void... voids) {
        Response response = null;
        try {
            switch (requestType) {
                case "GET":
                    response = RequestHandler.get(requestUrl);
                    break;
                case "POST":
                    response = RequestHandler.post(requestUrl, requestParams);
                    break;
                case "PUT":
                    response = RequestHandler.put(requestUrl, requestParams);
                    break;
                case "DELETE":
                    response = RequestHandler.delete(requestUrl + "/" + requestParams);
                    break;
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        progressBar.setVisibility(View.GONE);
        Gson converter = new Gson();
        if (response.getResponseCode() >= 400) {
            Toast.makeText(MainActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
            Log.d("onPostExecuteError: ", response.getContent());
        }
        switch (requestType) {
            case "GET":
                Person[] peopleArray = converter.fromJson(response.getContent(), Person[].class);
                people.clear();
                people.addAll(Arrays.asList(peopleArray));
                break;
            case "POST":
                Person person = converter.fromJson(response.getContent(), Person.class);
                people.add(0, person);
                urlapAlaphelyzetbe();
                break;
            case "PUT":
                Person updatePerson = converter.fromJson(response.getContent(), Person.class);
                people.replaceAll(person1 -> person1.getId() == updatePerson.getId() ? updatePerson : person1);
                urlapAlaphelyzetbe();
                break;
            case "DELETE":
                int id = Integer.parseInt(requestParams);
                people.removeIf(person1 -> person1.getId() == id);
                break;
        }
    }


}


