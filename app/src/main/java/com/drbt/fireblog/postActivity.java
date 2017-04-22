package com.drbt.fireblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

public class postActivity extends AppCompatActivity {

    private StorageReference storage;
    private DatabaseReference database;

    ImageButton postImage;
    EditText postTitle;
    EditText postDescription;
    Button postBtn;
    private ProgressDialog progressBar;
    Uri imageUri = null;
    private static final int GALLARY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        storage = FirebaseStorage.getInstance().getReference();


        postImage = (ImageButton)findViewById(R.id.post_img);
        postTitle = (EditText)findViewById(R.id.post_title);
        postDescription = (EditText)findViewById(R.id.post_details);
        postBtn = (Button)findViewById(R.id.post_button);
        progressBar = new ProgressDialog(this);

        //image btn gets the image
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,GALLARY_REQUEST_CODE);
            }
        });

        //post btn start posting
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting() {


        progressBar.setMessage("Posting to Blog ...");

        database = FirebaseDatabase.getInstance().getReference().child("Blog");

        final String postTtl = postTitle.getText().toString();
        final String postDesc = postDescription.getText().toString();



        if(!TextUtils.isEmpty(postTtl) && !TextUtils.isEmpty(postDesc) && imageUri!=null) {


            progressBar.show(); //showing the progress bar

            StorageReference filePath = storage.child("Blog_post_images").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = database.push(); //pusing the data
                    newPost.child("title").setValue(postTtl);
                    newPost.child("description").setValue(postDesc);
                    newPost.child("image").setValue(downloadUrl.toString());

                    progressBar.dismiss();

                    Intent i = new Intent(postActivity.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }else {
            Toast.makeText(this,"no img",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLARY_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            postImage.setImageURI(imageUri);
        }
    }
}
