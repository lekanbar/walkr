package com.contct.myip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.org.utilities.ActionItem;
import com.org.utilities.QuickAction;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class StatusActivity extends Activity {
	
	private QuickAction quickAction;
	
	final int DO_CAMERA = 0;
	final int DO_VIDEO = 1;
	final int DO_GALLERY = 2;
	
	String mCurrentPhotoPath, mCurrentVideoPath;
	Uri mCapturedImageURI;
	Uri mCapturedVideoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		
		InitializeUI();
	}
	
	private void InitializeUI(){
		//Quick action things
        Configuration conf = this.getResources().getConfiguration();
    	if(conf.orientation == 2)
    		quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
    	else
    		quickAction = new QuickAction(this, QuickAction.VERTICAL);
        
        ActionItem countriesItem = new ActionItem(0, getString(R.string.camera), getResources().getDrawable(R.drawable.camera_selector));
        ActionItem currencyItem = new ActionItem(1, getString(R.string.video), getResources().getDrawable(R.drawable.video_selector));
        ActionItem emailItem = new ActionItem(2, getString(R.string.gallery), getResources().getDrawable(R.drawable.gallery_selector));
        
        quickAction.addActionItem(countriesItem);
        //quickAction.addActionItem(langItem);
        quickAction.addActionItem(currencyItem);
        quickAction.addActionItem(emailItem);
        
        //Set listener for action item clicked
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                //here we can filter which action item was clicked with pos or actionId parameter
            	quickAction.dismiss();
            	
                if(actionId == 0){
                	doCamera();
                }
                else if(actionId == 1){
                	doVideo();
                }
                else{
                	Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*, video/*");
                    startActivityForResult(galleryIntent, DO_GALLERY);
                }
            }
        });
        
        //show on imgOptions
        ImageView imgOptions = (ImageView)findViewById(R.id.imgOptions);
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAction.show(v);
            }
        });
        
        //show on imgSend
        ImageView imgSend = (ImageView)findViewById(R.id.imgSend);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
	}
	
	private void doCamera(){
		if(StatusActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){		    
	        String imageFileName = "msAx" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
	        
		    ContentValues values = new ContentValues();
		    values.put(MediaStore.Images.Media.TITLE, imageFileName);
		    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		    
		    Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		    startActivityForResult(intentPicture, DO_CAMERA);
		}
		else{
			AlertDialog.Builder alert = new AlertDialog.Builder(StatusActivity.this);
			alert.setTitle(getString(R.string.attach));
			alert.setMessage(R.string.attachMessage);
			alert.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface arg0, int arg1) {
					StatusActivity.this.setResult(RESULT_CANCELED);
					finish();
				}
			});
			alert.show();
		}
	}
	
	private void doVideo(){
		if(StatusActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
    		final PackageManager packageManager = StatusActivity.this.getPackageManager();
    	    final Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    	    List<ResolveInfo> list = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
    	    
    	    if(list.size() > 0){
    	    	startActivityForResult(i, DO_VIDEO);
    	    }
    	    else{
    	    	StatusActivity.this.setResult(RESULT_CANCELED);
				finish();
    	    }
		}
		else{
			AlertDialog.Builder alert = new AlertDialog.Builder(StatusActivity.this);
			alert.setTitle(getString(R.string.attach));
			alert.setMessage(getString(R.string.attachMessage));
			alert.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface arg0, int arg1) {
					StatusActivity.this.setResult(RESULT_CANCELED);
					finish();
				}
			});
			alert.show();
		}
	}
	
	@Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
	    super.onActivityResult(reqCode, resCode, data);
	    switch(reqCode) {
		    case (DO_GALLERY) : {
			    if (resCode == Activity.RESULT_OK) {
			    	Uri selectedImage = data.getData();
		            String[] filePathColumn = { MediaStore.Images.Media.DATA };
		 
		            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();
		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            String filePath = cursor.getString(columnIndex);
		            cursor.close();
		            
		            EditText txtStatusMessage = (EditText)findViewById(R.id.txtStatusMessage);
		            txtStatusMessage.setText(filePath);
			    }
			    else{
			    	AlertDialog.Builder alert = new AlertDialog.Builder(StatusActivity.this);
        			alert.setTitle(getString(R.string.attach));
        			alert.setMessage(getString(R.string.errorMessage));
        			alert.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

    					public void onClick(DialogInterface arg0, int arg1) {
    						StatusActivity.this.setResult(RESULT_CANCELED);
    						finish();
    					}
        			});
        			alert.show();
			    }
			    break;
		    }
		    case (DO_CAMERA) : {
			    if (resCode == Activity.RESULT_OK) {
			        EditText txtStatusMessage = (EditText)findViewById(R.id.txtStatusMessage);
		            txtStatusMessage.setText(getRealPathFromURI(mCapturedImageURI));
			    }
			    else{
			    	AlertDialog.Builder alert = new AlertDialog.Builder(StatusActivity.this);
        			alert.setTitle(getString(R.string.attach));
        			alert.setMessage(getString(R.string.errorMessage));
        			alert.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

    					public void onClick(DialogInterface arg0, int arg1) {
    						StatusActivity.this.setResult(RESULT_CANCELED);
    						finish();
    					}
        			});
        			alert.show();
			    }
			    break;
		    }
		    case (DO_VIDEO) : {
		    	if (resCode == Activity.RESULT_OK) {
		    		mCapturedVideoUri = data.getData();			    	
			    	EditText txtStatusMessage = (EditText)findViewById(R.id.txtStatusMessage);
		            txtStatusMessage.setText(getRealPathFromURI(mCapturedVideoUri));
			    }
			    else{
			    	AlertDialog.Builder alert = new AlertDialog.Builder(StatusActivity.this);
        			alert.setTitle(getString(R.string.attach));
        			alert.setMessage(getString(R.string.errorMessage));
        			alert.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

    					public void onClick(DialogInterface arg0, int arg1) {
    						StatusActivity.this.setResult(RESULT_CANCELED);
    						finish();
    					}
        			});
        			alert.show();
			    }
			    break;
		    }
	    }
    }
	
	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
