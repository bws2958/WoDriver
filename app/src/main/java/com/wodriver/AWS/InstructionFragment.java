package com.wodriver.AWS;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wodriver.R;

/**
 * Created by user on 2016-11-24.
 */

public class InstructionFragment extends FragmentBase{
    private static final String ARGUMENT_FEATURE_NAME = "feature_name";

    public static InstructionFragment newInstance(final String FeatureName){
        InstructionFragment fragment = new InstructionFragment();
        Bundle args = new Bundle();
        args.putString(InstructionFragment.ARGUMENT_FEATURE_NAME, FeatureName);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instruction, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle args = getArguments();
        final String FeatureName = args.getString(ARGUMENT_FEATURE_NAME);
        final Configuration.Feature feature = Configuration.getFeatureByName(FeatureName);

        // Set the title for the instruction fragment
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle(feature.titleResId);

        final TextView textView = (TextView) view.findViewById(R.id.text_feature_overview);
        textView.setText(feature.titleResId);

        final TextView textView1 = (TextView) view.findViewById(R.id.text_feature_description);
        if(feature.descriptionResId > 0){
            textView1.setText(feature.descriptionResId);
        }else{
            final TextView textView2 = (TextView) view.findViewById(R.id.text_feature_description_heading);
            textView2.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
        }
        final TextView textView3 = (TextView) view.findViewById(R.id.text_feature_powered_by);
        textView3.setText(feature.poweredByResId);

        final ArrayAdapter<Configuration.Item> adapter = new ArrayAdapter<Configuration.Item>(getActivity(), R.layout.list_item_icon_text_with_subtitle){
            @NonNull
            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                View view = convertView;
                if(view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.list_item_icon_text_with_subtitle, parent, false);

                final Configuration.Item item = getItem(position);
                final ImageView imageView = (ImageView) view.findViewById(R.id.list_item_icon);
                imageView.setImageResource(item.iconResId);

                final TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(item.buttonTextResId);

                return view;
            }
        };
        adapter.addAll(feature.demos);
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final Configuration.Item item = adapter.getItem(position);
                final AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                if(appCompatActivity != null){
                    final Fragment fragment = Fragment.instantiate(getActivity(), item.fragmentClassName);
                    appCompatActivity.getSupportFragmentManager()
                            .beginTransaction()
//                            .replace(R.id.main_fragment_container, fragment, item.fragmentClassName)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    appCompatActivity.getSupportActionBar().setTitle(item.titleResId);
                }
            }
        });

        listView.setBackgroundColor(Color.WHITE);

        final UserSettings userSettings = UserSettings.getInstance(view.getContext());
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(final Void... params) {
                userSettings.loadFromDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                listView.setBackgroundColor(userSettings.getBackgroudColor());
            }
        }.execute();
    }
}
