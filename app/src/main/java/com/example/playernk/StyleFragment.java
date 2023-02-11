package com.example.playernk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.playernk.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StyleFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ArrayList<Style> styles;
    private StylesAdapter stylesAdapter;

    String selectedStyles;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        DB db = new DB(getContext());

        db.open();

        binding.cbRandom.setChecked(db.getConstant("random").equals("true"));

        binding.cbNewOnly.setChecked(db.getConstant("newOnly").equals("true"));

        binding.cbAquaring.setChecked(db.getConstant("aquare").equals("true"));

        binding.etStart.setText(db.getConstant("aquareStart"));

        binding.etRange.setText(db.getConstant("aquareRange"));

        selectedStyles = db.getConstant("selectedStyles");

        db.close();

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        styles = new ArrayList<>();
        stylesAdapter = new StylesAdapter(getContext(), styles);

        binding.rvList.setAdapter(stylesAdapter);

        stylesAdapter.setOnItemClickListener(new StylesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Style style) {

                style.selected = !style.selected;

                stylesAdapter.notifyDataSetChanged();

            }
        });

        stylesAdapter.setOnItemLongClickListener(new StylesAdapter.OnItemLongClickListener() {
            @Override
            public void onLongItemClick(Style item) {

            }
        });

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedStyles = "";

                for (Style style: styles
                     ) {
                    if (style.selected){
                        selectedStyles = selectedStyles + (selectedStyles.isEmpty() ? "" : ",") + style.name;
                    }
                }

                DB db = new DB(getContext());

                db.open();

                db.updateConstant("selectedStyles", selectedStyles);
                db.updateConstant("aquare", binding.cbAquaring.isChecked() ? "true" : "false");
                db.updateConstant("random", binding.cbRandom.isChecked() ? "true" : "false");
                db.updateConstant("newOnly", binding.cbNewOnly.isChecked() ? "true" : "false");
                db.updateConstant("aquareStart", binding.etStart.getText().toString());
                db.updateConstant("aquareRange", binding.etRange.getText().toString());

                db.close();

                Bundle result = new Bundle();
                result.putString("selectedStyles", selectedStyles);
                result.putBoolean("aquaring", binding.cbAquaring.isChecked());
                result.putBoolean("random", binding.cbRandom.isChecked());
                result.putBoolean("newOnly", binding.cbNewOnly.isChecked());
                result.putInt("aquareStart", Integer.valueOf(binding.etStart.getText().toString()));
                result.putInt("aquareRange", Integer.valueOf(binding.etRange.getText().toString()));
                getParentFragmentManager().setFragmentResult("selectStyle", result);

                NavHostFragment.findNavController(StyleFragment.this).popBackStack();

            }
        });

        UpdateStyles();

    }

    public void UpdateStyles(){

        styles.clear();

        VolleyRequestQueue.executeRequest(getContext(), Conn.addr + "styles", new JsonCallback() {
            @Override
            public void CallbackObject(JSONObject response) {

                try {
                    Style.getFromJsonArray(styles, response.getJSONArray("rows"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (!selectedStyles.isEmpty()){

                    for (String curSel: selectedStyles.split(",")
                         ) {

                        for (Style curStyle: styles
                             ) {

                            if (curStyle.name.equals(curSel)){

                                curStyle.selected = true;

                            }

                        }

                    }

                }

                stylesAdapter.notifyDataSetChanged();

            }

            @Override
            public void CallbackArray(JSONArray jsonArray) {

            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}