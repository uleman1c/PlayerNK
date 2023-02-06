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

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
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

                Bundle result = new Bundle();
                result.putString("style", style.name);
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