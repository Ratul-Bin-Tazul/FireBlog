package com.drbt.fireblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextView fullname;
    private TextView email;
    private TextView password;
    private Button regBtn;
    private ProgressDialog progress;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = (TextView)findViewById(R.id.reg_name);
        email = (TextView)findViewById(R.id.reg_email);
        password = (TextView)findViewById(R.id.reg_password);
        progress = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        regBtn = (Button)findViewById(R.id.reg_button);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });

    }

    private void startRegistration() {
        final String name = fullname.getText().toString().trim();
        String eml = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        progress.setMessage("Registering ...");

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(eml) && !TextUtils.isEmpty(pw)) {
            progress.show();
            auth.createUserWithEmailAndPassword(eml,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(RegisterActivity.this,"entered complete method",Toast.LENGTH_SHORT);
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,"entered id condition",Toast.LENGTH_SHORT);
                        String user_uid = auth.getCurrentUser().getUid();
                        DatabaseReference user = database.child(user_uid);
                        user.child("fullname").setValue(name);
                        user.child("image").setValue("default");
                        Toast.makeText(RegisterActivity.this,"success",Toast.LENGTH_SHORT);
                        progress.dismiss();

                        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }else{
                        Toast.makeText(RegisterActivity.this,"prob",Toast.LENGTH_SHORT);
                        progress.dismiss();
                    }

                }
            });
        }
        Toast.makeText(RegisterActivity.this,"did not enter big ",Toast.LENGTH_SHORT);
    }
}
