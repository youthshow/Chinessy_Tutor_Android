package com.chinessy.tutor.android.rtmp.utils;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.chinessy.tutor.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogUtils {
	
	public static interface InputConfigClickListener{
		public void onResult(DialogInterface dialog, HashMap<String, String> result);
	}
	
	/**
	 * 
	 * @param context
	 * @param keys
	 * @param defaultValues
	 * @param promptStrings
	 * @param configResultListhener
	 * @return 
	 */
	public static Dialog showConfigInputDialog(final Context context,
			final String[] keys, String[] defaultValues,String[] promptStrings,final InputConfigClickListener configResultListhener) {
		if(keys == null || keys.length == 0 ) return null;
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout container = new LinearLayout(context);
		int paddingPix = dpToPx(24,context.getResources());
		int editTextHeight = dpToPx(12,context.getResources());
		container.setPadding(paddingPix,paddingPix,paddingPix,paddingPix);
		container.setOrientation(LinearLayout.VERTICAL);
		
		final List<EditText> evList = new ArrayList<EditText>(keys.length);
		
		for(int i = 0; i < keys.length; i++){
			LinearLayout item = (LinearLayout)inflater.inflate(R.layout.eidt_dialog_item, null);
			String textString = keys[i];
			if(promptStrings != null && i < promptStrings.length){
				textString = promptStrings[i];
			}
			TextView textView = (TextView) item.findViewById(R.id.text_view);
			EditText editText = (EditText) item.findViewById(R.id.edit_text);
			textView.setText(textString);
			if(defaultValues != null && i < defaultValues.length){
				editText.setText(defaultValues[i]);
				editText.setSelection(defaultValues[i].length());
			}
			
			//add view
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			container.addView(item, lp);
			
			//save view
			evList.add(editText);
		}

		ScrollView scrollView = new ScrollView(context);
		scrollView.addView(container, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		OnClickListener onClick = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case AlertDialog.BUTTON_POSITIVE:
					HashMap<String,String> result = new HashMap<String, String>();
					for(int i = 0; i < evList.size(); i++){
						result.put(keys[i], evList.get(i).getText().toString());
					}
					configResultListhener.onResult(dialog,result);
				case AlertDialog.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				}

			}
		};

		builder.setView(scrollView);
		builder.setPositiveButton(android.R.string.ok, onClick);
		builder.setNegativeButton(android.R.string.cancel, onClick);

		Dialog dialog = builder.create();
		dialog.show();
		return dialog;
	}
	
	public static AlertDialog showRtmpUrlInputDialog(final Context context,OnClickListener onClickListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout container = new LinearLayout(context);
		int paddingPix = dpToPx(24,context.getResources());
		container.setPadding(paddingPix,paddingPix,paddingPix,paddingPix);
		container.setOrientation(LinearLayout.VERTICAL);

		EditText editText = new EditText(context);
		editText.setId(android.R.id.edit);
		editText.setMaxLines(3);
		editText.setMinLines(1);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		editText.setFocusable(false);
		editText.setFocusableInTouchMode(true);

		//add view
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		container.addView(editText, lp);

		//save view
		builder.setView(container);
		builder.setPositiveButton(android.R.string.ok, onClickListener);
		builder.setNegativeButton(android.R.string.cancel, onClickListener);

		builder.setTitle("请输入有效的推流地址");

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static Dialog showAlertDialog(Context context, String message){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(android.R.string.dialog_alert_title);
    	builder.setMessage(message);
    	builder.setPositiveButton(android.R.string.ok, null);
    	Dialog dialog = builder.create();
		dialog.show();
		return dialog;
    }

    public static ProgressDialog showSimpleProgressDialog(Context context,String message, DialogInterface.OnDismissListener dismissListener){
    	String cancleStr = context.getResources().getString(android.R.string.cancel);
    	ProgressDialog dialog = new ProgressDialog(context);
    	dialog.setCancelable(false);
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.setTitle(android.R.string.dialog_alert_title);
    	dialog.setMessage(message);
    	dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, cancleStr, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
    	dialog.setOnDismissListener(dismissListener);
    	dialog.show();
    	return dialog;
    }

    public static Dialog showSingleChoiceDialog(Context context, String title, String[] singleChoiceItems, int checkedItem, final OnClickListener onClickListener){
        if(context == null) return null;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                       .setSingleChoiceItems(singleChoiceItems, checkedItem, new OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface alertDialog, int which) {
                                                if(onClickListener != null){
                                                    onClickListener.onClick(alertDialog, which);
                                                    alertDialog.dismiss();
                                                }

                                            }
                                       })
                                       .setTitle(title)
                                       .setCancelable(true)
                                       .create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showSingleInputNumberDialog(final Context context,String title,String prompt, String defaultText,OnClickListener onClickListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout container = new LinearLayout(context);
		int paddingPix = dpToPx(24,context.getResources());
		container.setPadding(paddingPix,paddingPix,paddingPix,paddingPix);
		container.setOrientation(LinearLayout.VERTICAL);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout item = (LinearLayout)inflater.inflate(R.layout.eidt_dialog_item, null);
		TextView textView = (TextView) item.findViewById(R.id.text_view);
		textView.setText(prompt);
		EditText editText = (EditText) item.findViewById(R.id.edit_text);
		editText.setFocusable(false);
		editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		editText.setWidth(dpToPx(50,context.getResources()));
		editText.setFocusableInTouchMode(true);
		editText.setText(defaultText);

		//add view
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		container.addView(item, lp);

		//save view
		builder.setView(container);
		builder.setPositiveButton(android.R.string.ok, onClickListener);
		builder.setNegativeButton(android.R.string.cancel, onClickListener);

		builder.setTitle(title);

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

public static AlertDialog showSingleInputTextDialog(final Context context, String title, String prompt, String defaultText,OnClickListener onClickListener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout container = new LinearLayout(context);
		int paddingPix = dpToPx(24,context.getResources());
		container.setPadding(paddingPix,paddingPix,paddingPix,paddingPix);
		container.setOrientation(LinearLayout.VERTICAL);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout item = (LinearLayout)inflater.inflate(R.layout.eidt_dialog_item, null);
		TextView textView = (TextView) item.findViewById(R.id.text_view);
		textView.setText(prompt);
		EditText editText = (EditText) item.findViewById(R.id.edit_text);
		editText.setFocusable(false);
		editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		editText.setWidth(dpToPx(50,context.getResources()));
		editText.setFocusableInTouchMode(true);
		editText.setText(defaultText);
		
		//add view
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		container.addView(item, lp);
		
		//save view
		builder.setView(container);
		builder.setPositiveButton(android.R.string.ok, onClickListener);
		builder.setNegativeButton(android.R.string.cancel, onClickListener);

		builder.setTitle(title);
		
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}
}
