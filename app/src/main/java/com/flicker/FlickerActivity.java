package com.flicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

public class FlickerActivity extends AppCompatActivity implements FlickerView {

    private RecyclerView imageRecycler;
    private SearchView searchText;

    private FlickerPresenter presenter = new FlickerPresenter();

    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flicker);

        imageRecycler = (RecyclerView) findViewById(R.id.image_recycler);
        searchText = (SearchView) findViewById(R.id.search_text);

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                presenter.findImages(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        imageAdapter = new ImageAdapter();

        imageRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        imageRecycler.setAdapter(imageAdapter);
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
    public void displayImages(List<Photo> imageUrls) {
        imageAdapter.replaceImages(imageUrls);
    }

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
