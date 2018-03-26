package com.praveen.pilani.goplay;



import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
		View.OnClickListener,AdapterView.OnItemSelectedListener {
	private static final String TAG = "MainActivity";

	private static final int PERMISSION_REQUEST_CODE = 1;
	private ShareDialog shareDialog;
	private Button btnshare;
	private EditText ganderet,phoneet;
	private String name,gander,dob,sports,phone;
	private ImageView UserImage;
	private Bitmap imga;
	private ImageView datePicker;
	private int mYear,mMonth,mDay;
	private FloatingActionButton logout;
	private TextView nameView,dobet,sportset;
	private Spinner sportsPicker;
	private RadioGroup radioGroup;
	SharedPreferences preferences;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String ID_NUMBER = "nameKey";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(this);
		setContentView(R.layout.activity_main);

		initWidgets();


		shareDialog = new ShareDialog(this);

		Bundle inBundle = getIntent().getExtras();
		String name1 = inBundle.getString("name");
		String surnmae = inBundle.getString("surname");
		String imageUrl = inBundle.getString("imageUrl");

		// user name from facebook
		nameView.setText(""+name1+" "+surnmae);
		name = name1+" "+surnmae;

		//radio button click listner
		radioGroup.clearCheck();
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton) group.findViewById(checkedId);
				if (null != rb && checkedId > -1) {

					gander=rb.getText().toString();
					Toast.makeText(MainActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
				}
			}
		});


	//date picker click listner
		datePicker.setOnClickListener(this);


		new MainActivity.DownloadImage((CircleImageView)findViewById(R.id.profileImage)).execute(imageUrl);

		logout.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				LoginManager.getInstance().logOut();
				Intent login = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(login);
				finish();
			}
		});

