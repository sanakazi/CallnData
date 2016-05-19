package adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hireitservicesresources.callndata.R;
import models.CategoryModel;

/**
 * Created by Callndata on 4/22/2016.
 */
public class CategoryAdapter extends ArrayAdapter<CategoryModel> {
    private final Activity context;
    private ArrayList<CategoryModel> names;
    private static String TAG = CategoryAdapter.class.getSimpleName();

    public CategoryAdapter(Activity context, ArrayList<CategoryModel> names) {
        super(context, R.layout.listrow_buy_service_category, names);
        this.context = context;
        this.names = names;
        // TODO Auto-generated constructor stub
    }

    static class ViewHolder {
        public TextView txt_category;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listrow_buy_service_category, null, true);
            holder = new ViewHolder();
            holder.txt_category = (TextView) rowView.findViewById(R.id.txt_category);


            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        CategoryModel blipVar = names.get(position);
        if (blipVar != null) {

            holder.txt_category.setText(blipVar.getCat_fullName());
            //holder.imageView.setImageDrawable(Conf.loadImageFromURL(blipVar.getImage()));

        }

        return rowView;

    }
}
