package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;


public class PrivateDialog {

    private static PrivateDialog instace;

    public PrivateDialog() {

    }

    public static PrivateDialog getInstace() {

        if (instace == null) {
            synchronized (PrivateDialog.class) {
                if (instace == null) {
                    instace = new PrivateDialog();
                }
            }
        }
        return instace;
    }


    private String title = "温馨提示";
    private String message;
    private String sure;
    private String cancle;


    private Dialog tipDialog;

    private float clickTiem = 0;


    /**
     * desc: 提示隐私协议框
     */
    public void showConnectDialog(Context mContext) {
        dismiss();
        if ((SystemClock.elapsedRealtime() - clickTiem) < 500) {
            return;
        }
        tipDialog = new AlertDialog.Builder(mContext).create();

        tipDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_privates_dialog, null, false);
        TextView tvTitle = view.findViewById(R.id.tv_sava_dialog_title);
        TextView dialogTxt = view.findViewById(R.id.tv_sava_dialog_message);
        TextView tvcancle = view.findViewById(R.id.tv_sava_dialog_cancel);
        TextView tvsure = view.findViewById(R.id.tv_sava_dialog_confirg);
        SpannableStringBuilder tvProtocol = new SpannableStringBuilder(dialogTxt.getText().toString());
        tvProtocol.setSpan(new CliclSpan(mContext,1),21,27, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        dialogTxt.setText(tvProtocol);
        dialogTxt.setMovementMethod(LinkMovementMethod.getInstance());


        if (!this.title.isEmpty()) {
            tvTitle.setText(this.title);
        }
        if (!this.cancle.isEmpty()) {
            tvcancle.setText(this.cancle);
        }
        if (!this.sure.isEmpty()) {
            tvsure.setText(this.sure);
        }


        view.findViewById(R.id.tv_sava_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTiem = SystemClock.elapsedRealtime();
                tipDialog.dismiss();
                if (listener != null) {
                    listener.cancleClick();
                }
            }
        });

        tvsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                if (listener != null) {
                    listener.sureClick();
                }

            }
        });
        tipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH)
                {
                    return true;
                }
                else
                {
                    return false; //默认返回 false
                }
            }
        });
        tipDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        tipDialog.show();
        tipDialog.setCancelable(false);
        tipDialog.getWindow().setContentView(view);
        //tipDialog.getWindow().setWindowAnimations(R.style.DialogBottom); // 添加动画

        WindowManager windowManager = (WindowManager)
                mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams params = tipDialog.getWindow().getAttributes();
        params.width = (int) (display.getWidth() * 0.8);
        tipDialog.getWindow().setAttributes(params);

    }

    public void dismiss() {
        if (tipDialog != null) {
            if (tipDialog.isShowing()) {
                tipDialog.dismiss();
            }
            tipDialog = null;
        }
    }

    public interface OnTipItemClickListener {
        void cancleClick();

        void sureClick();

        void termsClick();
    }

    private OnTipItemClickListener listener;

    public PrivateDialog setOnTipItemClickListener(OnTipItemClickListener listener) {
        this.listener = listener;
        return this;
    }


    public PrivateDialog title(String title) {
        this.title = title;
        return this;
    }

    public PrivateDialog message(String message) {
        this.message = message;
        return this;
    }

    public PrivateDialog sure(String sure) {
        this.sure = sure;
        return this;
    }

    public PrivateDialog cancle(String cancle) {
        this.cancle = cancle;
        return this;
    }


    public void create(Context mContext) {
        showConnectDialog(mContext);

    }
    class CliclSpan extends ClickableSpan {
        Context mContext;
        Integer mNum;
        public CliclSpan() {
            super();
        }
        public CliclSpan(Context context,Integer num) {
            mContext = context;
            mNum = num;
        }
        @Override
        public void onClick(@NonNull View widget) {
            if (listener != null) {
                listener.termsClick();
            }
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(ContextCompat.getColor(mContext,R.color.blue));
            ds.setUnderlineText(false);
        }
    }

}

