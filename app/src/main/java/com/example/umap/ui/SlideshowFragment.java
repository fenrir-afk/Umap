package com.example.umap.ui;

import static android.app.job.JobInfo.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.umap.MainActivity;
import com.example.umap.R;
import com.example.umap.databinding.FragmentSlideshowBinding;
import com.example.umap.ui.Registration.DashboardActivity;
import com.example.umap.ui.Registration.RegisterActivity;
import com.example.umap.ui.Registration.SQLiteHelper;
import com.example.umap.ui.state_holder.SlideshowViewModel;

public class SlideshowFragment extends Fragment {
    Button LogInButton, RegisterButton ;
    EditText Email, Password ;
    String EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    SQLiteHelper sqLiteHelper;
    Cursor cursor;
    String TempPassword = "NOT_FOUND" ;
    public static final String UserEmail = "";
    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LogInButton = (Button)root.findViewById(R.id.buttonLogin);

        RegisterButton = (Button)root.findViewById(R.id.buttonRegister);

        Email = (EditText)root.findViewById(R.id.editEmail);
        Password = (EditText)root.findViewById(R.id.editPassword);


        //Добавляем слушателя для login
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Calling EditText is empty or no method.
                CheckEditTextStatus();
                // Calling login method.
                slideshowViewModel.LoginFunction(getContext(),EditTextEmptyHolder,Email.getText().toString(),Password.getText().toString());


            }
        });

        // Добавление слушателя для регистрации
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
                slideshowViewModel.sendNotification(getContext());

            }
        });


        return root;
    }
    public void CheckEditTextStatus(){

        // Getting value from All EditText and storing into String Variables.
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        // Checking EditText is empty or no using TextUtils.
        if( TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)){

            EditTextEmptyHolder = false ;

        }
        else {

            EditTextEmptyHolder = true ;
        }
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}