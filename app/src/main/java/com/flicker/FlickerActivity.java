package com.flicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.flicker.details.DetailFragment;
import com.flicker.models.Photo;
import com.flicker.search.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class FlickerActivity extends AppCompatActivity implements FlickerView {

    private AutoCompleteTextView searchText;

    private FlickerPresenter presenter = new FlickerPresenter();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flicker);

        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        searchText.setAdapter(adapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.updateRecentQueries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        search(searchText.getText().toString());

                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    search(searchText.getText().toString());

                    return true;
                }

                return false;
            }
        });

        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);

                search(item);
            }
        });

        replaceFragment(new SearchFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

        presenter.subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        presenter.unSubscribe();
    }

    @Override
    public void displayImages(List<Photo> photos) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof SearchFragment) {
            ((SearchFragment) fragment).displayImages(photos);
        }
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        searchText.setVisibility(View.VISIBLE);
    }

    @Override
    public void recentSearches(List<String> searches) {
        adapter.clear();

        adapter.addAll(searches);
        adapter.notifyDataSetChanged();
    }

    private void search(String text) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchText.getWindowToken(), 0);

        presenter.findImages(text);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitNow();
    }

    public void showDetailsFragment(DetailFragment fragment) {
        searchText.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
