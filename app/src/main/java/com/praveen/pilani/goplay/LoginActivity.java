package com.praveen.pilani.goplay;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements
		GoogleApiClient.OnConnectionFailedListener{
	private static final String TAG = "LoginActivity";


	private static final int RC_SIGN_IN = 007;

	private GoogleApiClient mGoogleApiClient;

	private SignInButton btnSignIn;
	private EditText inputEmail, inputPassword;
	private FirebaseAuth firebaseAuth;
	private ProgressBar progressBar;
	private Button btnSignup, btnLogin, btnReset;

	private CallbackManager callbackManager;
	private LoginButton btnFacebookLogin;
	private ProfileTracker profileTracker;
	private AccessTokenTracker accessTokenTracker;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_login);


		initWidgets();
		//Get Firebase auth instance
		firebaseAuth = FirebaseAuth.getInstance();

		callbackManager = CallbackManager.Factory.create();
		accessTokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken currentToken) {

			}
		};

		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
				nextActivity(newProfile);

			}
		};

		accessTokenTracker.startTracking();
		profileTracker.startTracking();

		FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Profile profile = Profile.getCurrentProfile();


				nextActivity(profile);

				Toast.makeText(getApplicationContext(),"login in..",Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onCancel() {

			}

			@Override
			public void onError(FacebookException error) {

			}
		};

		btnFacebookLogin.setReadPermissions("user_friends");
		btnFacebookLogin.registerCallback(callbackManager,callback);


//		if (firebaseAuth.getCurrentUser() != null) {
//			startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
//			finish();
//		}





		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, SignupActivity.class));
			}
		});

		btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetPassword();
				//startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
			}
		});

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = inputEmail.getText().toString();
				final String password = inputPassword.getText().toString();

				if (TextUtils.isEmpty(email)) {
					Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
					return;
				}

				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
					return;
				}

				progressBar.setVisibility(View.VISIBLE);

				//authenticate user
				firebaseAuth.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
							@Override
							public void onComplete(@NonNull Task<AuthResult> task) {
								// If sign in fails, display a message to the user. If sign in succeeds
								// the auth state listener will be notified and logic to handle the
								// signed in user can be handled in the listener.
								progressBar.setVisibility(View.GONE);
								if (!task.isSuccessful()) {
									// there was an error
									if (password.length() < 6) {
										inputPassword.setError(getString(R.string.minimum_password));
									} else {
										Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
									}
								} else {
									changetoActivity();
									finish();
								}
							}
						});
			}
		});


		//google signup button click listner
		btnSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
				startActivityForResult(signInIntent, RC_SIGN_IN);
			}
		});




	}

	// below code is for to control activity life cycle or instance

//	@Override
//	protected void onResume(){
//		super.onResume();
//		Profile profile = Profile.getCurrentProfile();
//		nextActivity(profile);
//	}
//
//	@Override
//	protected void onPause(){
//
//		super.onPause();
//	}
//
//	protected void onStop(){
//		super.onStop();
//		accessTokenTracker.stopTracking();
//		profileTracker.stopTracking();
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}

	private void handleSignInResult(GoogleSignInResult result) {
		Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.

			changetoActivity();
		} else {
			// Signed out, show unauthenticated UI.

		}
	}


	private  void nextActivity(Profile profile){

		if(profile !=null){
			Intent intent = new Intent(LoginActivity.this,MainActivity.class);
			intent.putExtra("name",profile.getFirstName());
			intent.putExtra("surname",profile.getLastName());
			intent.putExtra("imageUrl",profile.getProfilePictureUri(155,150).toString());
			startActivity(intent);

		}

	}

	@Override
	protected void onStart(){
		super.onStart();
		FirebaseUser currentUser = firebaseAuth.getCurrentUser();
		if(currentUser != null){

		}

	}


//on forget password
	private void resetPassword() {
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.activity_reset_password, null);
		dialogBuilder.setView(dialogView);

		final EditText editEmail = (EditText) dialogView.findViewById(R.id.email);
		final Button btnReset = (Button) dialogView.findViewById(R.id.btn_reset_password);
		final ProgressBar progressBar1 = (ProgressBar) dialogView.findViewById(R.id.progressBar);

		//dialogBuilder.setTitle("Send Photos");
		final AlertDialog dialog = dialogBuilder.create();

		btnReset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String email = editEmail.getText().toString().trim();

				if (TextUtils.isEmpty(email)) {
					Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
					return;
				}

				progressBar1.setVisibility(View.VISIBLE);
				firebaseAuth.sendPasswordResetEmail(email)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
									Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
								}

								progressBar1.setVisibility(View.GONE);
								dialog.dismiss();
							}
						});

			}
		});
		dialog.show();
	}

	//move to the next activity after successful login/register
	private void changetoActivity() {
		Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
		startActivity(intent);

	}

	private void initWidgets() {

		//facebook login button
		btnFacebookLogin = (LoginButton) findViewById(R.id.btnFacebookLogin);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.password);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		btnSignup = (Button) findViewById(R.id.btn_signup);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnReset = (Button) findViewById(R.id.btn_reset_password);
		btnSignIn = (SignInButton) findViewById(R.id.sign_in_button1);

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		// Customizing G+ button
		btnSignIn.setSize(SignInButton.SIZE_STANDARD);
		btnSignIn.setScopes(gso.getScopeArray());


	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not
		// be available.
		Log.d(TAG, "onConnectionFailed:" + connectionResult);
	}
}
