package io.github.nestegg333.nestegg;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        ((TextView) v.findViewById(R.id.paymentListItemAmount)).setText(payments.get(position).amount);
        /*LinearLayout v = new LinearLayout(context);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        v.setOrientation(LinearLayout.HORIZONTAL);
        v.setPadding(16, 16, 16, 16);

        if (position < payments.size()) {
            // TODO can't get weights to display properly
            TextView dateView = new TextView(context);
            dateView.setText(payments.get(position).date);
            dateView.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            dateView.setGravity(Gravity.LEFT);
            dateView.setPadding(8, 8, 8, 8);
            v.addView(dateView);

            TextView amountView = new TextView(context);
            amountView.setText(payments.get(position).amount);
            amountView.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            amountView.setGravity(Gravity.RIGHT);
            amountView.setPadding(8, 8, 8, 8);
            v.addView(amountView);
        }*/

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
