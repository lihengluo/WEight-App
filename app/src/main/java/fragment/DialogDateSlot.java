package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DialogDateSlot extends AppCompatDialog {
    // 外部入参
    final Context context;
    String dateSta;
    String dateEnd;
    final InteDateSlot inteDateSlot;
    // ui
    private TextView tvDateSlotSta;
    private TextView tvDateSlotEnd;
    private Button btDateSlotEnt;
    // 当前日期常量
    final Date dateCur = new Date();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);


    public DialogDateSlot(@NonNull Context context, @NonNull String dateSta,
                          @NonNull String dateEnd, @NonNull InteDateSlot inteDateSlot) {
        super(context, R.style.StyleDialogDateSlot);
        setContentView(R.layout.dialog_date_slot);
        //
        this.context = context;
        this.dateSta = dateSta;
        this.dateEnd = dateEnd;
        this.inteDateSlot = inteDateSlot;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init
        tvDateSlotSta = findViewById(R.id.tvDateSlotSta);
        tvDateSlotEnd = findViewById(R.id.tvDateSlotEnd);
        btDateSlotEnt = findViewById(R.id.btDateSlotEnt);
        // init value
        tvDateSlotSta.setText(dateSta);
        tvDateSlotEnd.setText(dateEnd);
        // init listener
        tvDateSlotSta.setOnClickListener(mClickDateSta);
        tvDateSlotEnd.setOnClickListener(mClickDateEnd);
        btDateSlotEnt.setOnClickListener(mClickBtEnt);
    }

    // 点击选择开始时间
    private View.OnClickListener mClickDateSta = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int tempY = Integer.parseInt(dateSta.substring(0, 4));
            final int tempM = Integer.parseInt(dateSta.substring(5, 7))-1;
            final int tempD = Integer.parseInt(dateSta.substring(8, 10));
            new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    // 转字符串日期格式
                    Calendar calendar= Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateSta = dateFormat.format(calendar.getTime());
                    tvDateSlotSta.setText(dateSta);
                }
            }, tempY, tempM, tempD).show();
        }
    };

    // 点击选择结束时间
    private View.OnClickListener mClickDateEnd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int tempY = Integer.parseInt(dateEnd.substring(0, 4));
            final int tempM = Integer.parseInt(dateEnd.substring(5, 7))-1;
            final int tempD = Integer.parseInt(dateEnd.substring(8, 10));
            new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    // 转字符串日期格式
                    Calendar calendar= Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateEnd = dateFormat.format(calendar.getTime());
                    tvDateSlotEnd.setText(dateEnd);
                }
            }, tempY, tempM, tempD).show();
        }
    };

    // 点击确认按钮
    private View.OnClickListener mClickBtEnt = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 校验通过，接口回调，并关闭对话框
            inteDateSlot.call(dateSta, dateEnd);
            dismiss();
        }
    };

}

