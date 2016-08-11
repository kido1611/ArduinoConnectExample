package id.kido1611.arduinoconnect.example;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.kido1611.arduinoconnect.ArduinoConnect;
import id.kido1611.arduinoconnect.ArduinoConnectCallback;

public class MainActivity extends AppCompatActivity
        implements ArduinoConnectCallback{

    private ArduinoConnect mArduinoConnect;

    private RelativeLayout mLayoutConnected;
    private Button mButtonConnect, mButtonSendMessage;
    private TextView mTextViewStatus;
    private TextInputEditText mEditTextInputMessage, mEditTextOutputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mArduinoConnect = new ArduinoConnect(this, this.getSupportFragmentManager(), this);
        mArduinoConnect.setSleepTime(1000);

        mLayoutConnected = (RelativeLayout) findViewById(R.id.layout_connected);
        mButtonConnect = (Button) findViewById(R.id.button_connect);
        mTextViewStatus = (TextView) findViewById(R.id.text_status);
        mEditTextInputMessage = (TextInputEditText) findViewById(R.id.edittext_input_message);
        mEditTextOutputMessage = (TextInputEditText) findViewById(R.id.edittext_output_message);
        mButtonSendMessage = (Button) findViewById(R.id.button_send_message);

        mButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mArduinoConnect.isConnected()) {
                    mArduinoConnect.disconnected();
                }else
                    mArduinoConnect.showDialog();
            }
        });
        mButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mEditTextInputMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(MainActivity.this, "Text is empty", Toast.LENGTH_SHORT).show();
                }else{
                    mArduinoConnect.sendMessage(message);
                }
            }
        });

        cekLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mArduinoConnect!=null)
            mArduinoConnect.onActivityResult(requestCode, resultCode, data);
    }

    public void cekLayout(){
        if(mArduinoConnect.isConnected()){
            mLayoutConnected.setVisibility(View.VISIBLE);
            mButtonConnect.setText("Disconnect");
            mTextViewStatus.setText("Connected");
        }else{
            mLayoutConnected.setVisibility(View.GONE);
            mButtonConnect.setText("Connect");
            mTextViewStatus.setText("Disconnected");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mArduinoConnect!=null) mArduinoConnect.disconnected();
    }

    @Override
    public void onSerialTextReceived(String s) {
        mEditTextOutputMessage.setText(s);
    }

    @Override
    public void onArduinoConnected(BluetoothDevice bluetoothDevice) {
        cekLayout();
        Toast.makeText(this, "Connected with "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArduinoDisconnected() {
        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
        cekLayout();
    }

    @Override
    public void onArduinoNotConnected() {
        cekLayout();
        Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArduinoConnectFailed() {
        cekLayout();
        Toast.makeText(this, "Failed to connect bluetooth", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothDeviceNotFound() {
        Toast.makeText(this, "Bluetooth device not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothFailedEnabled() {
        Toast.makeText(this, "Failed to turn on Bluetooth", Toast.LENGTH_SHORT).show();
    }
}
