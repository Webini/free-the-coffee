package fr.webini.nico.freethecoffee;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import fr.webini.nico.freethecoffee.exceptions.UnrecognizedTagException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private NfcAdapter adapter;
    private CoffeeTag currentTag = null;
    private Button editButton;
    private TextView noTagText;
    private EditText amountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNfc();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(adapter != null) {
            adapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adapter != null) {
            adapter.disableForegroundDispatch(this);
        }
    }

    private void initNfc() {
        adapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void initView() {
        editButton = findViewById(R.id.editButton);
        noTagText = findViewById(R.id.noTagText);
        amountInput = findViewById(R.id.amountInput);

        editButton.setOnClickListener(view -> onSetAmount());
    }

    private void removeCurrentTag() {
        currentTag = null;
        noTagText.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        amountInput.setVisibility(View.INVISIBLE);
    }

    private void setCurrentTag(CoffeeTag tag) throws IOException {
        currentTag = tag;

        noTagText.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.VISIBLE);
        amountInput.setVisibility(View.VISIBLE);

        Integer amount = tag.getAmount();
        amountInput.setText(String.valueOf((float)amount / 100));
    }

    private void onSetAmount() {
        if (currentTag == null) {
            Toast.makeText(this, getString(R.string.no_tag), Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = (int)(Float.parseFloat(amountInput.getText().toString()) * 100);
        if (amount > 0xFFFF) {
            Toast.makeText(this, getString(R.string.amount_max), Toast.LENGTH_SHORT).show();
            return;
        } else if(amount < 0) {
            Toast.makeText(this, getString(R.string.amount_min), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentTag.setAmount(amount);
            currentTag.close();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.io_error), Toast.LENGTH_SHORT).show();
        } finally {
            this.removeCurrentTag();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "onNewIntent " + intent.getAction());

        this.removeCurrentTag();

        if (tag == null) {
            return;
        }

        String[] techList = tag.getTechList();
        Boolean found = false;
        for (Integer i = 0; i < techList.length; i++) {
            if (techList[i] == MifareClassic.class.getName()) {
                found = true;
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, getString(R.string.unsupported_tag), Toast.LENGTH_SHORT).show();
            return;
        }

        String id = Tools.toString(tag.getId());
        //Toast.makeText(this, getString(R.string.tag_detected) + " " + id, Toast.LENGTH_SHORT).show();
        MifareClassic mfTag = MifareClassic.get(tag);

        try {
            this.setCurrentTag(CoffeeTag.get(mfTag));
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.io_error) + " " + id, Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.getMessage(), e);
        } catch (UnrecognizedTagException e) {
            Toast.makeText(this, getString(R.string.invalid_tag) + " " + id, Toast.LENGTH_SHORT).show();
        }
    }
}
