package example1.example1.mpos.mPOSDemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cardreader.audio.app.SComboReader_DUKPT;
import com.cardreader.audio.base.CardReaderAudioBase;
import com.cardreader.audio.sdk.AudioSeries;
import com.cardreader.audio.sdk.FileService;

import java.io.IOException;

public class CardOperations extends AppCompatActivity {
    private static final String TAG = "CARDREADER";
    private SComboReader_DUKPT sComboReader = null;
    private CardOperations cardOperations = null;
    // button
    Button btninit = null;
    Button btnbattery = null;
    Button btncardinformation = null;
    Button btninsert = null;
    Button btnemvdata = null;
    Button btnVersion=null;
    Button btnSenddata=null;
    // EditText
    EditText txtvalues = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_operations);

        cardOperations = this;
        // Filter HEADSET PLUG
        registerReceiver(mHeadsetReceiver, new IntentFilter(
                Intent.ACTION_HEADSET_PLUG));

        btninit = (Button) findViewById(R.id.init);
        btnbattery = (Button) findViewById(R.id.btnbattery);
        btncardinformation = (Button) findViewById(R.id.btncardinformation);
        btninsert = (Button) findViewById(R.id.btninsert);
        btnemvdata = (Button) findViewById(R.id.btnemvdata);
        btnVersion=(Button)findViewById(R.id.btnVersion);

        txtvalues = (EditText) findViewById(R.id.txtvalues);

        BtnEnabled(false);

        FileService read = new FileService(cardOperations);

        try {
            CardReaderAudioBase.readMODEL = read.readFile();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        btnSenddata=(Button)findViewById(R.id.btnSendData);
//        btnSenddata.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this,SendDataToServer.class);
//                startActivity(intent);
//            }
//        });
        SendMessage(MSG_STARTINIT, 0, 0);
    }

    public void btnClicks(View v) {
        int btnid = v.getId();
        if (btnid == btninit.getId()) {
            StartInit();
        } else if (btnid == btnbattery.getId()) {
            Send_DetectBatteryEnergy();
        } else if (btnid == btncardinformation.getId()) {
            Send_Information();
        } else if (btnid == btninsert.getId()) {
            Send_Insert();
        }else if (btnid == btnemvdata.getId()) {
            Send_EMV_Data();
        }else if (btnid==btnVersion.getId()) {
            Send_GetVersion();
        }else {
            Toast.makeText(this, "wrong btn", Toast.LENGTH_LONG).show();
        }


    }

    public void StartInit() {
        // Volume to the maximum
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        // Start SComboReader processing
        if (sComboReader.SComboReaderStart() != SComboReader_DUKPT.OK) {
            Toast.makeText(this, "init failled", Toast.LENGTH_LONG).show();
            return;
        }
        // Toast.makeText(this, "Before starting thread",
        // Toast.LENGTH_LONG).show();
        new InitThread().start();

    }

    public class InitThread extends Thread {
        @Override
        public void run() {
            if (sComboReader.SComboReaderInit() == SComboReader_DUKPT.OK) {
                Log.d(TAG, "MSG_INITDONE");
                SendMessage(MSG_INITDONE, 0, 0);
            } else {
                Log.d(TAG, "MSG_REINIT");
                SendMessage(MSG_REINIT, 0, 0);
            }
        }
    };

    public void ReInit() {

        myDialog = ProgressDialog.show(cardOperations, "Message",
                "System Initialization...");
        new ReInitThread().start();
    }

    public class ReInitThread extends Thread {
        public void run() {

            if (sComboReader.SComboReaderReInit() == SComboReader_DUKPT.OK) {
                SendMessage(MSG_INITDONE, 0, 0);

                FileService save = new FileService(cardOperations);
                try {
                    save.save();
                } catch (Exception e) {
                    // TODO
                    e.printStackTrace();
                }
            }

            else
                SendMessage(MSG_INITFALT, 0, 0, "Initial Fail.");
        }
    };

    // Detect Phone Jack plugged
    private boolean headsetConnected = false;

    public BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                boolean hasHeadset = (intent.getIntExtra("state", 0) == 1);
                boolean hasMicroPhone = (intent.getIntExtra("microphone", 0) == 1);
                if (hasHeadset && hasMicroPhone) {
                    headsetConnected = true;
                    CreateAudio();
                } else {
                    headsetConnected = false;
                    BtnEnabled(false);
                }

            }
        }
    };

    public void CreateAudio() {
        // Alloc a Singular Reader object
        if (sComboReader == null) {
            // Alloc a AudioSeriesPort parameter
            AudioSeries.AudioSeriesPort audioseriesport = new AudioSeries.AudioSeriesPort();
            sComboReader = new SComboReader_DUKPT();
            // Register Message callback handle
            sComboReader.RegisterMessage(myMessageHandler);
            // Open SComboReader Audio Device
            if (sComboReader.SComboReaderOpen(audioseriesport) != SComboReader_DUKPT.OK) {
                Toast.makeText(this, "System Error: Can't open audio device.",
                        Toast.LENGTH_LONG).show();
                // this.finish();
                // return;
            }

        }
    }

    private void BtnEnabled(boolean flag) {
        btninit.setEnabled(flag);
        btnbattery.setEnabled(flag);
        btninsert.setEnabled(flag);
        btncardinformation.setEnabled(flag);
        btnemvdata.setEnabled(flag);
        btnVersion.setEnabled(flag);
    }

    // communicate with the device

    public static final int MSG_STARTINIT = 0x01;
    public static final int MSG_INITDONE = 0x02;
    public static final int MSG_INITFALT = 0x03;
    public static final int MSG_REINIT = 0x04;

    public void SendMessage(int i_msg, int arg1, int arg2) {
        Message msg = new Message();
        msg.what = i_msg;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        myHandler.sendMessage(msg);
    }

    public void SendMessage(int i_msg, int arg1, int arg2, Object obj) {
        Message msg = new Message();
        msg.what = i_msg;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        myHandler.sendMessage(msg);
    }

    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STARTINIT: {

                    if (!headsetConnected) {
                        ShowMessage(cardOperations, "Message",
                                "Please insert SComboReader.", onDlgClick);
                    } else {
                        StartInit();
                    }
                    break;
                }
                case MSG_INITFALT: {
                    myDialog.dismiss();

                    if (msg.obj != null) {
                        String str_msg = (String) msg.obj;
                        ShowMessage(cardOperations, "Message", str_msg, null);
                    }

                    btninit.setEnabled(true);
                    break;
                }
                case MSG_INITDONE: {
                    //myDialog.dismiss();
                    BtnEnabled(true);
                    PutMessage("Initial Success.");
                    //myDialog.dismiss();
                    break;
                }
                case MSG_REINIT: {

                    // myDialog.dismiss();
                    if (!headsetConnected) {
                        ShowMessage(cardOperations, "Message",
                                "Please insert SComboReader.", onDlgClick);
                    } else {
                        ReInit();
                    }
                    break;
                }
            }

            super.handleMessage(msg);
        }
    };

    public Handler myMessageHandler = new Handler() {

        public void handleMessage(Message msg) {
            int SW = msg.arg1;
            switch (msg.what) {
                case SComboReader_DUKPT.CMD_Get_Version: {
                    SComboReader_DUKPT.ArrayByteValue val = (SComboReader_DUKPT.ArrayByteValue) msg.obj;

                    if (SW == SComboReader_DUKPT.OK) {

                        DispVersion ( val );
                    } else {
                        DispState ( SW );
                    }

                    BtnEnabled(true);
                    break;
                }

                case SComboReader_DUKPT.CMD_Detect_Battery_Energy: {
                    SComboReader_DUKPT.ArrayByteValue val = (SComboReader_DUKPT.ArrayByteValue) msg.obj;

                    if (SW == SComboReader_DUKPT.OK) {
                        DispEnergy(val);
                    } else {
                        DispState(SW);
                    }

                    BtnEnabled(true);
                    break;
                }
                case SComboReader_DUKPT.CMD_Reset_Chip:
                case SComboReader_DUKPT.CMD_Set_Time_Into_Power_Down:
                case SComboReader_DUKPT.CMD_Select_Encrypt_Mode: {
                    // DispState(SW);
                    BtnEnabled(true);
                    break;
                }
                // card inserted case
                case SComboReader_DUKPT.CMD_Detect_ICC_Inserted_Or_Not: {
                    SComboReader_DUKPT.ArrayByteValue val = (SComboReader_DUKPT.ArrayByteValue) msg.obj;

                    if (SW == SComboReader_DUKPT.OK) {
                        DispValueInsert(val);
                    } else {
                        DispState(SW);
                    }

                    BtnEnabled(true);
                    break;
                }
                // /////////////////////////// cardinformation case
                case SComboReader_DUKPT.CMD_ICC_Information: {
                    SComboReader_DUKPT.ArrayByteValue val = (SComboReader_DUKPT.ArrayByteValue) msg.obj;

                    if (SW == SComboReader_DUKPT.OK) {
                        DispValueInformation(val);
                    } else {
                        DispState(SW);
                    }

                    BtnEnabled(true);
                    break;
                }

                // /////////////////////// emv data case
                case SComboReader_DUKPT.CMD_Get_EMV_Data:
                case SComboReader_DUKPT.CMD_Verify_PIN:
                {
                    SComboReader_DUKPT.ArrayByteValue val = (SComboReader_DUKPT.ArrayByteValue) msg.obj;

                    if (SW == SComboReader_DUKPT.OK)
                    {
                        DispValue(val);
                    } else
                    {
                        DispState(SW);
                    }

                    BtnEnabled(true);
                    break;
                }
                //end emv data case
            }

            super.handleMessage(msg);
        }
    };

    // message dialog section
    public static void ShowMessage(Context context, String title, String msg) {
        ShowMessage(context, title, msg, null);
    }

    public static void ShowMessage(Context context, String title, String msg,
                                   final View.OnClickListener onClickListener) {
        String btn = "OK";

        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setPositiveButton(btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                if (onClickListener != null) {
                    onClickListener.onClick(null);
                }
            }
        });

        dlg.show();
    }

    public ProgressDialog myDialog;
    public View.OnClickListener onDlgClick = new View.OnClickListener() {

        public void onClick(View v) {

            if (!headsetConnected) {
                ShowMessage(cardOperations, "Message",
                        "Please insert SComboReader.", onDlgClick);
                return;
            }

            // Start SComboReader processing
            StartInit();
        }
    };

    public void PutMessage(String msg) {
        Log.d(TAG, "hapaaaaaaaaaa");

        // TextView lb = new TextView(sComboActivity);
        // lb.setText(string);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    // //cardinformation send
    public void Send_Information() {
        BtnEnabled(false);
        PutMessage("Get CardInformation");
        sComboReader.ICCInformation(null);
    }

    // detect battery level
    public void Send_DetectBatteryEnergy() {
        // Register Message callback handle
        sComboReader.RegisterMessage(myMessageHandler);
        BtnEnabled(false);
        sComboReader.DetectBatteryEnergy(null);
    }

    // Display Energy
    public void DispEnergy(SComboReader_DUKPT.ArrayByteValue energyString) {
        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        for (int i = 0; i < energyString.Value.length; i++) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(String.format("%02x", energyString.Value[i]));
        }

        if (sb.toString().equalsIgnoreCase("00")) {
            this.PutMessage("Battery empty");
        }

        else if (sb.toString().equalsIgnoreCase("01")) {
            this.PutMessage("Battery 1/3");
        } else if (sb.toString().equalsIgnoreCase("02")) {
            this.PutMessage("Battery 2/3");
        } else if (sb.toString().equalsIgnoreCase("03")) {
            this.PutMessage("Battery full");
        }
    }

    // Display Status
    public void DispState(int SW) {

        this.PutMessage(String.format("State: %s.", sComboReader.GetCSW(SW)));
    }

    // Display Receive version
    private void DispVersion(SComboReader_DUKPT.ArrayByteValue versionString) {

        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        for (int i = 0; i < versionString.Value.length; i++) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(String.format("%s", (char) versionString.Value[i]));
        }

        this.PutMessage(sb.toString());
    }

    // ////////////card reading processing///////////////////
    public void DispValueInformation(SComboReader_DUKPT.ArrayByteValue insertString) {
        Log.d(TAG, "DispValueInformation");
        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        for (int i = 0; i < insertString.Value.length; i++) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(String.format("%02x", insertString.Value[i]));
        }

        String card_data = sb.toString().replace(" ", "");
        ;
        ;

        int card_num = card_data.lastIndexOf("a1");
        int card_name = card_data.lastIndexOf("b1");
        int card_exp = card_data.lastIndexOf("c1");

        String card_number = changeHexString2CharString(card_data.substring(
                card_num + 2, card_name));
        String user_name = changeHexString2CharString(card_data.substring(
                card_name + 2, card_exp));
        String expired_date = changeHexString2CharString(card_data.substring(
                card_exp + 2, card_data.length()));
        expired_date = expired_date.substring(0, 2) + "/"
                + expired_date.substring(2, expired_date.length());

        int card_type1 = Integer.parseInt(card_number.substring(0, 1));
        int card_type2 = Integer.parseInt(card_number.substring(0, 3)); // 3
        int card_type3 = Integer.parseInt(card_number.substring(0, 4));
        String type;
        if (card_type1 == 4) {
            type = "Visa Card";
        } else if (card_type1 == 5) {
            type = "Master Card";
        } else if ((card_type1 == 3)
                && (card_type2 >= 340 && card_type2 <= 379)
                && (card_number.length() == 15)) {
            type = "AE Card";
        } else if (((card_type1 == 1) && (card_type3 == 1800))
                || ((card_type1 == 2) && (card_type3 == 2131)))//
        {
            type = "JCB Card";

        } else if ((card_type2 >= 300 && card_type2 <= 399)
                && (card_number.length() == 16) || ((card_type1 == 3))) {
            type = "JCB Card";
        } else {
            type = "Card";
        }

        Log.d(TAG, type + " " + card_number + " " + expired_date + " "
                + user_name);

        txtvalues.setText(type + " " + card_number + " " + expired_date + " "
                + user_name);
        PutMessage(type + " " + card_number + " " + expired_date + " "
                + user_name);
        // PutMessage("Card Type : " + type);
        // PutMessage("Card Number :  " + card_number);
        // PutMessage("Expired Date :  " + expired_date);
        // PutMessage("Name : " + user_name);
    }

    public static String changeHexString2CharString(String e) {
        String char_txt = "";
        for (int i = 0; i < e.length(); i = i + 2) {
            String c = e.substring(i, i + 2);
            char j = (char) Integer.parseInt(c, 16);
            char_txt += j;
        }
        return char_txt;
    }

    // detect insert
    public void Send_Insert() {
        BtnEnabled(false);
        PutMessage("Detect Insert");
        sComboReader.DetectICCInsertedOrNot(null);
    }

    // Disp Insert
    public void DispValueInsert(SComboReader_DUKPT.ArrayByteValue insertString) {

        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        for (int i = 0; i < insertString.Value.length; i++) {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(String.format("%02x", insertString.Value[i]));
        }

        if (sb.toString().equalsIgnoreCase("00")) {
            txtvalues.setText("IC Card doesn't insert");
        }

        else if (sb.toString().equalsIgnoreCase("01")) {
            txtvalues.setText("IC Card insert");
        }

    }

    //get reader version
    public void Send_GetVersion()
    {
        //message call back handle
        sComboReader.RegisterMessage(myMessageHandler);
        PutMessage(">>>Get Version...");
        sComboReader.GetVersion(null);
    }

    //send emv data
    public void Send_EMV_Data(){
        BtnEnabled(false);
        PutMessage("get EMV Data");
        sComboReader.GetEMVData(null);
    }
    //display emv data
    // Display Receive data
    public void DispValue(SComboReader_DUKPT.ArrayByteValue dataString)
    {

        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        for (int i = 0; i < dataString.Value.length; i++)
        {
            if (sb.length() > 0)
                sb.append(" ");
            sb.append(String.format("%02x", dataString.Value[i]));
        }

        txtvalues.setText(sb.toString());
    }

    // /////////////////////////////////////////////////////

    public void onDestroy() {
        unregisterReceiver(mHeadsetReceiver);
        super.onDestroy();
    }

}
