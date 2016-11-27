package com.chinessy.tutor.android.rtmp.object;

public class SettingItem {
	
	public final int settingId;
	public final String settingName;
	public final int iconResId;
	public final boolean dismissAfterClick;

	public SettingItem(int navigationId, String navigationName, int iconResId, boolean dismissAfterClick)
	{
		this.settingId = navigationId;
		this.settingName = navigationName;
		this.iconResId = iconResId;
		this.dismissAfterClick = dismissAfterClick;
	}
	
	
	@Override
	public String toString() {
		return settingName;
	}
}
