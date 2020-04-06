package com.k3kc.papercalc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "ZZZAAA";

    EditText mPeopleCount;
    LinearLayout mUsedPaper;
    EditText mPaperLength;
    TextView mPaperResult;

    class UI {
        private List<EditText> mEditTextArray;
        private List<TextView> mLabels;
        private LinearLayout mUsedPaper;
        private Context mContext;
        private Runnable mCalculateCb;

        public UI(LinearLayout parentLayout, Context context, Runnable calculateCb) {
            mUsedPaper = parentLayout;
            mContext = context;
            mCalculateCb = calculateCb;
        }


        public void AddPaperUsers(int paperUsersCount) {
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(mEditTextArray != null && mLabels != null) {
                for (TextView tv : mLabels)
                    mUsedPaper.removeView(tv);

                for (EditText et : mEditTextArray)
                    mUsedPaper.removeView(et);
            }

            mEditTextArray = new ArrayList<EditText>();
            mLabels = new ArrayList<TextView>();

            for(int i = 0; i < paperUsersCount; i++) {
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(lparams);
                tv.setText(getString(R.string.people_n, i + 1));
                mUsedPaper.addView(tv);

                final EditText et = new EditText(mContext);
                et.setLayoutParams(lparams);
                et.setText("60");
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Editable text =  et.getText();
                        try {
                            int items = Integer.parseInt(text.toString());
                            if(items >= 10 && items <= 100) {
                                mCalculateCb.run();
                            } else {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            Toast.makeText(getApplicationContext(), "Ожидалось число от 10 до 100", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                mUsedPaper.addView(et);

                // save
                mLabels.add(tv);
                mEditTextArray.add(et);
            }
            mCalculateCb.run();
        }

        public int GetTotalPaperUsageByDay() {
            int retVal = 0;

            if(mEditTextArray != null) {
                for (EditText et : mEditTextArray) {
                    try {
                        Editable text = et.getText();
                        retVal += Integer.parseInt(text.toString());
                    } catch (NumberFormatException ex) {
                        Toast.makeText(getApplicationContext(), "Ожидалось число от 10 до 1000", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return retVal;
        }

    }

    private UI mUi;

    private int getPaperLength() {
        int retVal = 0;
        try {
            Editable text = mPaperLength.getText();
            retVal = Integer.parseInt(text.toString());
            if(!(retVal >= 10 && retVal <= 100))
                throw new NumberFormatException();

        } catch (NumberFormatException ex) {
            Toast.makeText(getApplicationContext(), "Ожидалось число от 10 до 100", Toast.LENGTH_SHORT).show();
        }
        return retVal;
    }

    private void calculateResults() {
        // paper length used by all people
        int totalPaperUsage = mUi.GetTotalPaperUsageByDay();
        int paperLength = getPaperLength();
        if(totalPaperUsage == 0 || paperLength == 0)
            return;
        Log.d(MainActivity.TAG, totalPaperUsage + " " + paperLength);
        mPaperResult.setText(getString(R.string.paper_result, (paperLength * 100) / totalPaperUsage));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsedPaper = (LinearLayout)findViewById(R.id.usedPaper);
        mPeopleCount = (EditText)findViewById(R.id.peopleCount);
        mPaperLength = (EditText)findViewById(R.id.paperLength);
        mPaperResult = (TextView) findViewById(R.id.results);

        final Runnable calculateCallback = new Runnable() {
            @Override
            public void run() {
                calculateResults();
            }
        };

        mUi = new UI(mUsedPaper, this, calculateCallback);
        mUi.AddPaperUsers(1);

        mPaperLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateResults();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPeopleCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable text =  mPeopleCount.getText();
                try {
                    int items = Integer.parseInt(text.toString());
                    if(items >= 1 && items <= 10) {
                        mUi.AddPaperUsers(items);
                    } else {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    Toast.makeText(getApplicationContext(), "Ожидалось число от 1 до 10", Toast.LENGTH_SHORT).show();
                }
                //calculateResults();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("ZZZAAA", "TEXT WAS CHANGED");
            }
        });

    }

}
