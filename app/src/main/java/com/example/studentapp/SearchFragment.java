package com.example.studentapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.appcompat.widget.SearchView;

import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ldoublem.loadingviewlib.view.LVBlock;


import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{

    List<String> bookList;
    ArrayAdapter<String> arrayAdapter;
    private Context mContext;
    DatabaseReference myRef;
    FloatingActionButton fabAll, fabBusiness, fabHistory, fabHandF, fabSports, fabRomance;
    FloatingActionsMenu FAM;
    LVBlock LoadingAnim;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity();
        populateBookList();

    }

    private void initializeFAB(View currentview) {
        fabAll = currentview.findViewById(R.id.catAll);
        fabBusiness = currentview.findViewById(R.id.catBusiness);
        fabHistory = currentview.findViewById(R.id.catHistory);
        fabHandF = currentview.findViewById(R.id.catHandF);
        fabSports = currentview.findViewById(R.id.catSports);
        fabRomance = currentview.findViewById(R.id.catRomance);
        FAM = currentview.findViewById(R.id.FAM);


        fabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch only biography category books from firebase
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                bookList.add(snapshot.child("bookname").getValue().toString());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FAM.collapse();
            }
        });

        fabBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch only business category books from firebase
                updatebooklistwithcat("Business");
                FAM.collapse();
            }
        });

        fabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch only history category books from firebase
                updatebooklistwithcat("History");
                FAM.collapse();
            }
        });

        fabHandF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch only handf category books from firebase
                updatebooklistwithcat("Health And Fitness");
                FAM.collapse();
            }
        });

        fabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch only sports category books from firebase
                updatebooklistwithcat("Sports");
                FAM.collapse();
            }
        });

        fabRomance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch only romance category books from firebase
                updatebooklistwithcat("Romance");
                FAM.collapse();
            }
        });
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        String item = (String) listView.getAdapter().getItem(position);
        if (getActivity() instanceof OnItem1SelectedListener) {
            ((OnItem1SelectedListener) getActivity()).OnItem1SelectedListener(item);
        }
        getFragmentManager().popBackStack();
    }

    public interface OnItem1SelectedListener {
        void OnItem1SelectedListener(String item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);//Make sure you have this line of code.
        View RootView = inflater.inflate(R.layout.fragment_search, container, false);
        ListView listView = RootView.findViewById(android.R.id.list);
        TextView emptyTextView = RootView.findViewById(android.R.id.empty);

        LoadingAnim = RootView.findViewById(R.id.animation);
        LoadingAnim.startAnim();

        listView.setEmptyView(LoadingAnim);
        initializeFAB(RootView);



        //return inflater.inflate(R.layout.fragment_search, container, false);
        return RootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_book_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search Here");

        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        List<String> filteredValues = new ArrayList<String>(bookList);
        for (String value : bookList) {
            if (!value.toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, filteredValues);
        setListAdapter(arrayAdapter);

        return false;
    }

    public void resetSearch() {
        arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, bookList);
        setListAdapter(arrayAdapter);
    }

    private void populateBookList() {



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("books_db");

        bookList = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //User user = snapshot.getValue(User.class);
                    //System.out.println(user.email);

                    bookList.add(snapshot.child("bookname").getValue().toString());
                }
                arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, bookList);
                setListAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, bookList);
        //setListAdapter(arrayAdapter);
    }

    private void updatebooklistwithcat(final String currentCategory){

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getKey());
                    System.out.println(snapshot.child("category").getValue().toString());
                    if(snapshot.child("category").getValue().toString().equals(currentCategory)){
                        bookList.add(snapshot.child("bookname").getValue().toString());
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
