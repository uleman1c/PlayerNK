package com.example.playernk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
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
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getParentFragmentManager().setFragmentResultListener("userRegister", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                String userName = bundle.getString("userName");

                setUserLogined(userName);


            }
        });



    }

    private void setUserLogined(String userName) {
        DB db = new DB(getContext());
        db.open();

        db.updateConstant("userName", userName);

        db.close();

        Bundle responce = new Bundle();
        responce.putString("userName", userName);
        getParentFragmentManager().setFragmentResult("userLogin", responce);

        NavHostFragment.findNavController(LoginFragment.this).popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        root.findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText etName = root.findViewById(R.id.etName);
                EditText etPassword = root.findViewById(R.id.etPassword);

                String userName = etName.getText().toString().replaceAll(" ", "");
                String userPassword = etPassword.getText().toString().replaceAll(" ", "");

                Bundle bundle = new Bundle();

                bundle.putString("name", userName);
                bundle.putString("password", userPassword);

                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.registerFragment, bundle);

            }
        });



        root.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText etName = root.findViewById(R.id.etName);
                EditText etPassword = root.findViewById(R.id.etPassword);

                TextView tvError = root.findViewById(R.id.tvError);

                String userName = etName.getText().toString().replaceAll(" ", "");
                String userPassword = etPassword.getText().toString().replaceAll(" ", "");

                if (userName.isEmpty()){

                    tvError.setText("name must be specified");

                } else if (userPassword.isEmpty()) {

                    tvError.setText("password must be specified");

                } else {

                    tvError.setText("logining");

                    VolleyRequestQueue.executeRequest(getContext(), Conn.addr + "users?name=" + userName + "&password=" + userPassword, new JsonCallback() {
                        @Override
                        public void CallbackObject(JSONObject jsonObject) {

                            JSONArray users = new JSONArray();

                            try {
                                users = jsonObject.getJSONArray("rows");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if (users.length() == 0){

                                tvError.setText("user not found");

                                root.findViewById(R.id.btnRegister).setVisibility(View.VISIBLE);

                            } else {
                                setUserLogined(userName);
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