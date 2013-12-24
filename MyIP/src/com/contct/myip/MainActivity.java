package com.contct.myip;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnGestureListener {
	
	private GestureDetector gDetector;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gDetector = new GestureDetector(this);
		
		TabHost tabHost = getTabHost();
		
		// Tab for Status
        TabSpec statusspec = tabHost.newTabSpec("Status");
        // setting Title and Icon for the Tab
        statusspec.setIndicator("", getResources().getDrawable(R.drawable.status_selector));
        Intent statusIntent = new Intent(this, StatusActivity.class);
        statusspec.setContent(statusIntent);
         
        // Tab for Posts
        TabSpec postspec = tabHost.newTabSpec("Posts");        
        postspec.setIndicator("", getResources().getDrawable(R.drawable.posts_selector));
        Intent postsIntent = new Intent(this, PostsActivity.class);
        postspec.setContent(postsIntent);
         
        // Tab for Circles
        TabSpec circlespec = tabHost.newTabSpec("Circles");
        circlespec.setIndicator("", getResources().getDrawable(R.drawable.circles_selector));
        Intent circlesIntent = new Intent(this, CirclesActivity.class);
        circlespec.setContent(circlesIntent);
        
        // Tab for Requests
        TabSpec requestspec = tabHost.newTabSpec("Requests");
        requestspec.setIndicator("", getResources().getDrawable(R.drawable.requests_selector));
        Intent requestsIntent = new Intent(this, RequestsActivity.class);
        requestspec.setContent(requestsIntent);
         
        // Adding all TabSpec to TabHost
        tabHost.addTab(statusspec); // Adding status tab
        tabHost.addTab(postspec);
        tabHost.addTab(circlespec);
        tabHost.addTab(requestspec);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent me) {
        return gDetector.onTouchEvent(me);
    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY) {	
		
		if (Math.abs(start.getY() - finish.getY()) > SWIPE_MAX_OFF_PATH) {  return false;  }

        /* positive value means right to left direction */
        final float distance = start.getX() - finish.getX();
        final boolean enoughSpeed = Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY;
		
		if (distance > SWIPE_MIN_DISTANCE && enoughSpeed) {//start.getRawX() < finish.getRawX()
			//Right to Left
			if (getTabHost().getCurrentTab() < 3) {
				getTabHost().setCurrentTab(getTabHost().getCurrentTab() + 1);
			}
			return true;
		} else if(distance < -SWIPE_MIN_DISTANCE && enoughSpeed){
			//Left to Right
			if (getTabHost().getCurrentTab() > 0) {
				getTabHost().setCurrentTab(getTabHost().getCurrentTab() - 1);
			}
			return true;
		}
		else
			return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
