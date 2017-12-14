package com.example.webtextselect;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;

public class CustomWebView extends WebView {

	private Context context;
	// override all other constructor to avoid crash
	// setting custom action bar
	private ActionMode mActionMode;
	private ActionMode.Callback mSelectActionModeCallback;
	private GestureDetector mDetector;

	public CustomWebView(Context context) {
		this(context, null, 0);
	}

	public CustomWebView(Context context, AttributeSet attri) {
		this(context, attri, 0);
	}

	public CustomWebView(Context context, AttributeSet attri, int style) {
		super(context, attri, style);
		this.context = context;
		WebSettings webviewSettings = getSettings();
		webviewSettings.setJavaScriptEnabled(true);
		// add JavaScript interface for copy
		addJavascriptInterface(new WebAppInterface(context), "JSInterface");
	}

	@Override
	public ActionMode startActionMode(Callback callback) {
		ViewParent parent = getParent();
		if (parent == null) {
			return null;
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			String name = callback.getClass().toString();
			if (name.contains("SelectActionModeCallback")) {
				mSelectActionModeCallback = callback;
				mDetector = new GestureDetector(context, new CustomGestureListener());
			}
		}
		CustomActionModeCallback mActionModeCallback = new CustomActionModeCallback();
		return parent.startActionModeForChild(this, mActionModeCallback);
	}

	private class CustomActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mActionMode = mode;
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.copy, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			Log.e("actionmod", mode.getTitle() + "sss");
			
		
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			switch (item.getItemId()) {
			case R.id.copy:
				getSelectedData();

				return true;
			case R.id.share:

				return true;
			default:

				if (mode != null) {
					mode.finish();
				}
				return false;
			}

		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				clearFocus();
			} else {
				if (mSelectActionModeCallback != null) {
					mSelectActionModeCallback.onDestroyActionMode(mode);
				}
				// 2016 edit by sight
				// 这里 置空 会报 ActionModeImpl.invalidate(ActionBarImpl.java:1012) 空异常
				//mActionMode = null;
			}
		}
	}

	private void getSelectedData() {

		String js = "(function getSelectedText() {" + "var txt;" + "if (window.getSelection) {" + "txt = window.getSelection().toString();" + "} else if (window.document.getSelection) {"
				+ "txt = window.document.getSelection().toString();" + "} else if (window.document.selection) {" + "txt = window.document.selection.createRange().text;" + "}"
				+ "JSInterface.getText(txt);" + "})()";
		// calling the js function
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			evaluateJavascript("javascript:" + js, null);
		} else {
			loadUrl("javascript:" + js);
		}
	}

	private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (mActionMode != null) {
				//update  by sight 2016.5.12
				//mActionMode.finish();
				return true;
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);

			Log.e("SSAASAD", e.getAction() + "");
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Send the event to our gesture detector
		// If it is implemented, there will be a return value
		if (mDetector != null)
			mDetector.onTouchEvent(event);
		// If the detected gesture is unimplemented, send it to the superclass
		return super.onTouchEvent(event);
	}
}
