package io.github.nestegg333.nestegg;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FullPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utils.hideActionBar(this);

        ListView list = (ListView) findViewById(R.id.paymentHistoryListView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout headerView = (LinearLayout) inflater.inflate(R.layout.payment_list_item,
                null);
        ((TextView) headerView.findViewById(R.id.paymentListItemDate)).setText("Yesterday");
        ((TextView) headerView.findViewById(R.id.paymentListItemAmount)).setText("$5.66");
        list.addHeaderView(headerView);
        // TODO: make an adapater for this (ugh)
    }

}
