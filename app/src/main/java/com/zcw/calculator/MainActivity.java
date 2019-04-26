package com.zcw.calculator;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zcw.calculator.fragments.ButtonFragment;
import com.zcw.calculator.fragments.ResultFragment;

public class MainActivity extends FragmentActivity implements
        ResultFragment.OnFragmentInteractionListener, ButtonFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * 初始化函数，添加按钮fragment和显示结果fragment
     */
    private void init() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        ButtonFragment buttonFragment = new ButtonFragment();
        transaction.add(R.id.container_button, buttonFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
