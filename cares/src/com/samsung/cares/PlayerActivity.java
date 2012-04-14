package com.samsung.cares;

import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.IntentFilter;
import android.content.Intent;
import 	android.content.BroadcastReceiver;

public class PlayerActivity extends Activity 
	implements OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, SurfaceHolder.Callback {
	//������ �ּ�
	private String LINK;
	
	private MediaPlayer	mediaPlayer;
	private PlayerSurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	private boolean isPaused = false; //�Ͻ�����
	private boolean isEnd = false; //�������� ������ �� ����ƴ���
	
	private View titleView = null;
	private View controllerView = null; //�÷��̾� ��Ʈ�ѷ�(���, ����, ������, �ڷ�, �ð�)
	private long controllerViewActionTime = 0L; //ȭ�鿡 ��Ʈ�ѷ��� ���̰� ������� �ð��� ���´� ���
	
	private int videoPlayTime = 0; //������ �÷��� ����(�ð� 1000/1��)
	private int skipPrevTime = 5000; //��ŵ �ð�(1000=1��)
	private int skipNextTime = 15000; //��ŵ �ð�(1000=1��)
	private SeekBar timeBar = null; //������
	//private SeekBar volumeBar = null; //�����
	private ImageButton playButton = null; //���, ����
	private ImageButton skipPrevButton = null; //�ڷ�
	private ImageButton skipNextButton = null; //������
	private ProgressBar loadingProgressBar = null; //�ε����α׷���
	private TextView currentPlayTimeView = null; //���� ����ð� �ؽ�Ʈ
	private TextView maxPlayTimeView = null; //��ü ��� �ð� �ؽ�Ʈ	
	
	private String CHANNELID = "";
	private String SCHEDULEID = "";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { //HONEYCOMB
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                    WindowManager.LayoutParams.FLAG_FULLSCREEN );

		}
		else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		*/
		setContentView(R.layout.player);
		Logger.d("onCreate");
			
		surfaceView = (PlayerSurfaceView)findViewById(R.id.surface);
		surfaceView.addTapListener(onTap);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	
		titleView = findViewById(R.id.top_panel);
	    controllerView = findViewById(R.id.bottom_panel);
		timeBar = (SeekBar)findViewById(R.id.timeline);
		timeBar.setOnSeekBarChangeListener(onTimeBarSkip);
		//volumeBar = (SeekBar)findViewById(R.id.vodplayer_volume);
		//volumeBar.setOnSeekBarChangeListener(onVolume);		
		loadingProgressBar = (ProgressBar)findViewById(R.id.player_loading_progress);
		loadingProgressBar.setVisibility(View.VISIBLE);
			
		currentPlayTimeView = (TextView)findViewById(R.id.text_playtime_current);
		maxPlayTimeView = (TextView)findViewById(R.id.text_playtime_max);
		
		//AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);  
		//int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//volumeBar.setProgress(currentVolume);
		
		playButton = (ImageButton)findViewById(R.id.button_play_pause);
		playButton.setOnClickListener(onPlayPause);
		skipPrevButton = (ImageButton)findViewById(R.id.button_play_prev);
		skipPrevButton.setOnClickListener(onSkipPrev);
		skipNextButton = (ImageButton)findViewById(R.id.button_play_next);
		skipNextButton.setOnClickListener(onSkipNext);
	
	    XMLData xmlData = getIntent().getExtras().getParcelable("xmlData");
	        
	    CHANNELID = xmlData.channelId;
		SCHEDULEID = xmlData.scheduleId;
			
	    Status.NETWORK = Util.checkNetworkStatus(this);
			
		if(Status.NETWORK == Status.NETWORK_WIFI) {
			LINK = xmlData.HQFileURL;
		}
		else if(Status.NETWORK == Status.NETWORK_3G) {
			LINK = xmlData.fileURL;
		}
		else {
			showAlertDialog("Network");
		}
		//setTitle(xmlData.title);
		TextView title = (TextView)titleView.findViewById(R.id.text_title); 
		title.setText(xmlData.title);
			
		setBroadCastReceiver();
		setLog();
	}
	
	private void setBroadCastReceiver() {
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onResume() {
		Logger.d("onResume");
		isPaused = false;
		isEnd = false;
		if(surfaceView != null) {
			surfaceView.postDelayed(onEverySecond, 1000);
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Logger.d("onPause");
		isPaused = true;
		Status.SCREEN = Status.SCREEN_OFF;
		if(mediaPlayer != null) {
			//mediaPlayer.pause();
			mediaPlayer.stop();
			mediaPlayer.reset();
			Logger.d("mediaPlayer stopped");
		}
		finish(); //prevent home button while playing video.
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Logger.d("onDestroy");
		surfaceView.removeTapListener(onTap);
		unregisterReceiver(receiver);
		Status.SCREEN = Status.SCREEN_OFF;
		if(mediaPlayer != null) {
			//mediaPlayer.pause();
			mediaPlayer.stop();
			mediaPlayer.reset();
			Logger.d("mediaPlayer stopped");
		}
		finish();
		super.onDestroy();
	}
	
	public void onCompletion(MediaPlayer arg0) {
		
		Runnable run = new Runnable() {
			@Override
			public void run() {
				finish();
			}
		};
		
		Handler handler = new Handler();
		handler.postDelayed(run, 3000);
	}

	//������ ����غ� �Ϸ�
	public void onPrepared(MediaPlayer mediaplayer) {
		Logger.d("onPrepared");
		Logger.d("Status.SCREEN:"+Status.SCREEN);
		if(Status.SCREEN == Status.SCREEN_ON) {
			
			Status.SCREEN = Status.SCREEN_OFF;
		
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				int width = mediaPlayer.getVideoWidth();
				int height = mediaPlayer.getVideoHeight();
				surfaceHolder.setFixedSize(width, height);
			}
			videoPlayTime = mediaPlayer.getDuration(); 
			timeBar.setMax(videoPlayTime);
			timeBar.setProgress(0);
			timeBar.setSecondaryProgress(0);
			SetTextTime(maxPlayTimeView, videoPlayTime);
			
			mediaPlayer.start();
			controllerViewActionTime = SystemClock.elapsedRealtime();
			titleView.setVisibility(View.VISIBLE);
			controllerView.setVisibility(View.VISIBLE);
			//volumeBar.setVisibility(View.VISIBLE);
			loadingProgressBar.setVisibility(View.GONE);
			
		}
		else {
			finish();
		}
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	}

	//����غ� �Ϸ�
	public void surfaceCreated(SurfaceHolder holder) {
		Logger.d("surfaceCreated");
		playVideo(LINK);
		clearPanels();
	}

	private void playVideo(String url) {
		try {
			if(mediaPlayer == null)	{
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setScreenOnWhilePlaying(true);
			}
			else {
				mediaPlayer.stop();
				mediaPlayer.reset();
			}
			
			isEnd = false;
			mediaPlayer.setDataSource(url);
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnBufferingUpdateListener(this);
			loadingProgressBar.setVisibility(View.VISIBLE);
		}
		catch(Throwable t) {	
			t.printStackTrace();
		}
	}
	
	private void clearPanels() {
		controllerViewActionTime = 0;
		titleView.setVisibility(View.GONE);
		controllerView.setVisibility(View.GONE);
		//volumeBar.setVisibility(View.GONE);
	}
	
	//��ü ����ð� �ؽ�Ʈ ǥ�� 
	private void SetTextTime(TextView view, int time) {
		int hour = time / 360000;	//�ð�
		time %= 360000;
		int minute = time / 60000;	//��
		time %= 60000;
		int second = time / 1000;	//��
		
		String h = (hour < 10) ? "0" + Integer.toString(hour) : Integer.toString(hour);
		String m = (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute);
		String s = (second < 10) ? "0" + Integer.toString(second) : Integer.toString(second);
		view.setText(h + ":" + m + ":" + s);
	}
	
	//ȭ�� ��ġ
	private PlayerSurfaceView.TapListener onTap = new PlayerSurfaceView.TapListener() {
		public void onTap(MotionEvent event) {
			controllerViewActionTime = SystemClock.elapsedRealtime();
			if(controllerView.getVisibility() == View.VISIBLE) { //�޴� �����
				clearPanels();
			}
			else { //�޴� ���̱�
				titleView.setVisibility(View.VISIBLE);
				controllerView.setVisibility(View.VISIBLE);
				//volumeBar.setVisibility(View.VISIBLE);
			}
		}
	};
	
	private Runnable onEverySecond = new Runnable() {
		public void run() {
			//��Ʈ�ѷ��� ���� �ð��� ������� (1000=1��)
			if(controllerViewActionTime > 0 && SystemClock.elapsedRealtime() - controllerViewActionTime > 5000) {
				clearPanels();
			}
			if(mediaPlayer != null) {
				int time = mediaPlayer.getCurrentPosition();
				timeBar.setProgress(time);
				SetTextTime(currentPlayTimeView, time);
			}
			if(!isPaused) {
				surfaceView.postDelayed(onEverySecond, 1000);
			}
		}
	};
	
	//���, ���� ��ư
	private View.OnClickListener onPlayPause = new View.OnClickListener() {
		public void onClick(View v) {
			controllerViewActionTime = SystemClock.elapsedRealtime();
			
			if(mediaPlayer != null) {
				if(mediaPlayer.isPlaying()) { //��� ���̸� 
					playButton.setImageResource(R.drawable.bt_play);
					mediaPlayer.pause();
				}
				else { //��������� ������
					playButton.setImageResource(R.drawable.bt_stop);
					if(isEnd) {
						playVideo(LINK);
						clearPanels();
					}
					else {
						mediaPlayer.start();
					}
				}
			}
		}
	};
	
	//�ڷ� ��ŵ��ư
	private View.OnClickListener onSkipPrev = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			int time = mediaPlayer.getCurrentPosition() - skipPrevTime;
			if(time < 0) {
				time = 0;
			}
			mediaPlayer.seekTo(time);
		}
	};
	
	//������ ��ŵ��ư
	private View.OnClickListener onSkipNext = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			int time = mediaPlayer.getCurrentPosition() + skipNextTime;
			if(time > videoPlayTime) {
				time = videoPlayTime;
			}
			mediaPlayer.seekTo(time);
		}
	};
	
	//�÷��� Ÿ�ӹ�
	private SeekBar.OnSeekBarChangeListener onTimeBarSkip = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekbar, int i, boolean flag) {
			if(flag) {
				mediaPlayer.seekTo(i);
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekbar) {
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekbar) {
		}
	};
	
	//������
	/*
	private SeekBar.OnSeekBarChangeListener onVolume = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekbar, int i, boolean flag) {
			if(flag) {
				AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);  
				//int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (i/2), AudioManager.FLAG_PLAY_SOUND);
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekbar) {
		}
		@Override
		public void onStopTrackingTouch(SeekBar seekbar) {
		}
	};
	*/
	
	//���۸� ǥ��
	@Override
	public void onBufferingUpdate(MediaPlayer mediaplayer, int i) {
		//i=100%�� �Ѿ��
		//�ۼ�Ʈ���� ǥ��
		timeBar.setSecondaryProgress((videoPlayTime / 100) * i);
	}
	
	//�ڷΰ��� ��ư
	@Override 
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if(keyCode == KeyEvent.KEYCODE_BACK) { 
        	if(controllerView.getVisibility() == View.VISIBLE) { //�޴� �����
				clearPanels();
        	}
        	else {
        		Status.SCREEN = Status.SCREEN_OFF;
        		finish();
        	}
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            	Logger.d("ACTION_SCREEN_OFF");
            	Logger.d("" + mediaPlayer.isPlaying());
            	Status.SCREEN = Status.SCREEN_OFF;
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            	Logger.d("ACTION_SCREEN_ON");
            	Logger.d("" + mediaPlayer.isPlaying());
            	Status.SCREEN = Status.SCREEN_ON;
            }
        }
    };
	
	protected void setLog() {
		
		Runnable run = new Runnable() {
			@Override
			public void run() {
		        
		        try {
		        	
		        	String XMLURL = "http://www.samsungsupport.com/spstv/rss/android.jsp?type=VIDEO_LOG&manufacturer=" + Status.MANUFACTURER + "&model=" + Status.MODEL + "&channelId=" + CHANNELID + "&scheduleId=" + SCHEDULEID;
		        	//Logger.d(XMLURL);

		        	URL url = new URL(XMLURL);
		        	
		        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			        factory.setNamespaceAware(true); 
			        XmlPullParser xpp = factory.newPullParser(); 
			            
		            url.openStream();
		            
		            InputStream in = url.openStream();
		            xpp.setInput(in, "utf-8");
			         
		            int eventType = xpp.getEventType();
		            String tag;
		            
		            while(eventType != XmlPullParser.END_DOCUMENT) { 
		            	if(eventType == XmlPullParser.START_DOCUMENT) {
		            	}
		            	else if(eventType == XmlPullParser.END_DOCUMENT) { 
		            	}
		            	else if(eventType == XmlPullParser.START_TAG) {
		            		
		            		tag = xpp.getName();                  
		                  
		            		if(tag.equals("channel")) {
		            			String strStatus = xpp.getAttributeValue(0);
		            			String strMessage = xpp.getAttributeValue(1);
		            		}  
		            	}
		            	
		            	eventType = xpp.next(); 
		            }
		        }
		        catch(Exception e) {
		        	Logger.d("Player - Exception");
		        	//showAlertDialog("Network");
		        	//e.printStackTrace();
		        }
			}
		};
		
		Handler handler = new Handler();
		handler.postDelayed(run, 1000);
	}
	
	private void showAlertDialog(String title) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        
        if(title.equals("Network")) {
        	alertDialog.setMessage("An error occurred while fetching data. Please try again later.");
        	/*
            alertDialog.setPositiveButton("Close",
	        	new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	dialog.dismiss();
	                finish();
	            }
	        });
	        */
        }
        
        alertDialog.show();
    }
    
    private void showResultDialog(String title, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Close",
        	new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}