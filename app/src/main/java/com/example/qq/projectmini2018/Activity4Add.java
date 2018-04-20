package com.example.qq.projectmini2018;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sarayut on 8/4/2561.
 */

public class Activity4Add extends AppCompatActivity {

    TextView tvshowName, tvprofilePhoto, tvUpload, tvTakePhoto, tvCancel;
    AutoCompleteTextView tvName, tvEmail, tvPhone;
    Button btInsert;
    String imgPath = "";
    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_IMAGE_CAPTURE = 2;
    private boolean Formatting;
    private int After;
    private int count = 0;
    DatabaseHandler db;
    List<Contact> contacts;
    Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity4_add);

        tvshowName = findViewById(R.id.textViewName);
        tvprofilePhoto = findViewById(R.id.tvProfilePhoto);
        tvName = findViewById(R.id.textAddName);
        tvEmail = findViewById(R.id.textAddEmail);
        tvPhone = findViewById(R.id.textAddPhone);
        btInsert = findViewById(R.id.butInsert);

        //Create DB and Table
        db = new DatabaseHandler(this);
        //count rows for limits item
        contacts = db.getAllContacts();
        count = contacts.size();

        tvPhoneAddTextChanged();

        tvNameAddTextChanged();

        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = tvName.getText().toString();
                String email = tvEmail.getText().toString();
                String phone = tvPhone.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please input Name !!", Toast.LENGTH_SHORT).show();
                    tvName.requestFocus();

                } else if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please input E-mail !!", Toast.LENGTH_SHORT).show();
                    tvEmail.requestFocus();
                } else if (phone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please input Phone number !!", Toast.LENGTH_SHORT).show();
                    tvEmail.requestFocus();
                } else if(count <= 19) {
                    Contact person = new Contact();
                    person._name = tvName.getText().toString();
                    person._phone_number = tvPhone.getText().toString();
                    person._email = tvEmail.getText().toString();
                    person._img_path = imgPath;
                    db.updateOrInsert(person);//ถ้ามีปัญหาให้เช็ค version
                    finish();
                } else if(count == 20){
                    Toast.makeText(getApplicationContext(), "Error to insert data. Only 20 items can be added !!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        tvprofilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void tvNameAddTextChanged() {
        tvName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvshowName.setText(tvName.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void tvPhoneAddTextChanged() {
        tvPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                After = after; // เช็คการกด backspace
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Formatting) {
                    Formatting = true;
                    if (After != 0) //กรณีที่ไม่ได้กด backspace ใช้ format US และกำหนด length ถ้าไทยจะเป็น getDefault() แต่จะไม่ได้ผลลัพธ์ที่ต้องการคือ xxx-xxx-xxxx
                        PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
                    Formatting = false;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final CircleImageView viewPhoto = findViewById(R.id.imageViewPhoto);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            //picturePath is the path to save in DB
            imgPath = cursor.getString(columnIndex);
            cursor.close();
            viewPhoto.setImageBitmap(BitmapFactory.decodeFile(imgPath));

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            imgPath = file.getPath().toString();

            viewPhoto.setImageBitmap(BitmapFactory.decodeFile(imgPath));
        }
    }

    public void openDialog() {

        final Dialog dialog = new Dialog(Activity4Add.this);
        dialog.setTitle("Profile pictures.");
        dialog.setContentView(R.layout.dialog_profile);
        tvUpload = dialog.findViewById(R.id.txtUploadImg);
        tvTakePhoto = dialog.findViewById(R.id.txtTakePhoto);
        tvCancel = dialog.findViewById(R.id.tvTakePhotoCancel);
        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                dialog.dismiss();
            }
        });
        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ProjectAndroid2018");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }
}
