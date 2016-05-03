package io.github.nestegg333.nestegg;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aqeelp on 4/18/16.
 */
public class PaymentAdapter extends BaseAdapter {
    private final static String TAG = "NestEgg";
    Context context;
    ArrayList<Payment> payments;

    public PaymentAdapter(Context c, ArrayList<Payment> p) {
        context = c;
        payments = p;

        Log.d(TAG, "Created new PaymentAdapter");
    }

    @Override
    public int getCount() {
        return payments.size();
    }

    @Override
    public Payment getItem(int position) {
        return payments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.payment_list_item, null);
        ((TextView) v.findViewById(R.id.paymentListItemDate)).setText(payments.get(position).date);
        ((TextView) v.findViewById(R.id.paymentListItemAmount)).setText("$" + payments.get(position).amount);

        return v;
    }

    public static class Payment {
        public String date;
        public String amount;

        public Payment(String d, String a) {
            date = d;
            amount = a;
        }
    }
}
