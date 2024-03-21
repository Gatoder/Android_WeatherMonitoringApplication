package com.example.o3uit.ModelLogin;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.o3uit.MainActivity;
import com.example.o3uit.R;
import com.example.o3uit.Service.ApiService;


import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    ApiService apiServiceLogin;

    private EditText edtUsername,edtPass;

    private Button signInButton;

    private TextView buttonChangLang;

    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);
        LoadElement();

        buttonChangLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLanguage();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });


    }
    private void SignIn(){
        String username = edtUsername.getText().toString();
        String password = edtPass.getText().toString();

        if(isEmptyInput()) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url.GetURLMain())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiServiceLogin = retrofit.create(ApiService.class);

        // Thực hiện POST request
        Call<Asset> call = apiServiceLogin.authenticate("openremote", username, password, "password", "cakho12345@gmail.com");
        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    Asset result = response.body();
                    String token = result.getAccessToken();
                    String userName = edtUsername.getText().toString();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("token",token);
                    intent.putExtra("Username",userName);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Toast.makeText(LoginActivity.this, "Tên tài khoản hoặc mật khẩu không đúng !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Log.e("RetrofitError", t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối.", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private boolean isEmptyInput(){
        if(edtUsername.getText().toString().isEmpty()){
            ShowError(edtUsername,"Bạn chưa nhập thông tin");
            return true;
        }
        else if(edtPass.getText().toString().isEmpty()){
            ShowError(edtPass,"Bạn chưa nhập thông tin");
            return true;
        }
        return false;
    }


    private void LoadElement(){
        url= new URL();
        //chekboxRemember =findViewById(R.id.checkBoxRememberme);
        edtUsername= findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        signInButton = findViewById(R.id.sign_in_button_homepage);
        buttonChangLang = findViewById(R.id.changelangbtn);
    }

    private void ShowError(EditText edtInput,String s){
        edtInput.setError(s);
        edtInput.requestFocus();
    }

    public void ChangeLanguage() {
        final String[] listitems = {"Vietnamese", "English"};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle("Choose Language...");

        mBuilder.setSingleChoiceItems(listitems, -1, (dialog, which) -> {
            String selectedLanguage = listitems[which];

            // Set the selected language based on the chosen item
            switch (selectedLanguage) {
                case "Vietnamese":
                    setLocate("vi");
                    break;
                case "English":
                    setLocate("en");
                    break;
                default:
                    setLocate("en");
                    break;
            }

            recreate();
            dialog.dismiss();
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocate(String Lang) {
        Locale locale = new Locale(Lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Setting", MODE_PRIVATE).edit();
        editor.putString("My_Lang", Lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocate(language);
    }
}