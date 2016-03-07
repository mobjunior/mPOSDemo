package example1.example1.mpos.mPOSDemo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by smaingi on 2016-02-09.
 */
public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] item;
    private final Integer[] imageId;
    public CustomList(Activity context, String[] item, Integer[] imageId) {
        super(context, R.layout.list_custom, item);
        this.context = context;
        this.item = item;
        this.imageId = imageId;

    }
    public View getView(int position,View view,ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_custom, null, true);

        TextView tvTitle=(TextView)rowView.findViewById(R.id.txt);
        tvTitle.setText(item[position]);

        ImageView imageView=(ImageView)rowView.findViewById(R.id.img);
        imageView.setImageResource(imageId[position]);

        return rowView;
    }
}
