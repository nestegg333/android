package io.github.nestegg333.nestegg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import io.github.nestegg333.nestegg.fetch.FetchAllPayments;

public class FullPaymentActivity extends AppCompatActivity {
    private final static String TAG = "NestEgg";
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utils.hideActionBar(this);

        Intent intent = getIntent();
        Bundle userData = intent.getExtras();

        new FetchAllPayments(userData.getInt(Utils.OWNER_ID), this);
    }

    public void setAdapter(PaymentAdapter pa) {
        ListView list = (ListView) findViewById(R.id.payment_history_list_view);
        adapter = pa;
        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);

        findViewById(R.id.no_payments).setVisibility(View.GONE);
        findViewById(R.id.payment_list_header).setVisibility(View.VISIBLE);
    }

    public void displayNoPayments() {
        findViewById(R.id.no_payments).setVisibility(View.VISIBLE);
        findViewById(R.id.payment_list_header).setVisibility(View.GONE);
        findViewById(R.id.payment_history_list_view).setVisibility(View.GONE);
    }

}
