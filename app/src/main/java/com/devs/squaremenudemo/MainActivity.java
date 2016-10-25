package com.devs.squaremenudemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.devs.squaremenu.OnMenuClickListener;
import com.devs.squaremenu.SquareMenu;

public class MainActivity extends AppCompatActivity implements OnMenuClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SquareMenu mSquareMenu = (SquareMenu) findViewById(R.id.sm);
        //mSquareMenu.setOnMenuClickListener(this);

    }

    @Override
    public void onMenuOpen() {
        Toast.makeText(this, "Open", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuClose() {
        Toast.makeText(this, "Close", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickMenu1() {
        Toast.makeText(this, "Menu1", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickMenu2() {
        Toast.makeText(this, "Menu2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickMenu3() {
        Toast.makeText(this, "Menu3", Toast.LENGTH_SHORT).show();
    }
}
