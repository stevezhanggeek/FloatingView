package fr.anthonyfernandez.floatingmenu.Service;

import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.view.DragEvent;
import fr.anthonyfernandez.floatingmenu.R;


public class ServiceFloating extends Service {

	private WindowManager windowManager;
	private ImageView chatHead;
	private WindowManager.LayoutParams params;
	private ListPopupWindow popup;

	long lastPressTime;
	private Boolean windowOpened_ = false;
	private Boolean startDrag_ = false;
    private Boolean isMoving_ = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public void onCreate() {
		super.onCreate();
		
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);
		
		chatHead.setImageResource(R.drawable.floating2);

		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 0;

		windowManager.addView(chatHead, params);

		chatHead.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
                        if (isMoving_) {
                            startDrag_ = false;
                            isMoving_ = false;
                            WindowManager.LayoutParams position = params;
                            position.x = 0;
                            position.y = (int)event.getRawY() - 100;
                            windowManager.updateViewLayout(chatHead, position);
                        } else {
                            if (!windowOpened_) {
                                initiatePopupWindow(chatHead);
                                windowOpened_ = true;
                            } else {
                                popup.dismiss();
                                windowOpened_ = false;
                            }
                        }
						break;
					case MotionEvent.ACTION_MOVE:
						if (startDrag_) {
                            //ServiceFloating.this.stopSelf();
							WindowManager.LayoutParams position = params;
							position.x = (int)event.getRawX() - 100;
							position.y = (int)event.getRawY() - 100;
							windowManager.updateViewLayout(chatHead, position);
                            isMoving_ = true;
						} else {
							initiatePopupWindow(chatHead);
						}
						break;
				}
				return false;
			}
		});

		chatHead.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				startDrag_ = true;
				return true;
			}
		});
	}

	private void initiatePopupWindow(View anchor) {
		try {
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			popup = new ListPopupWindow(this);
			popup.setAnchorView(anchor);
			popup.setWidth((int) (display.getWidth() / (1.5)));
			popup.setHeight((int) (display.getWidth()/(1.5)));
			popup.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
	}

}