//sports spinner clcick listner
		sportsPicker.setOnItemSelectedListener(this);

		// Spinner Drop down elements
		List<String> categories = new ArrayList<String>();
		categories.add("Badminton");
		categories.add("Basketball");
		categories.add("Boxing");
		categories.add("Cricket");
		categories.add("Cycling");
		categories.add("Football");
		categories.add("Formula One");
		categories.add("Golf");
		categories.add("Gymnastics");
		categories.add("Handball");
		categories.add("Hockey");
		categories.add("Kabaddi");
		categories.add("Kho Kho");
		categories.add("Lawn Tennis");
		categories.add("Swimming");
		categories.add("Table tennis");
		categories.add("Throwball");
		categories.add(" Volleyball");


		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		sportsPicker.setAdapter(dataAdapter);

	}

	// spinner adapter item click listner

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// On selecting a spinner item
		sports= parent.getItemAtPosition(position).toString();

		// Showing selected spinner item
		Toast.makeText(parent.getContext(), "Selected: " + sports, Toast.LENGTH_LONG).show();
	}
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}



	public class  DownloadImage extends AsyncTask<String,Void, Bitmap>{

		ImageView bmImage;

		public DownloadImage(ImageView bmImage){
			this.bmImage = bmImage;
		}



		protected Bitmap doInBackground(String... urls){
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try{
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			}catch (Exception e){
//				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result){
			bmImage.setImageBitmap(result);

			imga = result;
		}

	}



	// to merge both image
	public void buttonMerge(View view) {

		int id = 34567889;

		preferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(ID_NUMBER,id);
		editor.commit();

		int Sid = preferences.getInt(ID_NUMBER,0);

		String StringId = String.valueOf(Sid);
		phone = phoneet.getText().toString();

		id++;

		if(gander.isEmpty()||dob.isEmpty()||sports.isEmpty()||phone.isEmpty()){

			Toast.makeText(MainActivity.this,"Please fill All Data",Toast.LENGTH_SHORT).show();

		}else {

			Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.drawable.id_carg_bg);
			Bitmap smallImage = imga;

			Log.d(TAG, "dataholder image--" + smallImage.toString());

			Bitmap mergedImages = createSingleImageFromMultipleImages(bigImage, smallImage);

			String nam = ( name + "\n" + "DOB - " + dob + "\n" + "Gander - " + gander + "\n" + "Sport - " + sports + "\n" + "Phone - " + phone+"\n"+"Player Id - "+StringId);
			Log.d(TAG, "user data--" + nam);

			final Bitmap img1 = drawText(nam, mergedImages);
			UserImage.setImageBitmap(img1);

			btnshare.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Build.VERSION.SDK_INT >= 23) {
						if (checkPermission()) {
							Log.e("value", "Permission already Granted, Now you can save image.");
						} else {
							requestPermission();
						}
					} else {
						Log.e("value", "Not required for requesting runtime permission");
					}


//saving the image in local storage

					String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), img1, "title", null);

					Uri contentUri = Uri.parse(pathofBmp);
					Log.d(TAG, "image uri--" + contentUri);


					if (contentUri != null) {
						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
						shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
						shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
						shareIntent.setType("image/png");
						startActivity(Intent.createChooser(shareIntent, "Choose an app"));
					}
				}
			});
		}

	}

	//merge two images

	private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage){

		Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(firstImage, 0f, 0f, null);
		canvas.drawBitmap(secondImage, 220, 297, null);
		return result;
	}

	//draw text over bitmap

	public Bitmap drawText(String text, Bitmap bitmap) {

		float scale  = getResources().getDisplayMetrics().density;

		Log.d(TAG,"text---"+text);
		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
		// set default bitmap config if none
		if(bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		bitmap = bitmap.copy(bitmapConfig, true);

		Canvas canvas = new Canvas(bitmap);

		// new antialiased Paint
		TextPaint paint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(00, 00, 00));
		// text size in pixels
		paint.setTextSize((int) (15 * scale));
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// set text width to canvas width minus 16dp padding
		int textWidth = canvas.getWidth() - (int) (40 * scale);

		// init StaticLayout for text
		StaticLayout textLayout = new StaticLayout(
				text, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

		// get height of multiline text
		int textHeight = textLayout.getHeight();

		// get position of text's top left corner
		float x = (bitmap.getWidth() - textWidth)/2;
		float y = (float) ((bitmap.getHeight() - textHeight)/1.5);

		// draw text to the Canvas center
		canvas.save();
		canvas.translate(x, y);
		textLayout.draw(canvas);
		canvas.restore();

		return bitmap;

	}

//check for permission
	private boolean checkPermission() {
		int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (result == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			return false;
		}
	}

	//request for to write and read storage
	private void requestPermission() {

		if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
		} else {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.e("value", "Permission Granted, Now you can save image .");
				} else {
					Log.e("value", "Permission Denied, You cannot save image.");
				}
				break;
		}
	}

	//Click listner

	@Override
	public void onClick(View v) {


		//Date picker click listner initilization
		if (v == datePicker){

			// Get Current Date
			final Calendar c;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
				c = Calendar.getInstance();

			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			}

			DatePickerDialog datePickerDialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
						                      int monthOfYear, int dayOfMonth) {

							dobet.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

							dob = (dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

						}
					}, mYear, mMonth, mDay);
			datePickerDialog.show();
		}

		//sports spinner click listner initilaztion


	}






	//UI element Initilaztion from Activity
	public void initWidgets(){

		UserImage =(ImageView) findViewById(R.id.idimg);
		logout = (FloatingActionButton) findViewById(R.id.logout);
		nameView = (TextView) findViewById(R.id.nameAndSurname);
		dobet = (TextView) findViewById(R.id.dobet);
		phoneet = (EditText) findViewById(R.id.phoneet);
		btnshare = (Button) findViewById(R.id.btnshare);
		datePicker = (ImageView) findViewById(R.id.datePicker);
		sportsPicker = (Spinner) findViewById(R.id.sportsPicker);
		radioGroup = (RadioGroup) findViewById(R.id.radioGrp);



	}

}
