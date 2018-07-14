package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;

/**
 * This class is about editable dialog box
 * 可编辑对话框
 */
public class EditDialog extends BaseDialog {
    private EditText editText;
    private ImageView ivClearText;
    // private Context context;

    public EditDialog(Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_editable);
        setTitle(title);
        setLeftButtonListener(null);
        // this.context = context;
        editText = (EditText) findViewById(R.id.dialog_edittext);
        ivClearText = (ImageView) findViewById(R.id.ivClearText);
        setCanceledOnTouchOutside(false);
        ivClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                editText.setText("");
            }
        });
    }

    public EditDialog(Context context, int titleResId) {
        this(context, context.getString(titleResId));
    }

    public String getText() {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    public void setTextStyle(String initText, int maxLength, int inputType) {
        CommonUtil.processEditTextWithNumber(editText, initText, maxLength, inputType);
        initEditText(initText);
    }

    private void initEditText(String initText) {
        //根据初始文本，设置按钮初始状态
        if (TextUtils.isEmpty(initText)) {
            getRightButton().setTextColor(LocContext.getResources()
                    .getColor(R.color.textThirdly));
            getRightButton().setEnabled(false);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    getRightButton().setTextColor(LocContext.getResources()
                            .getColor(R.color.textThirdly));
                    getRightButton().setEnabled(false);
                    ivClearText.setVisibility(View.GONE);

                } else {
                    getRightButton().setTextColor(LocContext.getResources()
                            .getColor(R.color.dialog_button_color));
                    getRightButton().setEnabled(true);
                    ivClearText.setVisibility(View.VISIBLE);
                }
            }

        });
    }
}
