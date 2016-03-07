package example1.example1.mpos.mPOSDemo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BalanceInquiry extends AppCompatActivity {
    private String[] biArray={
            "By Card",
            "By Mwallet"
    };
    Integer[] imageId={
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.bi
    };
    private ListView lvBalanceinquiry=null;
    //ArrayAdapter biAdapter=null;
    //Button btnbyCard=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);


        CustomList biAdapter=new CustomList(BalanceInquiry.this,biArray,imageId);

        lvBalanceinquiry=(ListView)findViewById(R.id.lvBalanceinquiry);
        //biAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,biArray);
        lvBalanceinquiry.setAdapter(biAdapter);

        lvBalanceinquiry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    Intent intent=new Intent(BalanceInquiry.this,BalanceInquiryByCard.class);
                    startActivity(intent);
                }
                else if (position==1)
                {
                    Intent intent=new Intent(BalanceInquiry.this,biByMWallet.class);
                    startActivity(intent);
                }
            }
        });
    }
}
