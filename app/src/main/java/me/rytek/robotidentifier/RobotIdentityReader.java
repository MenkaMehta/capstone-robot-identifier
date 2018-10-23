package me.rytek.robotidentifier;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RobotIdentityReader extends AppCompatActivity {

    private NFCHandler nfcHandler;
    private TextView TextView_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_identity_reader);

        nfcHandler = new NFCHandler(this, false);
        TextView_Name = (TextView)findViewById(R.id.tv_name);

        //DataOut.setText("");

    }

    @Override
    public void onPause()
    {
        nfcHandler.disableForegroundDispatch();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        //nfcHandler.enableForegroundDispatch(); IDK why it doesn't work, but the below does

        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
        IntentFilter[] intentFiltersArray = new IntentFilter[] {};
        String[][] techList = new String[][] { { android.nfc.tech.Ndef.class.getName() }, { android.nfc.tech.NdefFormatable.class.getName() } };
        NfcAdapter nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        nfcAdpt.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        //called when a new tag is read
        nfcHandler.handleNewRead(intent);
        System.out.println("read tag");
        try
        {
            TextView_Name.setText(nfcHandler.getTagText());
            findViewById(R.id.ImgView_harold).setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "read error", Toast.LENGTH_SHORT).show();
            //System.out.println(e);
        }
    }
}
