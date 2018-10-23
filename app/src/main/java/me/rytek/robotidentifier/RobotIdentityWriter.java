package me.rytek.robotidentifier;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RobotIdentityWriter extends AppCompatActivity {

    private NFCHandler nfcHandler;
    private NdefMessage message = null;

    EditText NameText;

    TextView StatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_identity_writer);
        nfcHandler = new NFCHandler(this, true);

        NameText = (EditText)findViewById(R.id.EditText_Name);
        StatusText = (TextView)findViewById(R.id.Status);
    }

    public void WriteTag_onClick(View v)
    {
        //writes in UTF-8
        String name = NameText.getText().toString();

        //DataOutText.setText(name);

        message = nfcHandler.getNdefMessageFromText(name);


        StatusText.setText("Waiting for Tag to be scanned");
        StatusText.setTextColor(Color.RED);

        Toast.makeText(this, "Entering write mode", Toast.LENGTH_SHORT).show();


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

    protected void onNewIntent(Intent intent)
    {
        //called when a new tag is read.
        //Writes the held message when a tag is scanned
        if (message==null)
        {
            Toast.makeText(this, "Message was not defined, did not write", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Writing to Robot", Toast.LENGTH_SHORT).show();
        Tag currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (message != null) {
            nfcHandler.writeTag(currentTag, message);
        }

        StatusText.setText("Assigned to Robot");
        StatusText.setTextColor(Color.GREEN);
    }
}
