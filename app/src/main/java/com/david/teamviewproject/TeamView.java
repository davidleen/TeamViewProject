package com.david.teamviewproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class TeamView extends View {

	// 最大显示相关员工数量
	private static final int MAX_PLANET = 10;

	private static final String TAG = "TeamView";
	// 开启动画展开步伐
	private static final int STEP = 10;

	private Bitmap[] bm_TaskNum = new Bitmap[MAX_PLANET];

	private com.david.teamviewproject.DataAdapter mDataAdapter;

	// private long lastDrawTime = getDrawingTime();
	// 开启动画path
	Path animPath = new Path();

	// 头像圆空间
	Path path_user_head = new Path();

	// 头像范围长方形空间
	Rect rect_user_head = new Rect();
	// 大头像矩形空间
	Rect rect_big_user_head = new Rect();
	// 大头像 圆空间
	Path path_big_user_head = new Path();
	// 大头像半径
	int radii_bigUserHead = 0;

	// 任务数量半径
	int radii_taskNum = 0;

	// path 任务数量圆圈path
	Path mPath_TaskNum_Circle = new Path();
	// path 任务矩形框
	Rect rect_TaskNum_Circle = new Rect();

	// 中心人员
	private TeamData sun;
	// 相关人员
	private List<TeamData> planet = new ArrayList<TeamData>(MAX_PLANET);

	// 临时周边的定点位置
	private Point loacation = new Point();

	private int mWidth = 0;
	private int mHeight = 0;
	private com.david.teamviewproject.Custom_Oval oval;
	private int radii_user_head;
	// adapter 数据大小
	private int size;

	// 默认头像
	private Bitmap defaultUserHead;

	// 开启动画圆当前半径
	private int visibleRadii = 0;
	// 角度调整值
	private float degreeAdjust = 0;

	Paint mPaint;

	// 点击事件处理接口
	private OnItemClickListener listener;

	public TeamView(Context context) {
		super(context);
		ini(context);

		// TODO Auto-generated constructor stub
	}

	public TeamView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ini(context);
		// TODO Auto-generated constructor stub
	}

	public TeamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ini(context);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	private void ini(Context context) {
		if (Build.VERSION.SDK_INT >= 11) {
			// 3.0以上版本需要关闭硬件加速。
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		TextView tv = new TextView(context);
		tv.setBackgroundResource(R.drawable.bg_task_num);
		tv.setGravity(Gravity.CENTER);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(25);

		// tv.layout(-tv.getMeasuredWidth(),-tv.getMeasuredWidth(),
		// -tv.getMeasuredWidth(), -tv.getMeasuredWidth())

		for (int i = 0; i < 10; i++) {

			tv.setText(i == 9 ? "9+" : String.valueOf(i + 1));
			tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			tv.layout(-tv.getMeasuredWidth(), -tv.getMeasuredHeight(), 0, 0);

			Log.e(TAG, "tv.getMeasuredWidth():" + tv.getMeasuredWidth() + ",tv.getMeasuredHeight():" + tv.getMeasuredHeight());
			tv.buildDrawingCache();
			bm_TaskNum[i] = Bitmap.createBitmap(tv.getDrawingCache());

		}
		tv.destroyDrawingCache();

		oval = new com.david.teamviewproject.Custom_Oval();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.drawColor(Color.WHITE);
		// long time = getDrawingTime();
		// // drawLine first
		// Log.e(TAG, "drawTime:" + time + ",lastDrawTime:" + lastDrawTime + ",  delta time:" + (time - lastDrawTime));
		// lastDrawTime = time;
		if (sun == null) {
			return;
		}

		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setDither(true);
		}
		/**
		 * 动画绘制， 根据不断扩大剪切的圆的半径范围来展开图像区域 只有第一次生成界面时才会执行该动画
		 */
		if (visibleRadii < mWidth || visibleRadii < mHeight) {

			visibleRadii += STEP;
			animPath.reset();
			animPath.addCircle(mWidth / 2, mHeight / 2, visibleRadii, Direction.CCW);
			canvas.clipPath(animPath);

			invalidate();

		}

		// 移动至中心位置
		canvas.translate(center.x, center.y);
		float averageDegree = 360f / size;
		for (int i = 0; i < size; i++) {

			// 获取当前item角度值
			float currentDegree = averageDegree * i + degreeAdjust;

			// 获取当前角度在圆环上的位置 相对于圆心
			oval.getLocationOnDegree(currentDegree, loacation);

			// Log.e("TAG", "Degree:" + (degree * i) + ",loacationX:"
			// + loacation.x + ",loacationY:" + loacation.y);
			configLineRingPaint(mPaint);
			configLineWidth(mPaint, planet.get(i).relateTaskNum);
			canvas.drawLine(0, 0, loacation.x, loacation.y, mPaint);
			canvas.save();
			canvas.translate(loacation.x, loacation.y);

			// draw user_head
			canvas.save();
			configGrayRingPaint(mPaint);
			canvas.drawCircle(0, 0, radii_user_head + 3, mPaint);

			configWhiteRingPaint(mPaint);
			canvas.drawCircle(0, 0, radii_user_head + 2, mPaint);
			canvas.clipPath(path_user_head);

			Bitmap bm = planet.get(i).head == null ? defaultUserHead : planet.get(i).head;
			canvas.drawBitmap(bm, null, rect_user_head, null);
			canvas.restore();
			// draw task num

			int taskNumber = planet.get(i).taskNum;
			if (taskNumber > 0) {
				canvas.save();

				// translate to user head edge
				float x_task_num = (float) (radii_user_head * Math.cos((currentDegree + 180 + 30) * Custom_Oval.TO_RADIANS));
				float y_task_num = (float) (radii_user_head * Math.sin((currentDegree + 180 + 30) * Custom_Oval.TO_RADIANS));
				canvas.translate( x_task_num,  y_task_num);

				canvas.clipPath(mPath_TaskNum_Circle);
				canvas.drawBitmap(bm_TaskNum[taskNumber > 9 ? 9 : taskNumber - 1], null, rect_TaskNum_Circle, null);

				// canvas.drawText(String.valueOf(taskNumber),
				// -textPaint.getTextSize() / 2,
				// textPaint.getTextSize() / 2, textPaint);

				canvas.restore();
			}

			canvas.restore();

		}
		configGreenRingPaint(mPaint);
		canvas.drawCircle(0, 0, radii_bigUserHead + 4, mPaint);
		configWhiteRingPaint(mPaint);
		canvas.drawCircle(0, 0, radii_bigUserHead + 0, mPaint);
		canvas.save();

		canvas.clipPath(path_big_user_head);
		canvas.drawColor(Color.GREEN);
		canvas.drawBitmap(sun.head == null ? defaultUserHead : sun.head, null, rect_big_user_head, null);
		canvas.restore();

	}

	public void setAdapter(DataAdapter adapter) {
		if (mDataAdapter != null)
			mDataAdapter.deleteObserver(observer);

		adapter.addObserver(observer);
		mDataAdapter = adapter;
		defaultUserHead = mDataAdapter.getDefalutUserHead();
		observer.update(mDataAdapter, null);

	}

	private Observer observer = new Observer() {

		@Override
		public void update(Observable observable, Object data) {
			// TODO Auto-generated method stub

			int count = mDataAdapter == null ? 0 : mDataAdapter.getData() == null ? 0 : mDataAdapter.getData().size();

			sun = null;
			if (count > 0) {

				List<TeamData> datas = new ArrayList<TeamData>();
				datas.addAll(mDataAdapter.getData());
				// 执行排序

				SortList(datas, new Comparator<TeamData>() {

					@Override
					public int compare(TeamData lhs, TeamData rhs) {
						return rhs.taskNum - lhs.taskNum;
					}
				});

				boolean hasSetSun = false;
				planet.clear();
				for (TeamData temp : datas) {
					if (temp.isViewer && !hasSetSun) {
						hasSetSun = true;
						sun = temp;
					} else {
						planet.add(temp);
					}
				}
				if (!hasSetSun && planet.size() > 0) {
					sun = planet.remove(0);
				}

				size = planet.size();
				if (size > 8)
					size = 8;

			}

			postInvalidate();
		}
	};

	private int DIVIDE_COUNT = 6;

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = w;
		mHeight = h;
		center.x = mWidth / 2;
		center.y = mHeight / 2;

		int divide = mWidth;
		if (mWidth > mHeight)
			divide = mHeight;

		radii_user_head = divide / DIVIDE_COUNT / 2;
		int mOvalWidth = mWidth - radii_user_head * 3;
		int mOvalHeight = mHeight - radii_user_head * 3;
		oval.resize(mOvalWidth, mOvalHeight);

		path_user_head.reset();
		path_user_head.addCircle(0, 0, radii_user_head, Direction.CCW);
		// 设置普通头像矩形边距

		rect_user_head.left = -radii_user_head;
		rect_user_head.top = -radii_user_head;
		rect_user_head.right = radii_user_head;
		rect_user_head.bottom = radii_user_head;

		radii_bigUserHead = radii_user_head * 3 / 2;
		// 设置大个头像矩形边距
		rect_big_user_head.left = -radii_bigUserHead;
		rect_big_user_head.top = -radii_bigUserHead;
		rect_big_user_head.right = radii_bigUserHead;
		rect_big_user_head.bottom = radii_bigUserHead;
		// 设置大个头像矩形剪切值
		path_big_user_head.reset();
		path_big_user_head.addCircle(0, 0, radii_bigUserHead, Direction.CCW);
		// 任务数圆圈的半径
		radii_taskNum = radii_user_head * 2 / 5;
		// 任务数圆圈的剪切值
		mPath_TaskNum_Circle.reset();

		mPath_TaskNum_Circle.addCircle(0, 0, radii_taskNum, Direction.CCW);

		// 设置任务数量矩形边距
		rect_TaskNum_Circle.left = -radii_taskNum;
		rect_TaskNum_Circle.top = -radii_taskNum;
		rect_TaskNum_Circle.right = radii_taskNum;
		rect_TaskNum_Circle.bottom = radii_taskNum;

		// if (mDataAdapter != null)
		// mDataAdapter.notifyObservers();
	}

	// 最近点击位置
	private Point lastPosition = new Point();
	// 最近点击位置
	private Point startPosition = new Point();
	// 中心位置。
	private Point center = new Point();

	@Override
	public boolean onTouchEvent(MotionEvent me) {

		switch (me.getAction()) {

		case MotionEvent.ACTION_DOWN:

			lastPosition.x = (int) me.getX();
			lastPosition.y = (int) me.getY();
			startPosition.x = lastPosition.x;
			startPosition.y = lastPosition.y;
			break;

		case MotionEvent.ACTION_MOVE:

			double moveDstSqrt = Math.pow(me.getX() - lastPosition.x, 2) + Math.pow(me.getY() - lastPosition.y, 2);

			if (moveDstSqrt < 10) {
				break;
			}

			// 计算向量转换方向 利用X乘 z轴的正负值。

			// 手势向量
			Point v_swipe = new Point();
			v_swipe.x = (int) (me.getX() - lastPosition.x);
			v_swipe.y = (int) (me.getY() - lastPosition.y);

			// 终点到中心的向量

			Point v_center = new Point();
			v_center.x = (int) (center.x - me.getX());
			v_center.y = (int) (center.y - me.getY());
			// 根据向量x乘
			float z = v_swipe.x * v_center.y - v_swipe.y * v_center.x;
			// 计算转角方向 顺时针 / 逆时针。
			Direction dir;
			if (z > 0) {
				dir = Direction.CW;
			} else if (z < 0) {
				dir = Direction.CCW;
			} else { // 平行 无视
				break;

			}

			double lastDstSqrt = Math.pow(lastPosition.x - center.x, 2) + Math.pow(lastPosition.y - center.y, 2);

			double currentDstSqrt = Math.pow(me.getX() - center.x, 2) + Math.pow(me.getY() - center.y, 2);

			// 根据余弦公式 计算出夹角
			double cosA = (lastDstSqrt + currentDstSqrt - moveDstSqrt) / (2 * Math.sqrt(lastDstSqrt) * Math.sqrt(currentDstSqrt));

			double degree = Math.toDegrees(Math.acos(cosA));
			degreeAdjust += dir == Direction.CW ? degree : -degree;
			correctDegree();
			invalidate();
			lastPosition.x = (int) me.getX();
			lastPosition.y = (int) me.getY();

			break;

		case MotionEvent.ACTION_UP:
			if (listener != null && sun != null) {
				// 按下起来的距离不超过5
				if (Math.abs(me.getX() - startPosition.x) < 5 && Math.abs(me.getY() - startPosition.y) < 5) {

					// 点在圆心周围
					if (rect_big_user_head.contains(startPosition.x - center.x, startPosition.y - center.y)) {

						listener.onItemClick(sun.position, sun.data);

					}
					for (int i = 0; i < size; i++) {

						// 获取当前item角度值
						float currentDegree = 360f / size * i + degreeAdjust;
						// 获取当前角度在圆环上的位置 相对于圆心
						oval.getLocationOnDegree(currentDegree, loacation);
						loacation.offset(center.x, center.y);
						if (rect_user_head.contains(startPosition.x - loacation.x, startPosition.y - loacation.y)) {
							TeamData data = planet.get(i);
							listener.onItemClick(data.position, data.data);
							break;
						}

					}
				}

			}
			break;

		}

		return true;
	}

	/**
	 * 校正旋转值
	 */
	private void correctDegree() {

		if (degreeAdjust > 360)
			degreeAdjust -= 360;
		else if (degreeAdjust < 0)
			degreeAdjust += 360;

	}


	/**
	 * 配置线段的属性 根据任务关联数据确定线条粗细 透明度
	 * 
	 * @param linePaint
	 * @param relateTaskNum
	 */
	private void configLineWidth(Paint linePaint, int relateTaskNum) {

		int lineWidth = relateTaskNum;
		if (lineWidth < 4)
			lineWidth = 4;
		else if (lineWidth > 10)
			lineWidth = 10;

		linePaint.setStrokeWidth(lineWidth / 2);

		linePaint.setAlpha(relateTaskNum == 0 ? 32 : 255);

	}



	// 绿色圆环画笔

	private void configGreenRingPaint(Paint greenRingPaint) {

		greenRingPaint.setStyle(Style.STROKE);
		greenRingPaint.setColor(Color.GREEN);
		greenRingPaint.setStrokeWidth(3);
	}

	// 白色圆环画笔
	private void configWhiteRingPaint(Paint whiteRingPaint) {

		whiteRingPaint.setStyle(Style.STROKE);
		whiteRingPaint.setColor(Color.WHITE);
		whiteRingPaint.setStrokeWidth(3);
	}

	// 灰色圆环画笔
	private void configGrayRingPaint(Paint grayRingPaint) {

		grayRingPaint.setStyle(Style.STROKE);
		grayRingPaint.setColor(Color.LTGRAY);
		grayRingPaint.setStrokeWidth(3);
	}

	// 灰色圆环画笔
	private void configLineRingPaint(Paint linePaint) {
		// 线条画笔

		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(8);
		linePaint.setColor(Color.GREEN);
	}

	public interface OnItemClickListener {

		public void onItemClick(int position, Object data);
	}

	/**
	 * 设置监听器。
	 * 
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	/**
	 * 封装 对列表数据进行排序
	 * 
	 * @param datas
	 * @param comparator
	 */
	@SuppressWarnings("unchecked")
	public static void SortList(List datas, Comparator comparator) {

		if (datas == null || comparator == null)
			return;

		int size = datas.size();
		Object[] array = new Object[size];
		for (int i = 0; i < size; i++) {
			array[i] = datas.get(i);
		}

		Arrays.sort(array, comparator);

		datas.clear();
		for (int i = 0; i < size; i++) {
			datas.add(array[i]);
		}

	}

}
