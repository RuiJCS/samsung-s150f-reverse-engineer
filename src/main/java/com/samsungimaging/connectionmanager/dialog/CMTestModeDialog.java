package com.samsungimaging.connectionmanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.samsungimaging.connectionmanager.R;
import com.samsungimaging.connectionmanager.app.cm.common.CMConstants;
import com.samsungimaging.connectionmanager.app.cm.common.CMSharedPreferenceUtil;
import com.samsungimaging.connectionmanager.app.cm.common.CMUtil;
import com.samsungimaging.connectionmanager.util.Trace;
import java.util.ArrayList;
import java.util.List;
import org.cybergarage.soap.SOAP;

/* JADX INFO: loaded from: classes.dex */
public class CMTestModeDialog extends Dialog {
    private ArrayList<String> mArrayList_testmode_forSpinner;
    private ArrayList<String> mArrayList_testmode_forSpinner_2;
    private ArrayList<String> mArrayList_testmode_forSpinner_3;
    Context mContext;
    EditText met_mac01;
    EditText met_mac01_2;
    EditText met_mac01_3;
    EditText met_mac02;
    EditText met_mac02_2;
    EditText met_mac02_3;
    EditText met_mac03;
    EditText met_mac03_2;
    EditText met_mac03_3;
    EditText met_model;
    EditText met_model_2;
    EditText met_model_3;
    TextView mtv_ssid;
    TextView mtv_ssid_2;
    TextView mtv_ssid_3;
    private String selected_item;
    private String selected_item_2;
    private String selected_item_3;
    TextWatcher watcher;
    TextWatcher watcher_2;
    TextWatcher watcher_3;

