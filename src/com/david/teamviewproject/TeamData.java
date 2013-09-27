package com.david.teamviewproject;

import android.graphics.Bitmap;

public class TeamData {

	public Bitmap head;
	public int relateTaskNum;
	public int taskNum;
	public int taskId;
	public boolean isViewer;
	public Object data;
	public int position;
	public String url;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TeamData [relateTaskNum=").append(relateTaskNum).append(", taskNum=").append(taskNum).append(", taskId=").append(taskId)
				.append(", isViewer=").append(isViewer).append(", data=").append(data).append(", position=").append(position).append(", url=").append(url)
				.append("]");
		return builder.toString();
	}

}
