package example1.example1.mpos.mPOSDemo;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

public class biByMWallet extends AppCompatActivity {

    Button btnmwok=null;

    Spinner spAccounts=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bi_by_mwallet);

        spAccounts=(Spinner)findViewById(R.id.spMwalletAcc);

        ArrayAdapter<CharSequence> accounts=ArrayAdapter.createFromResource(this,
                R.array.AccountsArray,android.R.layout.simple_spinner_item);
        accounts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccounts.setAdapter(accounts);

        btnmwok=(Button)findViewById(R.id.btnmWOk);
        btnmwok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popupWindow;
                View view=getLayoutInflater().inflate(R.layout.pin_input,null);
                popupWindow=new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
                popupWindow.setAnimationStyle(R.style.CardView);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                Button popOk=(Button)view.findViewById(R.id.btnOK);
                popOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                Button popCancel=(Button)view.findViewById(R.id.btnCancel);
                popCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }
}
