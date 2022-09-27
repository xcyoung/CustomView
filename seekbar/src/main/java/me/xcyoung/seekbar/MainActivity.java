package me.xcyoung.seekbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Seekbar seekbar = findViewById(R.id.seekbar);
        TextView textView = findViewById(R.id.tvLog);
        seekbar.setOnSeekBarChangeListener((current, old) -> {
            textView.setText("current: " + current + ", old:" + old);
        });
    }
}