package com.example.playernk;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "password";

    // TODO: Rename and change types of parameters
    private String name;
    private String password;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            password = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        ((EditText) root.findViewById(R.id.etName)).setText(name);
        ((EditText) root.findViewById(R.id.etPassword)).setText(password);

        root.findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText etName = root.findViewById(R.id.etName);
                EditText etPassword = root.findViewById(R.id.etPassword);
                EditText etPasswordRepeat = root.findViewById(R.id.etPasswordRepeat);

                TextView tvError = root.findViewById(R.id.tvError);

                String userName = etName.getText().toString().replaceAll(" ", "");
                String userPassword = etPassword.getText().toString().replaceAll(" ", "");
                String userPasswordRepeat = etPasswordRepeat.getText().toString().replaceAll(" ", "");

                if (userName.isEmpty()){

                    tvError.setText("name must be specified");

                } else if (userPassword.isEmpty()) {

                    tvError.setText("password must be specified");

                } else if (!userPassword.equals(userPasswordRepeat)) {

                    tvError.setText("passwords must be equals");

                } else {

                    tvError.setText("registering");

                    VolleyRequestQueue.executeRequest(getContext(), Conn.addr + "users?name=" + userName, new JsonCallback() {
                        @Override
                        public void CallbackObject(JSONObject jsonObject) {

                            JSONArray users = new JSONArray();

                            try {
                                users = jsonObject.getJSONArray("rows");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if (users.length() != 0){

                                tvError.setText("user found");

                                //root.findViewById(R.id.btnRegister).setVisibility(View.VISIBLE);

                            } else {

                                VolleyRequestQueue.executeRequest(getContext(), Conn.addr + "adduser?name=" + userName + "&password=" + userPassword, new JsonCallback() {
                                    @Override
                                    public void CallbackObject(JSONObject jsonObject) {

                                        Boolean result = false;
                                        try {
                                            result = jsonObject.getBoolean("result");
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        if (result){


                                            Bundle responce = new Bundle();
                                            responce.putString("userName", userName);
                                            getParentFragmentManager().setFragmentResult("userRegister", responce);

                                            NavHostFragment.findNavController(RegisterFragment.this).popBackStack();

                                        }

                                    }

                                    @Override
                                    public void CallbackArray(JSONArray jsonArray) {

                                    }
                                });


                            }
                        }

                        @Override
                        public void CallbackArray(JSONArray jsonArray) {

                        }
                    });


                }

            }
        });

        return root;
    }
}