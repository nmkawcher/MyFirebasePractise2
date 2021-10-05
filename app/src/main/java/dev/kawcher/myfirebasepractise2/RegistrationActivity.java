package dev.kawcher.myfirebasepractise2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailET,passwordET,nameET,confirmPasswordET;
    private Button registerBtn;
    private TextView loginTV;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=emailET.getText().toString();
                String password=passwordET.getText().toString();
                String name=nameET.getText().toString();
                String cPassword=confirmPasswordET.getText().toString();

                if(name.isEmpty()){
                    nameET.setError("required");
                   nameET.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    emailET.setError("required");
                    emailET.requestFocus();
                    return;
                }
                if(password.equals("")||password.length()<6){
                    passwordET.setError("password must 6 character");
                    passwordET.requestFocus();
                    return;
                }

                if(!cPassword.equals(password)){
                    confirmPasswordET.setError("password doesn't match");
                   confirmPasswordET.requestFocus();
                    return;
                }

                dialog.show();
                firebaseAuth=FirebaseAuth.getInstance();

                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    HashMap<String,String>map=new HashMap<>();
                                    map.put("name",name);
                                    map.put("email",email);
                                    FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid()).push().setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegistrationActivity.this, "register", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                                                   dialog.dismiss();
                                            }
                                        }
                                    });

                                  }else {
                                    Toast.makeText(RegistrationActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                     dialog.dismiss();
                                }
                            }
                        });

            }
        });

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                finish();
            }
        });



    }

    private void init(){
        dialog=new ProgressDialog(RegistrationActivity.this);
        emailET=findViewById(R.id.email_et);
        passwordET=findViewById(R.id.password_et);
        confirmPasswordET=findViewById(R.id.c_password_et);
        nameET=findViewById(R.id.name_et);
        registerBtn=findViewById(R.id.btn_registration);
        loginTV=findViewById(R.id.tv_login);
    }
}