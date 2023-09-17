package com.example.umap.ui.state_holder;

import static android.app.job.JobInfo.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umap.MainActivity;
import com.example.umap.R;
import com.example.umap.databinding.FragmentSlideshowBinding;
import com.example.umap.ui.Registration.DashboardActivity;
import com.example.umap.ui.Registration.SQLiteHelper;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

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

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }


    public void LoginFunction(Context context,Boolean EditTextEmptyHolder,String EmailHolder, String PasswordHolder){
        sqLiteHelper = new SQLiteHelper(context);

        if(EditTextEmptyHolder) {

            sqLiteDatabaseObj = sqLiteHelper.getWritableDatabase();


            cursor = sqLiteDatabaseObj.query(SQLiteHelper.TABLE_NAME, null, " " + SQLiteHelper.Table_Column_2_Email + "=?", new String[]{EmailHolder}, null, null, null);

            while (cursor.moveToNext()) {

                if (cursor.isFirst()) {

                    cursor.moveToFirst();

                    // Storing Password associated with entered email.
                    TempPassword = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_Password));
                    //TempPassword = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_Password));

                    // Closing cursor.
                    cursor.close();
                }
            }

            // Calling method to check final result ..
            CheckFinalResult(context,PasswordHolder);


        }
        else {
            //If any of login EditText empty then this block will be executed.
            Toast.makeText(context,"Please Enter UserName or Password.",Toast.LENGTH_LONG).show();

        }
    }
    public void CheckFinalResult(Context context,String PasswordHolder){

        if(TempPassword.equalsIgnoreCase(PasswordHolder))
        {

            Toast.makeText(context,"Login Successful",Toast.LENGTH_LONG).show();

            // Переход к dashboard после сообщения об успешном входе в систему.
            Intent intent = new Intent(context, DashboardActivity.class);

            // Отправка Email to Dashboard Activity используя intent.
            intent.putExtra(UserEmail, EmailHolder);
            context.startActivity(intent);
        }
        else {

            Toast.makeText(context,"UserName or Password is Wrong, Please Try Again.",Toast.LENGTH_LONG).show();

        }
        TempPassword = "NOT_FOUND" ;

    }


    public void sendNotification(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannelIfNeeded(notificationManager);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context,
                    0, new Intent(context, getClass()).addFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(context,
                    0, new Intent(context, getClass()).addFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }



        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,"123")
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Umap")
                        .setContentText("Поздравляем! Вы стали частью проетка Umap" )
                        .setPriority(PRIORITY_HIGH);
        notificationManager.notify(1,notificationBuilder.build());

    }
    public void createChannelIfNeeded(NotificationManager manager) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("123","123",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }



}