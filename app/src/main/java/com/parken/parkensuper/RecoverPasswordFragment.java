package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class RecoverPasswordFragment extends Fragment {

    private View mProgressView;
    private View mRecoverFormView;
    private EditText credential;
    private String user;
    private ShPref session;
    private Button next;
    private RecoverPasswordActivity activityRecoverPassword;
    private VolleySingleton volley;
    private ConstraintLayout constraintForm;
    protected RequestQueue fRequestQueue;
    private String id;
    private String cel;
    private String em;
    private String value;
    private String column = "contrasena";


    private LoginActivity loginAct = new LoginActivity();
    private RecoverPasswordActivity recoverPasswordActivity = new RecoverPasswordActivity();

    FirebaseAuth mAuth;

    public RecoverPasswordFragment() {
        // Required empty public constructor
    }


    public static RecoverPasswordFragment newInstance() {
        RecoverPasswordFragment fragment = new RecoverPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recover_password, container, false);

        credential = root.findViewById(R.id.editTextMail);
        session = new ShPref(getActivity());
        mRecoverFormView = root.findViewById(R.id.recover_form);
        mProgressView = root.findViewById(R.id.recover_progress);

        constraintForm = root.findViewById(R.id.constraint_form);
        next = root.findViewById(R.id.btnRecover);

        volley = VolleySingleton.getInstance(getActivity().getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        mAuth.getApp();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRecover();
                // constraintForm.setVisibility(View.GONE);


            }
        });

        credential.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRecover();
                    return true;
                }
                return false;
            }
        });

        return root;

    }

    private void attemptRecover(){

        // Reset errors.
        credential.setError(null);

        // Store values at the time of the login attempt.
        user = credential.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(user)) {
            credential.setError(getString(R.string.error_credential_field_required));
            focusView = credential;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //mAuthTask = new LoginActivity.UserLoginTask(correo, contrasena, app);
            //mAuthTask.execute((Void) null);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(credential.getWindowToken(), 0);
            //Verificar que exista el usuario en la base de datos
            verifyUser(user);
            // enviarJson(correo, contrasena, app);

        }
    }

    private void verifyUser(String user) {
        HashMap<String, String> parametros = new HashMap();
        parametros.put("credencial", user);
        parametros.put("tipo", "1");


        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_VERYFY_ID,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //onConnectionFinished();
                        Log.d("VerifyID", response.toString());
                        try{
                            showProgress(false);

                            if(response.getString("success").equals("1")){
                                //Si existe el usuario en la base de datos
                                Log.d("VerifyID", response.getString("success"));
                                //Guardamos el mail
                                value = response.getString("Contrasena");
                                em = response.getString("Email");
                                //Guardamos el cel
                                cel = response.getString("Celular");
                                id = response.getString("idSupervisor");
                                //modifyPassword(id,em,cel,"");
                                verificarCelular(cel);


                            }else{

                                dialogNoUser().show();
                                Log.d("VerifyID", response.getString("success"));
                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //onConnectionFailed(error.getMessage());
                        Log.d("VerifyID", "Error Respuesta en JSON: " + error.getMessage());
                        showProgress(false);
                        dialogNoConnection().show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    private void modifyPassword(String id, String em, String cel, String code) {
        //Mostrar el fragment para ingresar el código de verificación
        String phoneNumber = cel;
        //String phoneNumberFormatted = codigoPais + " " + celular.substring(0,2) + " " + celular.substring(2,6) + " " + celular.substring(6,10);

        //Before modify the password we sent the verification code


        VerifyCodeFragment verifyCodeFragment = (VerifyCodeFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.constraint2_form);

        if (verifyCodeFragment == null) {
            verifyCodeFragment = VerifyCodeFragment.newInstance();

            Bundle arguments = new Bundle();
            arguments.putString("origin","recoverPasswordActivity");

            arguments.putString("id", id);
            arguments.putString("column", "contrasena");
            arguments.putString("value", cel);
            arguments.putString("veriId", code);
            arguments.putString("phone", phoneNumber);
            arguments.putString("phoneFormatted", phoneNumber);

            verifyCodeFragment.setArguments(arguments);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nestedScrollForm, verifyCodeFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public AlertDialog dialogNoUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.alert_no_user_tittle))
                .setMessage(getString(R.string.alert_no_user_message))
                .setPositiveButton(getString(R.string.affirmative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.alert_no_connection_tittle))
                .setMessage(getString(R.string.alert_no_connection_message))
                .setPositiveButton(getString(R.string.affirmative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public android.support.v7.app.AlertDialog dialogVerificationFailed() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        builder.setTitle("Error")
                .setMessage("El código ingresado no coincide con el código enviado.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });

        return builder.create();
    }

    public android.support.v7.app.AlertDialog dialogError(String msg) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        builder.setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });

        return builder.create();
    }

    public void verificarCelular(String phoneNumber){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        showProgress(false);
                        //dialogVerificationFailed().show();
                        //dialogNoConnection().show();

                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("FAILED", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                            dialogError(e.getMessage()).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                            dialogError(e.getMessage()).show();
                        }

                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        String code = s;
                        showProgress(false);
                        //abrirVerifyCode(code);
                        modifyPassword(id, em, cel, code);
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                }

        );

    }

    //Esta da el acceso
    public void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            /*
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            showProgress(false);
                            if(origin.equals("createActivity")){
                                enviarJsonSign(phoneNumber);
                            }else{
                                if(origin.equals("recoverPasswordActivity")){
                                    //Llamar al activity PasswordActivity
                                    Intent password = new Intent(getActivity(), PasswordActivity.class);
                                    password.putExtra("id", id);
                                    password.putExtra("column", column);
                                    password.putExtra("value", value);
                                    password.putExtra("origin", "editProfileActivity");
                                    startActivity(password);
                                } else{
                                    actualizarPerfilAutomovilista(id,column,phoneNumber);
                                }

                            }

                            //dialogSuccess().show();

                            */

                            //modifyPassword(id, em, cel, "");

                            //Llamar al activity PasswordActivity
                            Intent password = new Intent(getActivity(), PasswordActivity.class);
                            password.putExtra("id", id);
                            password.putExtra("column", column);
                            password.putExtra("value", value);
                            password.putExtra("origin", "recoverPasswordActivity");
                            startActivity(password);


                            getActivity().finish();
                            loginAct.activityLogin.finish();
                            //passwordActivity.activityPassword.finish();
                            recoverPasswordActivity.activityRecoverPassword.finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showProgress(false);
                                dialogVerificationFailed().show();

                            }

                        }
                    }
                });
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRecoverFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecoverFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecoverFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRecoverFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

        public void onButtonPressed(Uri uri) {
    }
}
