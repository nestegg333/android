package io.github.nestegg333.nestegg;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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

        // TODO temporary:
        ArrayList<PaymentAdapter.Payment> p = new ArrayList<>();
        p.add(new PaymentAdapter.Payment("Tuesday", "$" + Utils.amountToString(500)));
        p.add(new PaymentAdapter.Payment("Yesterday", "$" + Utils.amountToString(1500)));
        p.add(new PaymentAdapter.Payment("Last month", "$" + Utils.amountToString(3000)));
        adapter = new PaymentAdapter(this, p);
        setAdapter(adapter);

        // TODO:
        // FetchAllPayments f = new FetchAllPayments(USERID, this);
        // f.execute(URL/payments/USERIR);
    }

    public void setAdapter(PaymentAdapter pa) {
        ListView list = (ListView) findViewById(R.id.paymentHistoryListView);
        adapter = pa;
        list.setAdapter(adapter);
    }

}
