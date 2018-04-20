package com.example.qq.projectmini2018;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Sarayut on 9/4/2561.
 */

public class Activity5View extends AppCompatActivity {
    Context context;
    ListView listView;
    DatabaseHandler db;
    List<Contact> contacts;
    SwipeRefreshLayout refreshLayout;
    private OwnProgressBar ownProgressBar;
    private ProgressDialog ProgressLoading;
    private Handler handler;
    private View FooterView;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity5_view);

        context = this;
        //Create DB and Table
        db = new DatabaseHandler(this);
        ownProgressBar = new OwnProgressBar(context);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ProgressLoading = new ProgressDialog(context);
        contacts = db.getAllContacts();

        String[] name = new String[contacts.size()];
        String[] phone = new String[contacts.size()];
        String[] imgPaths = new String[contacts.size()];
        String[] email = new String[contacts.size()];

        for (int i = 0; i < name.length; i++) {
            name[i] = contacts.get(i)._name;
            phone[i] = contacts.get(i)._phone_number;
            imgPaths[i] = contacts.get(i)._img_path;
            email[i] = contacts.get(i)._email;
        }

        adapter = new CustomAdapter(getApplicationContext(), name, phone, imgPaths, email);

        listView = findViewById(R.id.listview1);
        listView.setAdapter(adapter);
        listViewDialog(builder);

        refreshLayout = findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(getIntent());
                        refreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void listViewDialog(final AlertDialog.Builder builder) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                int[] _id = new int[contacts.size()];
                for (int i = position; i < _id.length; i++) {
                    _id[i] = contacts.get(i)._id;
                }

                final CustomAdapter adapter = new CustomAdapter(_id);
                final int posID = adapter.id[position];

                builder.setTitle("Choose the one you want.");
                builder.setMessage(Html.fromHtml("<font color='#ff9900'>This ID : </font>" + posID));
                builder.setCancelable(false);
                builder.setPositiveButton("Show", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final Intent intent = new Intent(getApplication(), Activity6Detail.class);
                        intent.putExtra("position", position);
                        final Dialog dialogView = new Dialog(Activity5View.this);
                        dialogView.setContentView(R.layout.own_progressbar_from_paint);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                                dialogView.dismiss();
                            }
                        }, 3450);
                        dialogView.show();
                    }
                })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteContact(new Contact(posID));
                                listView.invalidateViews();// refresh listview
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Delete ID: " + posID + " successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}