package com.david.teamviewproject;

import java.util.ArrayList;

import android.app.Activity;

import android.os.Bundle;

import android.widget.Toast;

import com.david.teamviewproject.TeamView.OnItemClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {




		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TeamView tv = (TeamView) this.findViewById(R.id.teamView);

		DataAdapter da = new DataAdapter(this, new ArrayList<TeamData>());
		da.initFewDatas();
		tv.setAdapter(da);
		tv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(int position, Object data) {

				Toast.makeText(MainActivity.this, "data:" + data + ",position:" + position, Toast.LENGTH_SHORT).show();

			}
		});


        // f4;
	}
}
