package com.aprilbrother.blueduino;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aprilbrother.blueduino.bean.PinInfo;
import com.aprilbrother.blueduino.contants.Contants;
import com.aprilbrother.blueduino.globalvariables.GlobalVariables;
import com.aprilbrother.blueduino.utils.ByteUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint("NewApi")
public class CommunicateActivity extends Activity {

	private static final int UART_PROFILE_DISCONNECTED = 21;

	private UartService mService = null;
	private int mState = UART_PROFILE_DISCONNECTED;
	private static final int UART_PROFILE_CONNECTED = 20;
	private BluetoothDevice device;

	private CanvasView customCanvas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Set text view
		setContentView(R.layout.activity_communicate);
		TextView tv = (TextView) findViewById(R.id.responseView);
		tv.setMovementMethod(new ScrollingMovementMethod());

		// Set Matrix view
		// (Also commented out customCanvas references (3) in BroadcastReceiver
		//setContentView(R.layout.activity_visualize);
		//customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

		super.onCreate(savedInstanceState);

		//if(savedInstanceState != null) {
			init();

			service_init();

			setViewData();
		//}

	}

	private void setViewData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// waiting for service to connect
					Thread.sleep(500);

					// connect device
					connectService(device);

					// waiting for device connect
					Thread.sleep(3000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void init() {
		Bundle bundle = getIntent().getExtras();
		device = bundle.getParcelable("device");
	}

	public void sendMessage(View view){
		byte[] value;
		EditText chatDialog = (EditText)findViewById(R.id.tf_chat);
		try {
			value = chatDialog.getText().toString().getBytes("UTF-8");
			chatDialog.setText("");
			mService.writeRXCharacteristic(value);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void visualizePad(View view){
		Intent intent = new
				Intent(CommunicateActivity.this,VisualizeActivity.class);
		startActivity(intent);
	}
	/**
	 * connect the service to operate bluetooth
	 */
	private void connectService(BluetoothDevice device) {
		mService.connect(device.getAddress());
	}

	private void service_init() {
		Intent bindIntent = new Intent(this, UartService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}

	// UART service connected/disconnected
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
									   IBinder rawBinder) {
			mService = ((UartService.LocalBinder) rawBinder).getService();
			if (!mService.initialize()) {
				finish();
			}
		}

		public void onServiceDisconnected(ComponentName classname) {
			// // mService.disconnect(mDevice);
			mService = null;
		}
	};

	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String responseString;

			// *********************//
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				runOnUiThread(new Runnable() {
					public void run() {
						mState = UART_PROFILE_CONNECTED;
					}
				});
			}

			// *********************//
			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				runOnUiThread(new Runnable() {
					public void run() {
						mState = UART_PROFILE_DISCONNECTED;
						mService.close();
						// setUiState();
					}
				});
			}

			// *********************//
			if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
				mService.enableTXNotification();
			}
			// *********************//
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

				final byte[] txValue = intent
						.getByteArrayExtra(UartService.EXTRA_DATA);
				responseString = new String(txValue);
				String[] xy = responseString.split(",");

				//float x = Float.parseFloat(xy[0])/2 * customCanvas.width;
				//float y = Float.parseFloat(xy[1])/2 * customCanvas.height;

				try {
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

					TextView response = (TextView)findViewById(R.id.responseView);
					response.append(sdf.format(cal.getTime()) + " | Response: " + responseString + "\n");

					//customCanvas.startTouch(x,y);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// *********************//
			if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
				showMessage("Device doesn't support UART. Disconnecting");
				mService.disconnect();
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		return intentFilter;
	}

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		ABProtocol.mValues = new byte[0];
		try {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					UARTStatusChangeReceiver);
			unbindService(mServiceConnection);
			mService.stopSelf();
			mService = null;
		} catch (Exception ignore) {
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}