package com.example.qq.projectmini2018;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Sarayut on 9/4/2561.
 */
public class Activity6Detail extends AppCompatActivity {

    TextView tvShowName, tvShowEmail, tvShowPhone, tvMsg;
    CircleImageView ShowImgCircle;
    Context context;
    DatabaseHandler db;
    List<Contact> contacts;
    private int pos = 0, After;
    private boolean Formatting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity6_detail);

        context = this;
        //Create DB and Table
        db = new DatabaseHandler(this);
        contacts = db.getAllContacts();

        tvShowName = findViewById(R.id.tvShowName);
        tvShowEmail = findViewById(R.id.tvShowEmail);
        tvShowPhone = findViewById(R.id.tvShowPhone);
        ShowImgCircle = findViewById(R.id.imgviewDetail);

        Intent intent = getIntent();
        pos = intent.getExtras().getInt("position");

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

        final CustomAdapter adapter = new CustomAdapter(getApplicationContext(), name, email, imgPaths, phone);

        tvShowName.setText(adapter.strName[pos]);
        tvShowEmail.setText(adapter.email[pos]);
        tvShowPhone.setText(adapter.phoneNum[pos]);
        ShowImgCircle.setImageBitmap(BitmapFactory.decodeFile(adapter.imgPath[pos]));
        showImgDialog(adapter);

        tvShowName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(adapter.strName[pos], 1);
            }
        });
        tvShowEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(adapter.email[pos], 2);
            }
        });
        tvShowPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(adapter.phoneNum[pos], 3);
            }
        });
    }

    private void showImgDialog(final CustomAdapter adapter) {
        ShowImgCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Activity6Detail.this);
                dialog.setContentView(R.layout.dialog_profile_photo);
                ImageView ProfilePhoto = dialog.findViewById(R.id.imgProfilePhoto);
                Button btDialog = dialog.findViewById(R.id.dialogBt);
                TextView dialogShowName = dialog.findViewById(R.id.dialogShowName);
                dialogShowName.setText(adapter.strName[pos]);
                ProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(adapter.imgPath[pos]));
                PhotoViewAttacher photoView = new PhotoViewAttacher(ProfilePhoto);
                photoView.update();
                btDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    public void openDialog(String oldData, final int i) {
        final Dialog dialog = new Dialog(Activity6Detail.this);
        String title = "", Text = "";
        if (i == 1) {
            title = "Change display name.";
            Text = "Rename";
        } else if (i == 2) {
            title = "Change display email.";
            Text = "New email";
        } else if (i == 3) {
            title = "Change display phone no.";
            Text = "New phone number";
        }
        dialog.setTitle(title);
        dialog.setContentView(R.layout.input_box_name);
        tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setText(Text);
        final EditText etInput = dialog.findViewById(R.id.etInput);
        etInput.setText(oldData);
        TextView tvUpdate = dialog.findViewById(R.id.tvUpdate);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(Activity6Detail.this);
                progressDialog.setMessage("Updating....");
                progressDialog.setMax(50);
                progressDialog.show();

                int[] _id = new int[contacts.size()];
                for (int i = pos; i < _id.length; i++) {
                    _id[i] = contacts.get(i)._id;
                }

                final CustomAdapter adapter = new CustomAdapter(_id);
                final int posID = adapter.id[pos];
                Contact update = new Contact();

                if (i == 1) {
                    update._name = etInput.getText().toString();
                    update._email = tvShowEmail.getText().toString();
                    update._phone_number = tvShowPhone.getText().toString();
                    update._id = posID;
                    tvShowName.setText(etInput.getText().toString());
                } else if (i == 2) {
                    update._name = tvShowName.getText().toString();
                    update._email = etInput.getText().toString();
                    update._phone_number = tvShowPhone.getText().toString();
                    update._id = posID;
                    tvShowEmail.setText(etInput.getText().toString());
                } else if (i == 3) {
                    update._name = tvShowName.getText().toString();
                    update._email = tvShowEmail.getText().toString();
                    update._phone_number = etInput.getText().toString();
                    update._id = posID;
                    tvShowPhone.setText(etInput.getText().toString());
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(getIntent());
                    }
                }, 2000);

                db.updateContact(update);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
