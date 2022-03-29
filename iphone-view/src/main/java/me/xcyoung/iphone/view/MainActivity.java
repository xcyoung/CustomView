package me.xcyoung.iphone.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iPhoneView iPhoneView = findViewById(R.id.iPhoneView);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_background);
//        iPhoneView.setBackground(bitmap);
    }
}