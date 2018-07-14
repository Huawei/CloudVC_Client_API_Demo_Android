package com.huawei.opensdk.ec_sdk_demo.ui.conference;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.huawei.opensdk.demoservice.ConfConstant;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.adapter.CreateConfAdapter;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfCreateContract;
import com.huawei.opensdk.ec_sdk_demo.logic.conference.mvp.ConfCreatePresenter;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.util.CommonUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.EditDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.ThreeInputDialog;
import com.huawei.opensdk.ec_sdk_demo.widget.TripleDialog;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class CreateConfActivity extends BaseActivity implements View.OnClickListener, ConfCreateContract.ConfCreateView
{

    private ConfCreatePresenter mPresenter;
    private EditText subjectET;
    private RelativeLayout confTimeRL;
    private RelativeLayout confTypeRL;
    private RelativeLayout accessNumberRL;
    private RelativeLayout passwordRL;
    private TextView accessNumberTV;
    private ListView listView;
    private Button addMemberBtn;
    private TextView rightTV;
    private ImageButton clearSubjectBtn;
    private TextView startTimeText;
    private TextView confTypeText;
    private DateEntity dateEntity;
    private LinearLayout rightButtonLL;

    private CreateConfAdapter adapter;

    private String confPwd = "";

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.conference_create_layout);
        rightTV = (TextView) findViewById(R.id.right_text);
        subjectET = (EditText) findViewById(R.id.conf_subject_et);
        confTimeRL = (RelativeLayout) findViewById(R.id.conference_time_view);
        confTypeRL = (RelativeLayout) findViewById(R.id.rl_conference_type);
        findViewById(R.id.conference_pwd_view);
        accessNumberTV = (TextView) findViewById(R.id.conference_members_number);
        listView = (ListView) findViewById(R.id.member_list);
        addMemberBtn = (Button) findViewById(R.id.add_member_btn);
        clearSubjectBtn = (ImageButton) findViewById(R.id.meeting_clear_subject);
        startTimeText = (TextView) findViewById(R.id.conf_create_time);
        confTypeText = (TextView) findViewById(R.id.tv_conference_type);
        accessNumberRL = (RelativeLayout) findViewById(R.id.conference_end_time_view);
        passwordRL = (RelativeLayout) findViewById(R.id.conference_pwd_view);
        rightButtonLL = (LinearLayout) findViewById(R.id.right_img_layout);


        rightTV.setText(R.string.create_conf);
        rightButtonLL.setOnClickListener(this);
        confTypeRL.setOnClickListener(this);
        confTimeRL.setOnClickListener(this);
        confTimeRL.setVisibility(View.GONE);
        addMemberBtn.setOnClickListener(this);
        clearSubjectBtn.setOnClickListener(this);
        accessNumberRL.setOnClickListener(this);
        passwordRL.setOnClickListener(this);

        listView.setAdapter(adapter);

        //Set default subject
        String defaultSubject = LoginMgr.getInstance().getAccount() + "'s Meeting";
        mPresenter.setSubject(defaultSubject);
        mPresenter.setMediaType(ConfConstant.ConfMediaType.VOICE_CONF);

        //Join the meeting as chairman
        if (null != LoginCenter.getInstance().getSipAccountInfo().getTerminal()){
            Member chairman = new Member();
            chairman.setNumber(LoginCenter.getInstance().getSipAccountInfo().getTerminal());
            chairman.setAccountId(LoginCenter.getInstance().getAccount());
            chairman.setRole(ConfConstant.ConfRole.CHAIRMAN);

            //Other fields are optional, and can be filled according to need

            mPresenter.addMember(chairman);
        }


        subjectET.setText(defaultSubject);
        accessNumberTV.setText(LoginMgr.getInstance().getSipNumber());
    }

    @Override
    public void initializeData()
    {
        mPresenter = new ConfCreatePresenter(this);
        adapter = new CreateConfAdapter(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.meeting_clear_subject:
                subjectET.setText("");
                break;
            case R.id.conference_time_view:
                showDatePicker();
                break;
            case R.id.rl_conference_type:
                showTypePicker();
                break;
            case R.id.right_img_layout:
                mPresenter.setSubject(subjectET.getText().toString());
                mPresenter.setPassWorld(confPwd);
                mPresenter.createConference();
                finish();
                break;
            case R.id.add_member_btn:
                showAddMemberDialog();
                break;
            case R.id.conference_end_time_view:
                showAccessNumberDialog();
                break;
            case R.id.conference_pwd_view:
                showConfPwdDialog();
                break;
            default:
                break;
        }
    }

    private void showAccessNumberDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.input_access_number);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(CreateConfActivity.this);
                if (TextUtils.isEmpty(dialog.getText()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }
                mPresenter.updateAccessNumber(dialog.getText());
            }
        });
        dialog.show();
    }

    private void showConfPwdDialog()
    {
        final EditDialog dialog = new EditDialog(this, R.string.password_code_input);
        dialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtil.hideSoftInput(CreateConfActivity.this);
                confPwd = dialog.getText().toString();
            }
        });
        dialog.show();
    }

    private void showTypePicker()
    {
        TripleDialog typePickerDialog = new TripleDialog(this);
        typePickerDialog.setLeftText(R.string.conference_voice);
        typePickerDialog.setRightText(R.string.conference_video);
        typePickerDialog.setThirdText(R.string.conference_video_data);
        typePickerDialog.setLeftButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setMediaType(ConfConstant.ConfMediaType.VOICE_CONF);
                updateTypeView(ConfConstant.ConfMediaType.VOICE_CONF);
            }
        });
        typePickerDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    mPresenter.setMediaType(ConfConstant.ConfMediaType.VIDEO_CONF);
                    updateTypeView(ConfConstant.ConfMediaType.VIDEO_CONF);
            }
        });
        typePickerDialog.setThirdButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.setMediaType(ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF);
                updateTypeView(ConfConstant.ConfMediaType.VIDEO_AND_DATA_CONF);
            }
        });
        typePickerDialog.show();
    }

    private void updateTypeView(ConfConstant.ConfMediaType type)
    {
        switch (type)
        {
            case VOICE_CONF:
                confTypeText.setText(R.string.conference_voice);
                break;
            case VIDEO_CONF:
                confTypeText.setText(R.string.conference_video);
                break;
//            case VOICE_AND_DATA_CONF:
//                confTypeText.setText(R.string.conference_voice_data);
//                break;
            case VIDEO_AND_DATA_CONF:
                confTypeText.setText(R.string.conference_video_data);
                break;
            default:
                break;
        }
    }

    private void showDatePicker()
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                dateEntity = new DateEntity();
                dateEntity.setYear(year);
                dateEntity.setMonth(monthOfYear);
                dateEntity.setDay(dayOfMonth);
                showTimePicker();
            }
        }, gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void showTimePicker()
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                if (dateEntity != null)
                {
                    dateEntity.setHour(hourOfDay);
                    dateEntity.setMin(minute);
                    updateTime();
                }
            }
        }, gregorianCalendar.get(Calendar.HOUR_OF_DAY), gregorianCalendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void updateTime()
    {
        Date date = new Date();
        date.setYear(dateEntity.getYear() - 1900);
        date.setMonth(dateEntity.getMonth());
        date.setDate(dateEntity.getDay());
        date.setHours(dateEntity.getHour());
        date.setMinutes(dateEntity.getMin());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatStr = dateFormat.format(date);
        mPresenter.setStartTime(formatStr);
        mPresenter.setBookType(false);
        mPresenter.setDuration(120);
        startTimeText.setText(formatStr);
    }

    private void showAddMemberDialog()
    {
        final ThreeInputDialog addMemberDialog = new ThreeInputDialog(this);
        addMemberDialog.setRightButtonListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //The number of participants required, others are optional
                //If invited users need to query through conference list to conference, accountId must fill in
                if (TextUtils.isEmpty(addMemberDialog.getInput1()))
                {
                    showToast(R.string.invalid_number);
                    return;
                }

                Member attendee = new Member();
                attendee.setNumber(addMemberDialog.getInput1());
                attendee.setDisplayName(addMemberDialog.getInput2());
                attendee.setAccountId(addMemberDialog.getInput3());
                
                attendee.setRole(ConfConstant.ConfRole.ATTENDEE);
                //Other fields are optional, and can be filled according to need

                mPresenter.addMember(attendee);
            }
        });
        addMemberDialog.setHint1(R.string.input_number);
        addMemberDialog.setHint2(R.string.input_name);
        addMemberDialog.setHint3(R.string.input_account);
        addMemberDialog.show();
    }

    @Override
    public void refreshListView(List<Member> memberList)
    {
        adapter.setData(memberList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void createFailed()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(R.string.create_conf_fail);
                finish();
            }
        });

    }

    @Override
    public void createSuccess()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showToast(R.string.create_conf_success);
                finish();
            }
        });
    }


    @Override
    public void updateAccessNumber(String accessNumber)
    {
        accessNumberTV.setText(accessNumber);
    }

    @Override
    public void showLoading()
    {

    }

    @Override
    public void dismissLoading()
    {

    }

    @Override
    public void showCustomToast(final int resID)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showCustomToast(resID);
            }
        });
    }

    private static class DateEntity
    {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int min;

        public int getYear()
        {
            return year;
        }

        public void setYear(int year)
        {
            this.year = year;
        }

        public int getMonth()
        {
            return month;
        }

        public void setMonth(int month)
        {
            this.month = month;
        }

        public int getDay()
        {
            return day;
        }

        public void setDay(int day)
        {
            this.day = day;
        }

        public int getHour()
        {
            return hour;
        }

        public void setHour(int hour)
        {
            this.hour = hour;
        }

        public int getMin()
        {
            return min;
        }

        public void setMin(int min)
        {
            this.min = min;
        }
    }
}
