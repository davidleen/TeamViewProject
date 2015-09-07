package com.david.teamviewproject;

import java.util.List;
import java.util.Observable;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class DataAdapter extends Observable {

	private List<com.david.teamviewproject.TeamData> lists;
	private Context context;

	public DataAdapter(Context context, List<TeamData> lists) {
		this.context = context;
		this.lists = lists;

	}

	public List<TeamData> getData() {

		return lists;
	}

	public void initFewDatas() {
		Random random = new Random();

		Bitmap defaultBitmap = getDefalutUserHead();
		lists.clear();

		for (int i = 0, count = 20; i < count; i++) {

			final TeamData newData = new TeamData();
			newData.head = defaultBitmap;
			newData.relateTaskNum = random.nextInt(10);
			newData.taskNum = random.nextInt(30);
			newData.isViewer = random.nextBoolean();
			newData.url = "";
			newData.position = i;
			newData.data = newData.toString();
			// Runnable runnable = new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// newData.head = getDefalutUserHead();
			// setChanged();
			// notifyObservers();
			// }
			// };
			// new Thread(runnable).start();
			lists.add(newData);
		}
		setChanged();

		this.notifyObservers();
	}

	public Bitmap getDefalutUserHead() {

		return ((BitmapDrawable) context.getResources().getDrawable(R.drawable.icon_boy)).getBitmap();
	}

}
