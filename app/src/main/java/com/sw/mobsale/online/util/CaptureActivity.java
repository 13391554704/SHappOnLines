package com.sw.mobsale.online.util;

import java.io.IOException;
import java.util.Vector;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.sw.mobsale.online.R;
import com.sw.mobsale.online.camera.CameraManager;
import com.sw.mobsale.online.decoding.CaptureActivityHandler;
import com.sw.mobsale.online.decoding.InactivityTimer;
import com.sw.mobsale.online.ui.BaseActivity;
import com.sw.mobsale.online.ui.CarLoadActivity;
import com.sw.mobsale.online.ui.LoadingActivity;
import com.sw.mobsale.online.view.ViewfinderView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 扫描订单
 */
public class CaptureActivity extends BaseActivity implements Callback {
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	//head文件
	private TextView tvTitle;
	private RelativeLayout rlBack;
	private TextView tvOrderNo;
	private String orderNo;
	private MyHandler mhandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		tvTitle = (TextView) findViewById(R.id.main_title);
		tvTitle.setText("二维码扫描");
		tvTitle.setTextSize(24);
		rlBack = (RelativeLayout) findViewById(R.id.head_rl_title);
		rlBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CaptureActivity.this, LoadingActivity.class);
				startActivity(intent);
				finish();
			}
		});
		tvOrderNo = (TextView) findViewById(R.id.test);
		mhandler = new MyHandler();
		//扫码单数量
		MyThread.getInstance(CaptureActivity.this).OrderThread(mhandler,Constant.SAOMA_ORDER,"","H","","","");
		// CameraManager
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy()
	{
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder)
	{
		try
		{
			CameraManager.get().openDriver(surfaceHolder);
		}
		catch (IOException ioe)
		{
			return;
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (handler == null)
		{
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(final Result obj, Bitmap barcode)
	{
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();
		orderNo = obj.getText().toString();
		MyThread.getInstance(CaptureActivity.this).OrderDetailThread(mhandler,Constant.TWO_ORDER_DETAIL_PATH,orderNo,"","");
	}


	/**
	 * handler
	 */
	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case Constant.ORDER_DETAIL:
					String result = msg.getData().getString("result");
					try {
						JSONObject object = new JSONObject(result);
						String message = object.getString("errorMessage");
						if ("err".equals(message)){
							Toast.makeText(CaptureActivity.this,"此订单无效!",Toast.LENGTH_SHORT).show();
							AlertDialog.Builder dialog = new AlertDialog.Builder(CaptureActivity.this);
							dialog.setTitle("提示!").setMessage("订单:"+ orderNo +"无效!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(CaptureActivity.this,CaptureActivity.class);
									startActivity(intent);
									finish();
								}
							}).create().show();
						}else{
							Intent intent = new Intent(CaptureActivity.this, CarLoadActivity.class);
							intent.putExtra("name","codeOrder");
							intent.putExtra("orderNo",orderNo);
							startActivity(intent);
							finish();
						}
					}catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				//扫码订单数
				case Constant.SAOMA_ORDER:
					try {
						String resultNum = msg.getData().getString("result");
						Log.d("TAG","cap result->"+resultNum);
						JSONObject object = new JSONObject(resultNum);
						JSONArray array = object.getJSONArray("rows");
						for (int i = 0; i < array.length(); i++) {
							JSONObject jo = array.getJSONObject(i);
							String message = jo.getString("count");
							tvOrderNo.setText(message);
						}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					break;
			}
		}
	}

	private void initBeepSound()
	{
		if (playBeep && mediaPlayer == null)
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try
			{
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			}
			catch (IOException e)
			{
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate()
	{
		if (playBeep && mediaPlayer != null)
		{
			mediaPlayer.start();
		}
		if (vibrate)
		{
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener()
	{
		public void onCompletion(MediaPlayer mediaPlayer)
		{
			mediaPlayer.seekTo(0);
		}
	};

}