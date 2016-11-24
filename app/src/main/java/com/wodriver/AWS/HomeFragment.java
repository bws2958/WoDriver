package com.wodriver.AWS;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.wodriver.R;

/**
 * Created by user on 2016-11-24.
 */

public class HomeFragment extends FragmentBase{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final wodListAdapter adapter = new wodListAdapter(getActivity());
    }

    private static final class wodListAdapter extends ArrayAdapter<Configuration.Feature>{
        private LayoutInflater inflater;

        public wodListAdapter(final Context context){
            super(context, R.layout.list_item_icon_text_with_subtitle);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                view = inflater.inflate(R.layout.list_item_icon_text_with_subtitle, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.iconImageView = (ImageView) view.findViewById(R.id.list_item_icon);
                viewHolder.titleTextView = (TextView) view.findViewById(R.id.list_item_title);
                viewHolder.subtitleTextView = (TextView) view.findViewById(R.id.list_item_subtitle);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Configuration.Feature item = getItem(position);
            viewHolder.iconImageView.setImageResource(item.iconResId);
            viewHolder.titleTextView.setText(item.titleResId);
            viewHolder.subtitleTextView.setText(item.subtitleResId);

            return view;
        }
    }

    private static final class ViewHolder{
        ImageView iconImageView;
        TextView titleTextView;
        TextView subtitleTextView;
    }
}
