package example1.example1.mpos.mPOSDemo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardGridView;
import it.gmariotti.cardslib.library.view.CardListView;

public class Inquiries extends AppCompatActivity {
    CardGridView lvInquiries=null;
    private ArrayList<Card> cards=null;
    //private String[] inquiriesArray={"Balance Inquiry","Mini Statement"};
    Integer[] iImageId={
            R.drawable.bi,
            R.drawable.ic_account_balance_black_24dp,
            R.drawable.bi,
            R.drawable.bi,
            R.drawable.bi,
            R.drawable.bi,
            R.drawable.bi,
            R.drawable.bi
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiries);
        lvInquiries=(CardGridView)findViewById(R.id.lvInquiries);
        cards=new ArrayList<Card>();

        for (int i = 0; i<iImageId.length; i++) {
            // Create a Card 1
            Card card = new Card(this);
            // Create a CardHeader
            CardHeader header = new CardHeader(this);
            // Add Header to card
            header.setTitle("Enquiry");
            card.setTitle("Balance Enquiry");
            card.addCardHeader(header);
            card.isClickable();
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
//                    if (card.getId()==iImageId[0].toString())
//                    {
                    Intent intent = new Intent(Inquiries.this, BalanceInquiry.class);
                    startActivity(intent);
                    //}
                }
            });

            CardThumbnail thumb = new CardThumbnail(this);
            thumb.setDrawableResource(iImageId[i]);
            LightingColorFilter colorFilter=new LightingColorFilter(Color.GREEN,Color.BLUE);
            //thumb.setDrawableResource;
            //Drawable drawable=getResources().getDrawable(iImageId[i]);
            //thumb.getDrawableResource();
            card.addCardThumbnail(thumb);

            cards.add(card);
        }

//        Card card2 = new Card(this);
//        // Create a CardHeader
//        CardHeader header2 = new CardHeader(this);
//        // Add Header to card
//        header2.setTitle("Enquiry" );
//        card2.setTitle("MiniStatement");
//        card2.addCardHeader(header);
//        card2.isClickable();
//        card2.setOnClickListener(new Card.OnCardClickListener() {
//            @Override++++++++++++++++++++++++++++
//            public void onClick(Card card, View view) {
////                    if (card.getId()==iImageId[0].toString())
////                    {
//                //Intent intent=new Intent(Inquiries.this,BalanceInquiry.class);
//                //startActivity(intent);
//                //}
//            }
//        });
//
//        CardThumbnail thumb2 = new CardThumbnail(this);
//        thumb2.setDrawableResource(iImageId[1]);
//        card2.addCardThumbnail(thumb2);
//
//        cards.add(card2);

        //CustomList bilAdapter=new CustomList(Inquiries.this,inquiriesArray,iImageId);
        //arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,inquiriesArray);
        CardGridArrayAdapter cardGridArrayAdapter=new CardGridArrayAdapter(this,cards);
        if(lvInquiries!=null)
        {
            lvInquiries.setAdapter(cardGridArrayAdapter);
        }


//        lvInquiries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(position==0)
//                {
//                    Intent intent=new Intent(Inquiries.this,BalanceInquiry.class);
//                    startActivity(intent);
//                }
//            }
//        });

    }
}