    public CMTestModeDialog(Context context, int style) {
        super(context, style);
        this.mContext = null;
        this.selected_item = "AP_SSC_";
        this.selected_item_2 = "AP_SSC_";
        this.selected_item_3 = "AP_SSC_";
        this.watcher = new TextWatcher() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CMTestModeDialog.this.setSSID();
            }
        };
        this.watcher_2 = new TextWatcher() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CMTestModeDialog.this.setSSID2();
            }
        };
        this.watcher_3 = new TextWatcher() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.3
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CMTestModeDialog.this.setSSID3();
            }
        };
        this.mContext = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.cm_dialog_testmode);
        this.mtv_ssid = (TextView) findViewById(R.id.tv_cm_testmode_ssid);
        this.met_model = (EditText) findViewById(R.id.et_cm_testmode_modelname);
        this.met_model.addTextChangedListener(this.watcher);
        this.met_mac01 = (EditText) findViewById(R.id.et_cm_testmode_mac01);
        this.met_mac01.addTextChangedListener(this.watcher);
        this.met_mac02 = (EditText) findViewById(R.id.et_cm_testmode_mac02);
        this.met_mac02.addTextChangedListener(this.watcher);
        this.met_mac03 = (EditText) findViewById(R.id.et_cm_testmode_mac03);
        this.met_mac03.addTextChangedListener(this.watcher);
        this.mtv_ssid_2 = (TextView) findViewById(R.id.tv_cm_testmode_ssid_2);
        this.met_model_2 = (EditText) findViewById(R.id.et_cm_testmode_modelname_2);
        this.met_model_2.addTextChangedListener(this.watcher_2);
        this.met_mac01_2 = (EditText) findViewById(R.id.et_cm_testmode_mac01_2);
        this.met_mac01_2.addTextChangedListener(this.watcher_2);
        this.met_mac02_2 = (EditText) findViewById(R.id.et_cm_testmode_mac02_2);
        this.met_mac02_2.addTextChangedListener(this.watcher_2);
        this.met_mac03_2 = (EditText) findViewById(R.id.et_cm_testmode_mac03_2);
        this.met_mac03_2.addTextChangedListener(this.watcher_2);
        this.mtv_ssid_3 = (TextView) findViewById(R.id.tv_cm_testmode_ssid_3);
        this.met_model_3 = (EditText) findViewById(R.id.et_cm_testmode_modelname_3);
        this.met_model_3.addTextChangedListener(this.watcher_3);
        this.met_mac01_3 = (EditText) findViewById(R.id.et_cm_testmode_mac01_3);
        this.met_mac01_3.addTextChangedListener(this.watcher_3);
        this.met_mac02_3 = (EditText) findViewById(R.id.et_cm_testmode_mac02_3);
        this.met_mac02_3.addTextChangedListener(this.watcher_3);
        this.met_mac03_3 = (EditText) findViewById(R.id.et_cm_testmode_mac03_3);
        this.met_mac03_3.addTextChangedListener(this.watcher_3);
        enableEditTextArea(true);
        enableEditTextArea2(true);
        enableEditTextArea3(true);
        this.mArrayList_testmode_forSpinner = new ArrayList<>();
        this.mArrayList_testmode_forSpinner.add("AP_SSC_");
        this.mArrayList_testmode_forSpinner.add("EK_");
        this.mArrayList_testmode_forSpinner.add("SM_");
        this.mArrayList_testmode_forSpinner.add("GC_");
        this.mArrayList_testmode_forSpinner.add("NONE");
        ArrayAdapter<String> adapter_forSpinner = new ArrayAdapter<>(this.mContext, android.R.layout.simple_spinner_item, this.mArrayList_testmode_forSpinner);
        adapter_forSpinner.setDropDownViewResource(R.layout.cm_dialog_testmode_items);
        Spinner sp = (Spinner) findViewById(R.id.spinner_cm_testmode);
        sp.setAdapter((SpinnerAdapter) adapter_forSpinner);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                CMTestModeDialog.this.selected_item = String.valueOf(CMTestModeDialog.this.mArrayList_testmode_forSpinner.get(arg2));
                if (CMTestModeDialog.this.selected_item.equals("NONE")) {
                    CMTestModeDialog.this.enableEditTextArea(false);
                } else {
                    CMTestModeDialog.this.enableEditTextArea(true);
                }
                CMTestModeDialog.this.setSSID();
                Trace.d(CMConstants.TAG_NAME, "arg2 = " + arg2 + ", selected_item = " + CMTestModeDialog.this.selected_item);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> arg0) {
                Trace.d(CMConstants.TAG_NAME, "onNothingSelected.");
            }
        });
        this.mArrayList_testmode_forSpinner_2 = new ArrayList<>();
        this.mArrayList_testmode_forSpinner_2.add("AP_SSC_");
        this.mArrayList_testmode_forSpinner_2.add("EK_");
        this.mArrayList_testmode_forSpinner_2.add("SM_");
        this.mArrayList_testmode_forSpinner_2.add("GC_");
        this.mArrayList_testmode_forSpinner_2.add("NONE");
        ArrayAdapter<String> adapter_forSpinner_2 = new ArrayAdapter<>(this.mContext, android.R.layout.simple_spinner_item, this.mArrayList_testmode_forSpinner_2);
        adapter_forSpinner_2.setDropDownViewResource(R.layout.cm_dialog_testmode_items);
        Spinner sp_2 = (Spinner) findViewById(R.id.spinner_cm_testmode_2);
        sp_2.setAdapter((SpinnerAdapter) adapter_forSpinner_2);
        sp_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.5
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                CMTestModeDialog.this.selected_item_2 = String.valueOf(CMTestModeDialog.this.mArrayList_testmode_forSpinner_2.get(arg2));
                if (CMTestModeDialog.this.selected_item_2.equals("NONE")) {
                    CMTestModeDialog.this.enableEditTextArea2(false);
                } else {
                    CMTestModeDialog.this.enableEditTextArea2(true);
                }
                CMTestModeDialog.this.setSSID2();
                Trace.d(CMConstants.TAG_NAME, "arg2 = " + arg2 + ", selected_item_2 = " + CMTestModeDialog.this.selected_item_2);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> arg0) {
                Trace.d(CMConstants.TAG_NAME, "onNothingSelected.");
            }
        });
        this.mArrayList_testmode_forSpinner_3 = new ArrayList<>();
        this.mArrayList_testmode_forSpinner_3.add("AP_SSC_");
        this.mArrayList_testmode_forSpinner_3.add("EK_");
        this.mArrayList_testmode_forSpinner_3.add("SM_");
        this.mArrayList_testmode_forSpinner_3.add("GC_");
        this.mArrayList_testmode_forSpinner_3.add("NONE");
        ArrayAdapter<String> adapter_forSpinner_3 = new ArrayAdapter<>(this.mContext, android.R.layout.simple_spinner_item, this.mArrayList_testmode_forSpinner_3);
        adapter_forSpinner_3.setDropDownViewResource(R.layout.cm_dialog_testmode_items);
        Spinner sp_3 = (Spinner) findViewById(R.id.spinner_cm_testmode_3);
        sp_3.setAdapter((SpinnerAdapter) adapter_forSpinner_3);
        sp_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.6
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                CMTestModeDialog.this.selected_item_3 = String.valueOf(CMTestModeDialog.this.mArrayList_testmode_forSpinner_3.get(arg2));
                if (CMTestModeDialog.this.selected_item_3.equals("NONE")) {
                    CMTestModeDialog.this.enableEditTextArea3(false);
                } else {
                    CMTestModeDialog.this.enableEditTextArea3(true);
                }
                CMTestModeDialog.this.setSSID3();
                Trace.d(CMConstants.TAG_NAME, "arg2 = " + arg2 + ", selected_item_3 = " + CMTestModeDialog.this.selected_item_3);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> arg0) {
                Trace.d(CMConstants.TAG_NAME, "onNothingSelected.");
            }
        });
        this.met_model.addTextChangedListener(this.watcher);
        this.met_model_2.addTextChangedListener(this.watcher_2);
        this.met_model_3.addTextChangedListener(this.watcher_3);
        Button btnConfirm = (Button) findViewById(R.id.btn_cm_testmode_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() { // from class: com.samsungimaging.connectionmanager.dialog.CMTestModeDialog.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                String ssid_temp = CMTestModeDialog.this.mtv_ssid.getText().toString();
                if (ssid_temp.length() != 0 && ssid_temp.equals("NONE")) {
                    ssid_temp = "";
                }
                String ssid_temp_2 = CMTestModeDialog.this.mtv_ssid_2.getText().toString();
                if (ssid_temp_2.length() != 0 && ssid_temp_2.equals("NONE")) {
                    ssid_temp_2 = "";
                }
                String ssid_temp_3 = CMTestModeDialog.this.mtv_ssid_3.getText().toString();
                if (ssid_temp_3.length() != 0 && ssid_temp_3.equals("NONE")) {
                    ssid_temp_3 = "";
                }
                Trace.d(CMConstants.TAG_NAME, "test mode Dialog, ssid_temp = " + ssid_temp);
                Trace.d(CMConstants.TAG_NAME, "test mode Dialog, ssid_temp_2 = " + ssid_temp_2);
                Trace.d(CMConstants.TAG_NAME, "test mode Dialog, ssid_temp_3 = " + ssid_temp_3);
                List<String> list = new ArrayList<>();
                list.add(ssid_temp);
                list.add(ssid_temp_2);
                list.add(ssid_temp_3);
                String title = "";
                if ("true".equalsIgnoreCase(CMSharedPreferenceUtil.getString(CMTestModeDialog.this.mContext, CMConstants.TESTMODE, "false"))) {
                    for (int i = 0; i < list.size(); i++) {
                        String temp = list.get(i);
                        if (temp.length() != 0 && !temp.equals("NONE")) {
                            title = String.valueOf(title) + "(" + temp + ")";
                        }
                    }
                    Trace.d(CMConstants.TAG_NAME, "test mode Dialog, title = " + title);
                }
                CMSharedPreferenceUtil.put(CMTestModeDialog.this.mContext, CMConstants.TESTMODE_SSID, ssid_temp);
                CMSharedPreferenceUtil.put(CMTestModeDialog.this.mContext, CMConstants.TESTMODE_SSID2, ssid_temp_2);
                CMSharedPreferenceUtil.put(CMTestModeDialog.this.mContext, CMConstants.TESTMODE_SSID3, ssid_temp_3);
                CMUtil.setdscPrefix_fortest(CMTestModeDialog.this.mContext, list);
                CMTestModeDialog.this.dismiss();
            }
        });
        String ssid_temp = CMSharedPreferenceUtil.getString(this.mContext, CMConstants.TESTMODE_SSID, "");
        if (ssid_temp.length() > 0) {
            if (ssid_temp.startsWith(this.mArrayList_testmode_forSpinner.get(0))) {
                sp.setSelection(0);
            } else if (ssid_temp.startsWith(this.mArrayList_testmode_forSpinner.get(1))) {
                sp.setSelection(1);
            } else if (ssid_temp.startsWith(this.mArrayList_testmode_forSpinner.get(2))) {
                sp.setSelection(2);
            } else if (ssid_temp.startsWith(this.mArrayList_testmode_forSpinner.get(3))) {
                sp.setSelection(3);
            }
        } else {
            sp.setSelection(4);
        }
        String ssid_temp_2 = CMSharedPreferenceUtil.getString(this.mContext, CMConstants.TESTMODE_SSID2, "");
        if (ssid_temp_2.length() > 0) {
            if (ssid_temp_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(0))) {
                sp_2.setSelection(0);
            } else if (ssid_temp_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(1))) {
                sp_2.setSelection(1);
            } else if (ssid_temp_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(2))) {
                sp_2.setSelection(2);
            } else if (ssid_temp_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(3))) {
                sp_2.setSelection(3);
            }
        } else {
            sp_2.setSelection(4);
        }
        String ssid_temp_3 = CMSharedPreferenceUtil.getString(this.mContext, CMConstants.TESTMODE_SSID3, "");
        if (ssid_temp_3.length() > 0) {
            if (ssid_temp_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(0))) {
                sp_3.setSelection(0);
                return;
            }
            if (ssid_temp_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(1))) {
                sp_3.setSelection(1);
                return;
            } else if (ssid_temp_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(2))) {
                sp_3.setSelection(2);
                return;
            } else {
                if (ssid_temp_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(3))) {
                    sp_3.setSelection(3);
                    return;
                }
                return;
            }
        }
        sp_3.setSelection(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableEditTextArea(boolean enable) {
        if (enable) {
            this.met_model.setEnabled(true);
            this.met_mac01.setEnabled(true);
            this.met_mac02.setEnabled(true);
            this.met_mac03.setEnabled(true);
            return;
        }
        this.met_model.setEnabled(false);
        this.met_mac01.setEnabled(false);
        this.met_mac02.setEnabled(false);
        this.met_mac03.setEnabled(false);
        this.met_model.setText("");
        this.met_mac01.setText("");
        this.met_mac02.setText("");
        this.met_mac03.setText("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableEditTextArea2(boolean enable) {
        if (enable) {
            this.met_model_2.setEnabled(true);
            this.met_mac01_2.setEnabled(true);
            this.met_mac02_2.setEnabled(true);
            this.met_mac03_2.setEnabled(true);
            return;
        }
        this.met_model_2.setEnabled(false);
        this.met_mac01_2.setEnabled(false);
        this.met_mac02_2.setEnabled(false);
        this.met_mac03_2.setEnabled(false);
        this.met_model_2.setText("");
        this.met_mac01_2.setText("");
        this.met_mac02_2.setText("");
        this.met_mac03_2.setText("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableEditTextArea3(boolean enable) {
        if (enable) {
            this.met_model_3.setEnabled(true);
            this.met_mac01_3.setEnabled(true);
            this.met_mac02_3.setEnabled(true);
            this.met_mac03_3.setEnabled(true);
            return;
        }
        this.met_model_3.setEnabled(false);
        this.met_mac01_3.setEnabled(false);
        this.met_mac02_3.setEnabled(false);
        this.met_mac03_3.setEnabled(false);
        this.met_model_3.setText("");
        this.met_mac01_3.setText("");
        this.met_mac02_3.setText("");
        this.met_mac03_3.setText("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSSID() {
        String model = this.met_model.getText().toString();
        String mac01 = this.met_mac01.getText().toString();
        String mac02 = this.met_mac02.getText().toString();
        String mac03 = this.met_mac03.getText().toString();
        String ssid = this.selected_item;
        if (this.selected_item.startsWith(this.mArrayList_testmode_forSpinner.get(0))) {
            if (!model.equals("")) {
                ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "_0-";
                if (!mac01.equals("")) {
                    ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "_0-" + mac01.toUpperCase();
                    if (!mac02.equals("")) {
                        ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "_0-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase();
                        if (!mac03.equals("")) {
                            ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "_0-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase() + SOAP.DELIM + mac03.toUpperCase();
                        }
                    }
                }
            }
        } else if (this.selected_item.startsWith(this.mArrayList_testmode_forSpinner.get(1)) || this.selected_item.startsWith(this.mArrayList_testmode_forSpinner.get(2)) || this.selected_item.startsWith(this.mArrayList_testmode_forSpinner.get(3))) {
            if (!model.equals("")) {
                ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "-";
                if (!mac01.equals("")) {
                    ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "-" + mac01.toUpperCase();
                    if (!mac02.equals("")) {
                        ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase();
                        if (!mac03.equals("")) {
                            ssid = String.valueOf(this.selected_item) + model.toUpperCase() + "-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase() + SOAP.DELIM + mac03.toUpperCase();
                        }
                    }
                }
            }
        } else if (this.selected_item.startsWith(this.mArrayList_testmode_forSpinner.get(4))) {
            CMSharedPreferenceUtil.put(this.mContext, CMConstants.TESTMODE_SSID, "");
            ssid = this.selected_item;
        }
        this.mtv_ssid.setText(ssid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSSID2() {
        String model = this.met_model_2.getText().toString();
        String mac01 = this.met_mac01_2.getText().toString();
        String mac02 = this.met_mac02_2.getText().toString();
        String mac03 = this.met_mac03_2.getText().toString();
        String ssid = this.selected_item_2;
        if (this.selected_item_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(0))) {
            if (!model.equals("")) {
                ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "_0-";
                if (!mac01.equals("")) {
                    ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "_0-" + mac01.toUpperCase();
                    if (!mac02.equals("")) {
                        ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "_0-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase();
                        if (!mac03.equals("")) {
                            ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "_0-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase() + SOAP.DELIM + mac03.toUpperCase();
                        }
                    }
                }
            }
        } else if (this.selected_item_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(1)) || this.selected_item_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(2)) || this.selected_item_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(3))) {
            if (!model.equals("")) {
                ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "-";
                if (!mac01.equals("")) {
                    ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "-" + mac01.toUpperCase();
                    if (!mac02.equals("")) {
                        ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase();
                        if (!mac03.equals("")) {
                            ssid = String.valueOf(this.selected_item_2) + model.toUpperCase() + "-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase() + SOAP.DELIM + mac03.toUpperCase();
                        }
                    }
                }
            }
        } else if (this.selected_item_2.startsWith(this.mArrayList_testmode_forSpinner_2.get(4))) {
            CMSharedPreferenceUtil.put(this.mContext, CMConstants.TESTMODE_SSID2, "");
            ssid = this.selected_item_2;
        }
        this.mtv_ssid_2.setText(ssid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSSID3() {
        String model = this.met_model_3.getText().toString();
        String mac01 = this.met_mac01_3.getText().toString();
        String mac02 = this.met_mac02_3.getText().toString();
        String mac03 = this.met_mac03_3.getText().toString();
        String ssid = this.selected_item_3;
        if (this.selected_item_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(0))) {
            if (!model.equals("")) {
                ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "_0-";
                if (!mac01.equals("")) {
                    ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "_0-" + mac01.toUpperCase();
                    if (!mac02.equals("")) {
                        ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "_0-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase();
                        if (!mac03.equals("")) {
                            ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "_0-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase() + SOAP.DELIM + mac03.toUpperCase();
                        }
                    }
                }
            }
        } else if (this.selected_item_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(1)) || this.selected_item_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(2)) || this.selected_item_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(3))) {
            if (!model.equals("")) {
                ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "-";
                if (!mac01.equals("")) {
                    ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "-" + mac01.toUpperCase();
                    if (!mac02.equals("")) {
                        ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase();
                        if (!mac03.equals("")) {
                            ssid = String.valueOf(this.selected_item_3) + model.toUpperCase() + "-" + mac01.toUpperCase() + SOAP.DELIM + mac02.toUpperCase() + SOAP.DELIM + mac03.toUpperCase();
                        }
                    }
                }
            }
        } else if (this.selected_item_3.startsWith(this.mArrayList_testmode_forSpinner_3.get(4))) {
            CMSharedPreferenceUtil.put(this.mContext, CMConstants.TESTMODE_SSID3, "");
            ssid = this.selected_item_3;
        }
        this.mtv_ssid_3.setText(ssid);
    }
}
