package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VerifyCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyCodeFragment extends Fragment {

    FirebaseAuth mAuth;

    TimerTask timerTask = new TimerTask();

    TextView txtVerifying, txtNumberConfirm, txtMinute, txtSecond, txtInstructions;
    AutoCompleteTextView editCode;
    Button verify2, wrong, resend;
    String codeFinal, verificationId, phoneNumber, phoneNumberFormatted, origin;
    View form2;
    VerifyCodeFragment fragmentCodeVerify;

    private LoginActivity loginAct = new LoginActivity();
    private VerifyActivity verifyActivity = new VerifyActivity();

    private View mProgressView;
    private View mVerifyFormView;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private ShPref session;

    // TODO: Rename and change types of parameters
    private String nombre, apellido, correo, password;
    private  String id, column, value;

    private InformationActivity infAct = new InformationActivity();
    //private ParkenActivity actParken;


    public VerifyCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VerifyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyCodeFragment newInstance() {
        VerifyCodeFragment fragment = new VerifyCodeFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            origin = getArguments().getString("origin");
            if(origin.equals("createActivity")){
                nombre = getArguments().getString("nombre");
                apellido = getArguments().getString("apellido");
                correo = getArguments().getString("correo");
                password = getArguments().getString("password");

            }
            if (origin.equals("informationActivity")){

                id = getArguments().getString("id");
                column = getArguments().getString("column");
                value = getArguments().getString("value");

            }

        }

        volley = VolleySingleton.getInstance(getContext());
        fRequestQueue = volley.getRequestQueue();
        fragmentCodeVerify = this;
        //actParken = new ParkenActivity();
        session = new ShPref(fragmentCodeVerify.getActivity());


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mVerifyFormView = getActivity().findViewById(R.id.nestedScrollForm);
        mProgressView = getActivity().findViewById(R.id.verifiy_progress);

        View root = inflater.inflate(R.layout.fragment_verify_code, container, false);


        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        mAuth.getApp();

        txtNumberConfirm = root.findViewById(R.id.textViewNumberConfirm);
        txtVerifying = root.findViewById(R.id.textViewVerifing);
        txtInstructions = root.findViewById(R.id.textViewInstructions);
        editCode = root.findViewById(R.id.editTextCodigo);
        verify2 = root.findViewById(R.id.btnVerificar2);
        wrong = root.findViewById(R.id.btnWrongNumber);
        resend = root.findViewById(R.id.btnResendCode);
        form2 = root.findViewById(R.id.verify_form);

        txtSecond = root.findViewById(R.id.textViewSeconds);


        if(getArguments() != null) {

            verificationId = getArguments().getString("veriId");
            phoneNumber = getArguments().getString("phone");
            phoneNumberFormatted = getArguments().getString("phoneFormatted");

            origin = getArguments().getString("origin");
            if(origin.equals("recoverPasswordActivity")){
                //Si el activity origen es para cambiar la contraseña entonces modificamos las etiquetas
                txtVerifying.setVisibility(View.GONE);
                txtNumberConfirm.setVisibility(View.GONE);
                //txtInstructions.setText("Se ha enviado un código de verificación al número celular vinculado al correo electrónico.");
                txtInstructions.setText("Para modificar tu contraseña, ingresa el código de verificación que hemos enviado al número celular registrado en Parken.");
                wrong.setVisibility(View.GONE);
                verify2.setText("Siguiente");

                ((RecoverPasswordActivity) getActivity())
                        .setupActionBar(false);

            }

            if(origin.equals("informationActivity"))
                ((VerifyActivity) getActivity())
                        .setupActionBar(false);
                if(origin.equals("createActivity"))
                    ((VerifyActivity) getActivity())
                            .setupActionBar(false);
        }

        txtVerifying.setText("Verificando "+ phoneNumberFormatted);
        txtNumberConfirm.setText(phoneNumberFormatted);

        wrong.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               VerifyFragment verifiyFragment = new VerifyFragment();
                session.setVerifying(false);
                timerTask.cancel(true);

               Bundle arg = new Bundle();

               if(origin.equals("createActivity")){
                   arg.putString("phone", phoneNumber);
                   arg.putString("phoneFormatted", phoneNumberFormatted);
                   arg.putString("nombre", nombre);
                   arg.putString("apellido", apellido);
                   arg.putString("correo", correo);
                   arg.putString("password", password);
                   arg.putString("origin", origin);
               }
               if(origin.equals("informationActivity")){
                   arg.putString("id", id);
                   arg.putString("phone", phoneNumber);
                   arg.putString("phoneFormatted", phoneNumberFormatted);
                   arg.putString("column", column);
                   arg.putString("value", value);
                   arg.putString("origin", origin);
               }
               verifiyFragment.setArguments(arg);


               getActivity().getSupportFragmentManager().beginTransaction()
                       //.remove(getParentFragment())
                       .replace(R.id.nestedScrollForm, verifiyFragment)
                       .addToBackStack(null)
                       .commit();
           }
        });


        verify2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editCode.getWindowToken(), 0);

                codeFinal = editCode.getText().toString().trim();
                if(codeEntered(codeFinal)){
                    //verificationId -> El código que s envio
                    //code -> el codigo ingresado
                    showProgress(true);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeFinal);
                    signInWithPhoneAuthCredential(credential);

                }else{
                    return;
                }

            }
        });


        editCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editCode.getWindowToken(), 0);

                    codeFinal = editCode.getText().toString().trim();
                    if(codeEntered(codeFinal)){
                        //verificationId -> El código que s envio
                        //code -> el codigo ingresado
                        showProgress(true);
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeFinal);
                        signInWithPhoneAuthCredential(credential);

                    }
                    return true;
                }
                return false;
            }
        });



        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                verificarCelular();
            }
        });


        timerTask.execute();

        return root;
    }

    public AlertDialog dialogSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Mucho exito")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //verificarCelular();
                            }

                        });

        return builder.create();
    }

    public AlertDialog dialogVerificationFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Error")
                .setMessage("El código es incorrecto.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }

                        });

        return builder.create();
    }

    public boolean codeEntered(String codeFinal){

        if (TextUtils.isEmpty(codeFinal) | !isCodeValid(codeFinal)) {
            //cel.setError(getString(R.string.error_field_required));
            Snackbar snackbar = Snackbar.make(getView(), "Código de verificación no válido.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            snackbar.show();
            return false;
        } else{
            return true;
        }

    }

    private boolean isCodeValid(String number) {

        if(number.length() == 6){
            if(number.contains(".")){
                return false;
            }
            try {
                Float.parseFloat(number);
                return true;
            } catch (NumberFormatException nfe){
                return false;
            }

        }else{
            return false;
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mVerifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVerifyFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVerifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mVerifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Esta da el acceso
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            showProgress(false);

                            if(origin.equals("recoverPasswordActivity")){
                                    //Llamar al activity PasswordActivity
                                    Intent password = new Intent(getActivity(), PasswordActivity.class);
                                    password.putExtra("id", id);
                                    password.putExtra("column", column);
                                    password.putExtra("value", value);
                                    password.putExtra("origin", "recoverPasswordActivity");
                                    startActivity(password);
                            }

                            if (origin.equals("informationActivity")){
                                    actualizarPerfilAutomovilista(id,column,phoneNumber);
                            }


                            //dialogSuccess().show();

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


    public void actualizarPerfilAutomovilista(String id, String column, String value){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("id", id);
        parametros.put("column", column);
        parametros.put("value", value);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_UPDATE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        //Log.d("LoginActivity", response.toString());
                        try {
                            if(response.getString("success").equals("1")){
                                showProgress(false);
                                //Si los datos se actualizaron correctamente, entonces cerramos el activity
                                //y/o cerramos el otro activity y lo reiniciamos o lo volvemos a abrir
                                dialogUpdateSuccess().show();

                            }else{
                                showProgress(false);
                                if(response.getString("success").equals("0")){
                                    dialogUpdateFailed().show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogNoConnection().show();



                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }


    public AlertDialog dialogUpdateFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Error")
                .setMessage("Error al actualizar el perfil. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogUpdateSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Actualización exitosa")
                .setMessage("Se ha modificado el perfil exitosamente.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                infAct.informationActivity.finish();
                                startActivity(new Intent(getActivity(),InformationActivity.class));
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNoUser(int info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = "";
        if(info == 2){
            message = "correo electrónico";
        }
        if(info == 3){
            message = "número celular";
        }

        builder.setTitle("No se pudo crear la cuenta")
                .setMessage("El "+ message + " ya está registrado.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogWelcome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Bienvenido")
                .setMessage("Registro exitoso.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }
    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Error")
                .setMessage("No se puede realizar la conexión con el servidor. Intente de nuevo")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }


    public class TimerTask extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtSecond.setVisibility(View.VISIBLE);
            txtSecond.setText("Espera 60 segundos para reenviar el código.");
            resend.setVisibility(View.INVISIBLE);

            //verificarCelular();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            String msj1= "Espera ";
            String msj2 = " segundos para reenviar el código.";
            String seg = Integer.toString(values[0]);
            txtSecond.setText(msj1 + seg + msj2);

        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {

                for(int i = 59; i >=0; i--){
                    //for(int i = 3; i >=0; i--){

                    Thread.currentThread();
                    Thread.sleep(1000);
                    if(!isCancelled())
                        publishProgress(i);
                    else break;


            }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            txtSecond.setText("");
            txtSecond.setVisibility(View.INVISIBLE);
            resend.setVisibility(View.VISIBLE);
            timerTask = null;
            super.onPostExecute(success);
        }

        @Override
        protected void onCancelled() {
            timerTask = null;
            super.onCancelled();

        }
    }

    public void verificarCelular(){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                //"+525540673724",
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
                        dialogVerificationFailed().show();

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }

                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        reiniciarVerifyCode(verificationId);

                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                }
        );
    }


    public void reiniciarVerifyCode(String code){

        VerifyCodeFragment vcf = new VerifyCodeFragment();
        Bundle arg = new Bundle();
        arg.putString("id", code);
        arg.putString("phone", phoneNumber);
        arg.putString("phoneFormatted", phoneNumberFormatted);
        arg.putString("nombre", nombre);
        arg.putString("apellido", apellido);
        arg.putString("correo", correo);
        arg.putString("password", password);
        arg.putString("origin", origin);
        vcf.setArguments(arg);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nestedScrollForm, vcf)
                .addToBackStack(null)
                .commit();
        session.setVerifying(true);
        showProgress(false);

    }
}
