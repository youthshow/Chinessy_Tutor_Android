package com.chinessy.tutor.android.rtmp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";
	protected String[] PERMISSIONS = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
	protected String[] PERMISSION_TOAST_STRING = new String[] { "存储", "相机", "麦克风" };

	protected boolean checkAndRequestPermission() {

		ArrayList<String> lackedPermissions = new ArrayList<String>(PERMISSIONS.length);
		for (int i = 0; i < PERMISSIONS.length; i++) {
			int result = PermissionChecker.checkCallingOrSelfPermission(this, PERMISSIONS[i]);
			if (result != PackageManager.PERMISSION_GRANTED) {
				lackedPermissions.add(PERMISSIONS[i]);
			}
		}

		if (lackedPermissions.size() > 0) {
			String[] rP = new String[lackedPermissions.size()];
			lackedPermissions.toArray(rP);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M - 1) {
				requestPermissions(rP, 0);
				Log.i(TAG, "requestPermissions " + lackedPermissions.toString() + " !");
			} else {
				int[] grantResults = new int[rP.length];
				for (int i = 0; i < grantResults.length; i++) {
					grantResults[i] = -1;
				}
				onRequestPermissionsResult(0, rP, grantResults);
				Log.i(TAG, "the platform versin below 23 M , cann't request permissions  !");
			}
			return false;
		}
		Log.i(TAG, "checkPermission success , All permission has granted !");
		return true;
	}
	
	protected List<String> checkPermissions() {

		ArrayList<String> lackedPermissions = new ArrayList<String>(PERMISSIONS.length);
		for (int i = 0; i < PERMISSIONS.length; i++) {
			int result = PermissionChecker.checkCallingOrSelfPermission(this, PERMISSIONS[i]);
			if (result != PackageManager.PERMISSION_GRANTED) {
				lackedPermissions.add(PERMISSIONS[i]);
			}
		}
		return lackedPermissions;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Log.i(TAG, "permissions :" + Arrays.toString(permissions));
		Log.i(TAG, "grantResults :" + Arrays.toString(grantResults));
		ArrayList<String> lackedPermissions = new ArrayList<String>(PERMISSIONS.length);
		for (int i = 0; i < permissions.length; i++) {
			if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
				lackedPermissions.add(permissions[i]);
			}
		}
		showRequestPermissinDialog(lackedPermissions);
	}

	protected void showRequestPermissinDialog(List<String> mLackedPermissions) {
		if (mLackedPermissions == null || mLackedPermissions.size() == 0)
			return;
		StringBuilder stringBuilder = new StringBuilder();
		for (String permission : mLackedPermissions) {
			for (int i = 0; i < PERMISSIONS.length; i++) {
				if (PERMISSIONS[i].equals(permission)) {
					stringBuilder.append(PERMISSION_TOAST_STRING[i]).append(",");
				}
			}
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);

		Builder builder = new Builder(this);
		builder.setMessage("应用需要如下权限： " + stringBuilder.toString() + "，请从“设置”中打开相应权限。");
		builder.setTitle(android.R.string.dialog_alert_title);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing

				// Intent intent = new Intent();
				// intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				// intent.setData(Uri.fromParts("package", getPackageName(),
				// null));
				// startActivity(intent);

			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}

}
