package com.zcw.calculator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zcw.calculator.R;
import com.zcw.calculator.adapter.HistoryAdapter;
import com.zcw.calculator.bean.History;
import com.zcw.calculator.database.DBManager;
import com.zcw.calculator.operator.Operator;
import com.zcw.calculator.utils.Common;
import com.zcw.calculator.utils.Constant;
import com.zcw.calculator.view.CustomButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ButtonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ButtonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * 用于存放和显示表达式
     */
    private StringBuilder mExpression;
    private TextView mTvExpression;

    /**
     * 用于存放和显示结果
     */
    private String mResult;
    private TextView mTvResult;

    /**
     * 存放数字按钮
     */
    private CustomButton[] mBtnNumber;

    /**
     * 存放操作按钮
     */
    private CustomButton[] mBtnOperator;

    /**
     * 其他按钮操作
     */
    private CustomButton[] mBtnOther;

    /**
     * 算符优先算法
     */
    private Operator mOperator;

    /**
     * 记录计算器的状态
     */
    private State mState;

    /**
     * 计算器类型
     */
    private CalcType mType;

    /** 数据库管理类 */
    private DBManager dbManager;

    public ButtonFragment() {
        setShow(new StringBuilder(""), "");
        mState = State.INIT;
        mType = CalcType.STANDARD;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ButtonFragment.
     */
    public static ButtonFragment newInstance(String param1, String param2) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button, container, false);
        initWidget(view);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_0:
                buttonNumber0(v);
                break;
            case R.id.button_1:
            case R.id.button_2:
            case R.id.button_3:
            case R.id.button_4:
            case R.id.button_5:
            case R.id.button_6:
            case R.id.button_7:
            case R.id.button_8:
            case R.id.button_9:
                buttonNumber(v);
                break;

            // 小数点操作
            case R.id.button_point:
                buttonPoint(v);
                break;

            case R.id.button_add:
            case R.id.button_sub:
            case R.id.button_mul:
            case R.id.button_div:
                buttonOperator(v);
                break;

            // 复位按钮操作
            case R.id.button_clean:
                buttonReset();
                return;

            // 回退按钮操作
            case R.id.button_back:
                buttonBack();
                return;

            // “=”按钮操作
            case R.id.button_equal:
                buttonEqual(v);
                return;
        }

        mTvResult.setText(mExpression);
    }

    /**
     * 初始化
     */
    private void init() {
        mOperator = new Operator();
        dbManager = DBManager.getInstance(getActivity());
    }

    /**
     * 初始化Widget
     *
     * @param view
     */
    private void initWidget(View view) {
        mBtnNumber = new CustomButton[11];
        mBtnOperator = new CustomButton[4];
        mBtnOther = new CustomButton[3];

        mBtnNumber[0] = view.findViewById(R.id.button_0);
        mBtnNumber[1] = view.findViewById(R.id.button_1);
        mBtnNumber[2] = view.findViewById(R.id.button_2);
        mBtnNumber[3] = view.findViewById(R.id.button_3);
        mBtnNumber[4] = view.findViewById(R.id.button_4);
        mBtnNumber[5] = view.findViewById(R.id.button_5);
        mBtnNumber[6] = view.findViewById(R.id.button_6);
        mBtnNumber[7] = view.findViewById(R.id.button_7);
        mBtnNumber[8] = view.findViewById(R.id.button_8);
        mBtnNumber[9] = view.findViewById(R.id.button_9);
        mBtnNumber[10] = view.findViewById(R.id.button_point);

        mBtnOperator[0] = view.findViewById(R.id.button_add);
        mBtnOperator[1] = view.findViewById(R.id.button_sub);
        mBtnOperator[2] = view.findViewById(R.id.button_mul);
        mBtnOperator[3] = view.findViewById(R.id.button_div);

        mBtnOther[0] = view.findViewById(R.id.button_clean);
        mBtnOther[1] = view.findViewById(R.id.button_back);
        mBtnOther[2] = view.findViewById(R.id.button_equal);

        ShowLongClick longClick = new ShowLongClick();
        mTvExpression = view.findViewById(R.id.expression);
        mTvResult = view.findViewById(R.id.result);
        mTvExpression.setOnLongClickListener(longClick);
        mTvResult.setOnLongClickListener(longClick);

        for (int i = 0; i < mBtnNumber.length; i++) {
            mBtnNumber[i].setOnClickListener(this);
            mBtnNumber[i].setOnLongClickListener(this);
        }

        for(int i = 0; i < mBtnOperator.length; i++) {
            mBtnOperator[i].setOnClickListener(this);
            mBtnOperator[i].setOnLongClickListener(this);
        }

        for (CustomButton text : mBtnOther) {
            text.setOnClickListener(this);
        }
        mBtnOther[1].setOnLongClickListener(backLongClick);     // 设置“←”按钮长按响应函数
        mBtnOther[2].setOnLongClickListener(equalLongClick);    // 设置“=”按钮长按响应函数

        buttonChange(view);     // "( )"按钮处理函数

        initBtnTextImage();
    }

    /**
     * 初始化按钮文字和图片
     */
    private void initBtnTextImage() {
        for(int i = 0; i < mBtnNumber.length - 1; i++) {
            mBtnNumber[i].setText("" + i);
        }
        mBtnNumber[mBtnNumber.length - 1].setText(".");
        mBtnNumber[0].setImageResource(R.mipmap.little_btn_function_pai_pressed);
        mBtnNumber[1].setImageResource(R.mipmap.little_btn_function_sinh_pressed);
        mBtnNumber[2].setImageResource(R.mipmap.little_btn_function_cosh_pressed);
        mBtnNumber[3].setImageResource(R.mipmap.little_btn_function_tanh_pressed);
        mBtnNumber[4].setImageResource(R.mipmap.little_btn_function_sin_pressed);
        mBtnNumber[5].setImageResource(R.mipmap.little_btn_function_cos_pressed);
        mBtnNumber[6].setImageResource(R.mipmap.little_btn_function_tan_pressed);
        mBtnNumber[7].setImageResource(R.mipmap.little_btn_function_sqrtx_pressed);
        mBtnNumber[8].setImageResource(R.mipmap.little_btn_function_sqrtx_y_pressed);
        mBtnNumber[9].setImageResource(R.mipmap.little_btn_function_x2_pressed);

        mBtnOperator[0].setText("＋");
        mBtnOperator[0].setImageResource(R.mipmap.little_btn_function_e_pressed);
        mBtnOperator[1].setText("－");
        mBtnOperator[1].setImageResource(R.mipmap.little_btn_function_yx_pressed);
        mBtnOperator[2].setText("×");
        mBtnOperator[2].setImageResource(R.mipmap.little_btn_function_in_pressed);
        mBtnOperator[3].setText("÷");
        mBtnOperator[3].setImageResource(R.mipmap.little_btn_function_log_pressed);

        mBtnOther[0].setText("C");
        mBtnOther[1].setText("←");
        mBtnOther[2].setText("=");
    }

    /**
     * 设置表达式和结果 “要” 显示的内容
     * <div />调用此函数之后，并未显示
     *
     * @param expression 要显示的表达式
     * @param result     要显示的结果
     */
    private void setShow(StringBuilder expression, String result) {
        mExpression = expression;
        mResult = result;
    }

    /**
     * 设置表达式和结果显示的内容
     * <div />调用此函数之后,会显示
     *
     * @param expression 要显示的表达式
     * @param result     要显示的结果
     */
    private void setShow2(StringBuilder expression, String result) {
        setShow(expression, result);

        mTvExpression.setText(mExpression);
        mTvResult.setText(mResult);
    }

    /**
     * 数字0操作按钮
     *
     * @param view
     */
    private void buttonNumber0(View view) {
        if (mState == State.RESULT_SHOW) {
            setShow(new StringBuilder(""), "");
        }

        // 不允许数字以多个0开始
        String lastNumber = matchLastInputNumber(mExpression.toString());
        if (lastNumber.length() == 1 && lastNumber.equals("0"))
            return;

        buttonNumber(view);
    }

    /**
     * 数字操作按钮
     *
     * @param view
     */
    private void buttonNumber(View view) {
        if (mState == State.RESULT_SHOW) {
            setShow(new StringBuilder(""), "");
        }

        // 限制输入的数字位数
        if(matchLastInputNumber(mExpression.toString()).length() >= 15) {
            Common.ToastShow(getActivity(), "数字不能超过15位");
            return ;
        }

        // 如果数字前是e或者π，则添加*
        char number = ((CustomButton) view).getTextView().getText().toString().charAt(0);
        if(Common.matchLastChar("[eπ]", mExpression.toString()))
            mExpression.append("×" + number);
        else
            mExpression.append(number);
        mState = State.INPUT;      // 置为输入状态
    }

    /**
     * 小数点处理
     *
     * @param view
     */
    private void buttonPoint(View view) {
        if (mState == State.RESULT_SHOW) {
            setShow(new StringBuilder(""), "");
        }

        // 匹配数字的正则
        String numberRegex = "[0-9]";

        /**
         * 判断数字中是否包含了小数点
         * 如果包含了，则不再添加小数点
         */
        String lastNumber = matchLastInputNumber(mExpression.toString());
        if (!lastNumber.equals("") && lastNumber.contains("."))
            return;

        // 如果一开始就输入小数点，在小数点前添加0
        // 如果小数点前为非数字，则添加0
        if (mExpression.length() == 0) {
            mExpression.append("0");
        } else {
            String last = mExpression.substring(mExpression.length() - 1, mExpression.length());
            if (!Pattern.matches(numberRegex, last)) {
                mExpression.append("0");
            }
        }

        mExpression.append(((CustomButton) view).getTextView().getText().toString().charAt(0));
        mState = State.INPUT;   // 置为输入状态
    }

    /**
     * 输入“（）”按钮操作
     * <br />点击输入“（”
     * <br />长按输入“）”
     *
     * @param view
     */
    private void buttonChange(View view) {
        CustomButton tvChange = (CustomButton) view.findViewById(R.id.button_change);
        tvChange.setText("( )");

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == State.RESULT_SHOW) {
                    setShow(new StringBuilder(""), "");
                }

                // 匹配数字的正则
                String numberRegex = "[0-9)]";
                int index = mExpression.length();
                String last = mExpression.substring(index > 0 ? index - 1 : index, index);

                // 如果"("前为数字或者")"，则添加"*"
                if (Pattern.matches(numberRegex, last)) {
                    mExpression.append("×(");
                } else {
                    mExpression.append("(");
                }

                mTvResult.setText(mExpression);
                mState = State.INPUT;   // 置为输入状态
            }
        });

        tvChange.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mState == State.RESULT_SHOW) {
                    setShow(new StringBuilder(""), "");
                }

                mExpression.append(")");
                mTvResult.setText(mExpression);

                mState = State.INPUT;   // 置为输入状态
                return true;
            }
        });
    }

    /**
     * +-/*按钮操作
     * <br />如果上一个输入，也为符号，则替换
     *
     * @param view
     */
    private void buttonOperator(View view) {
        String operator = ((CustomButton) view).getTextView().getText().toString().substring(0, 1);

        if (mState == State.RESULT_SHOW) {
            setShow(new StringBuilder(""), mResult);
        }

        // 如果上一次计算结果存在，则作为下一次运算的输入
        if (mResult != "") {
            if (Pattern.matches(Constant.NUMBER_REGEX, mResult)) {
                mExpression.append(mResult + operator);
                setShow(mExpression, "");
                mState = State.INPUT;
                return;
            }
            setShow(new StringBuilder(""), "");
        }

        // 第0位，只能输入“-”运算符
        if (mExpression.length() == 0 && !operator.equals("－")) {
            return;
        }

        int index = mExpression.length();
        String last = mExpression.substring(index > 0 ? index - 1 : index, index);

        // 判断上一个输入是否为符号，如果为符号，则替换
        if (Pattern.matches(Constant.OPERATOR_REGEX2S, last)) {
            // 如果输入的为“-”，则直接替换
            mExpression = new StringBuilder(mExpression.substring(0, mExpression.length() - 1));
            if(operator.equals("－")) {
                mExpression.append(operator);
            }
            else {
                // 第0位和“（”后，不能输入“+*/”
                int index2 = mExpression.length();
                String last2 = mExpression.substring(index2 > 0 ? index2 - 1 : index2, index2);
                if(mExpression.length() != 0 && !last2.equals("(")) {
                    mExpression.append(operator);
                }
            }
        } else {
            // 处理“（”后输入“-”，如果输入+*/，则无效
            if(last.equals("(")) {
                if(operator.equals("－"))
                    mExpression.append(operator);
            }
            else {
                mExpression.append(operator);
            }
        }

        mState = State.INPUT;   // 置为输入状态
    }

    /**
     * 复位按钮操作
     */
    private void buttonReset() {
        // 不同状态下的复位操作
        switch (mState) {
            case INIT:
                setShow2(new StringBuilder(""), "");
                break;

            case INPUT:
                String string = mTvExpression.getText().toString();
                setShow2(new StringBuilder(string), "");
                setShow(new StringBuilder(""), "");
                break;

            case RESULT_SHOW:
                setShow2(new StringBuilder(mResult), "");
                setShow(new StringBuilder(""), "");
                break;
        }
        mState = State.INIT;
    }

    /**
     * 回退按钮操作
     */
    private void buttonBack() {
        if (mState != State.INPUT)
            return;

        int end = mExpression.length() - 1;
        mExpression = new StringBuilder(mExpression.substring(0, end >= 0 ? end : 0));
        mTvResult.setText(mExpression);
    }

    /**
     * "="按钮操作
     *
     * @param view
     */
    private void buttonEqual(View view) {
        if (mState != State.INPUT || mExpression.length() == 0)
            return;

        if (!mOperator.checkExpression(mExpression.toString())) {
            mResult = "表达式错误";
        } else {
            Double result = mOperator.calculator(mExpression.toString());
            if (result == null) {
                mResult += "表达式错误";
            } else {
                mResult += handleResultPoint(result);
            }
        }

        mTvExpression.setText(mExpression);
        mTvResult.setText(mResult);
        mState = State.RESULT_SHOW;     // 置为结果展示状态

        // 保存历史记录
        History history = new History();
        history.setExpression(mExpression.toString());
        history.setResult(mResult.toString());
        history.setDate(Common.getCurrentTime("yyyy-MM-dd HH:mm"));
        dbManager.insertHistory(history);
    }

    /**
     * 匹配表达式中最后一个正在输入的数字
     *
     * @param expression 需要匹配的表达式
     * @return 如果匹配到，则返回；未匹配到，则返回“”；
     */
    private String matchLastInputNumber(String expression) {
        Matcher matcher = Pattern.compile(Constant.NUMBER_REGEX).matcher(expression);
        String lastNumber = "";
        while (matcher.find()) {
            lastNumber = matcher.group(0);
        }
        if (expression.lastIndexOf(lastNumber) + lastNumber.length() == expression.length()) {
            return lastNumber;
        } else {
            return "";
        }
    }

    /**
     * 处理计算结果
     * <br />去掉小数部分无用的0
     *
     * @param data 需要处理的结果
     * @return
     */
    private String handleResultPoint(Double data) {
        String result = "";

        // 处理除数为0的情况
        if (data == Double.NEGATIVE_INFINITY) {
            result = "-∞";
            return result;
        } else if (data == Double.POSITIVE_INFINITY) {
            result = "∞";
            return result;
        } else if (data.isNaN()) {
            result = "NaN";
            return result;
        }

        // 如果是结果太大，或者太小，则用科学计数法显示
        // 整数大于15位，小数小于10位
        if(data > 0) {
            if(data >= 1000000000000000d || data < 0.00000000001) {
                return data.toString();
            }
        }
        else if(data < 0){
            if(data <= -1000000000000000d || data > -0.00000000001) {
                return data.toString();
            }
        }

        // 保留小数点后10位
        DecimalFormat format = new DecimalFormat("0.0000000000");
        format.setGroupingUsed(true);   //设置用逗号分隔整数部分
        result = format.format(data);

        // 匹配出小数部分中有效的部分
        String regex = "\\.[0-9]*[1-9]{1}";
        Matcher matcher = Pattern.compile(regex).matcher(result);
        String point = "";
        while (matcher.find())
            point = matcher.group(0);

        // 去掉结果中无用的小数点和0
        if (point.equals("")) {
            result = result.substring(0, result.indexOf("."));
        } else {
            result = result.substring(0, result.lastIndexOf(point) + point.length());
        }
        return result;
    }

    @Override
    public boolean onLongClick(View v) {
        // 在标准型下，长按无效
        if(mType == CalcType.STANDARD)
            return true;

        // 重新计算，清空表达式和结果
        if (mState == State.RESULT_SHOW) {
            setShow(new StringBuilder(""), "");
        }

        switch(v.getId()) {
            case R.id.button_0:
                buttonLong0(v);
                break;

            case R.id.button_1:
                buttonLong123456(v, "sinh");
                break;

            case R.id.button_2:
                buttonLong123456(v, "cosh");
                break;

            case R.id.button_3:
                buttonLong123456(v, "tanh");
                break;

            case R.id.button_4:
                buttonLong123456(v, "sin");
                break;

            case R.id.button_5:
                buttonLong123456(v, "cos");
                break;

            case R.id.button_6:
                buttonLong123456(v, "tan");
                break;

            case R.id.button_7:
                buttonLong7(v);
                break;

            case R.id.button_8:
                buttonLong8(v);
                break;

            case R.id.button_9:
                buttonLong9(v);
                break;

            case R.id.button_add:
                buttonLongAdd(v);
                break;

            case R.id.button_sub:
                buttonLongSub(v);
                break;

            case  R.id.button_div:
                buttonLongMulDiv(v, "log");
                break;

            case  R.id.button_mul:
                buttonLongMulDiv(v, "ln");
                break;
        }

        mState = State.INPUT;
        mTvResult.setText(mExpression);
        return true;
    }

    /**
     * "←"按钮长按响应
     * <br />切换标准型和科学型
     */
    private View.OnLongClickListener backLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            buttonLongBack(v);
            return true;
        }
    };

    /**
     * "="按钮长按响应
     * <br />切换标准型和科学型
     */
    private View.OnLongClickListener equalLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            buttonLongEqual(v);
            return true;
        }
    };

    /**
     * "0"按钮长按响应
     * <br />在科学型下，分别输入π
     * @param v
     */
    private void buttonLong0(View v) {
        if(Common.matchLastChar("[)0-9\\.e]", mExpression.toString())) {
            mExpression.append("×π");
        }
        else {
            mExpression.append("π");
        }
    }

    /**
     * "1、2、3、4、5、6"按钮长按响应
     * <br />在科学型下，分别输入sinh、cosh、tanh、sin、cos、tan
     * @param v
     */
    private void buttonLong123456(View v, String operator) {
        String string = operator;
        string += "(";      //输入湿，添加"("

        if(Common.matchLastChar("[)0-9\\.eπ]", mExpression.toString()))
            mExpression.append("×" + string);
        else
            mExpression.append(string);
    }

    /**
     * "7"按钮长按响应
     * <br />在科学型下，输入√（根号）
     * @param v
     */
    private void buttonLong7(View v) {
        if(Common.matchLastChar("[)0-9\\.eπ]", mExpression.toString()))
            mExpression.append("×√");
        else
            mExpression.append("√");
    }

    /**
     * "8"按钮长按响应
     * <br />在科学型下，输入^(1.0/
     * @param v
     */
    private void buttonLong8(View v) {
        mExpression.append("^(1/");
    }

    /**
     * "9"按钮长按响应
     * <br />在科学型下，输入^2
     * @param v
     */
    private void buttonLong9(View v) {
        mExpression.append("^2");
    }

    /**
     * "+"长按响应
     * <br />在科学型下，长按会输入e
     * @param v
     */
    private void buttonLongAdd(View v) {
        if(Common.matchLastChar("[)0-9\\.π]", mExpression.toString())) {
            mExpression.append("×e");
        }
        else {
            mExpression.append("e");
        }
    }

    /**
     * "-"长按响应
     * <br />在科学型下，长按会输入ln
     * @param v
     */
    private void buttonLongSub(View v) {
        mExpression.append("^(");      //输入湿，添加"("
    }

    /**
     * "* /"长按响应
     * <br />在科学型下，长按*会输入ln，长按/会输入log
     * @param v
     */
    private void buttonLongMulDiv(View v, String operator) {
        String expression = operator;
        expression += "(";      //输入湿，添加"("

        if(Common.matchLastChar("[)0-9\\.eπ]", mExpression.toString()))
            mExpression.append("×" + expression);
        else
            mExpression.append(expression);
    }

    /**
     * "←"长按响应
     * 清空输入的表达式
     * @param v
     */
    private void buttonLongBack(View v) {
        setShow(new StringBuilder(""), mResult);
        mTvResult.setText(mExpression);
    }

    /**
     * "="长按处理,切换标准计算器和科学计算器
     *
     * @param v
     */
    private void buttonLongEqual(View v) {
        if(mType == CalcType.STANDARD) {
            mType = CalcType.SCIENCE;

            for(int i = 0; i < mBtnNumber.length - 1; i++) {
                mBtnNumber[i].showImage();
            }

            for(CustomButton button : mBtnOperator)
                button.showImage();
        }
        else {
            mType = CalcType.STANDARD;

            for(int i = 0; i < mBtnNumber.length - 1; i++) {
                mBtnNumber[i].hideImage();
            }

            for(CustomButton button : mBtnOperator)
                button.hideImage();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 描述计算器的状态
     */
    enum State {
        /** 初始状态 */
        INIT,

        /** 输入状态 */
        INPUT,

        /** 结果展示状态 */
        RESULT_SHOW
    }

    /**
     * 计算器类型，有标准型和科学型
     */
    enum CalcType {
        /** 标准型 */
        STANDARD,

        /** 科学型 */
        SCIENCE
    }

    /**
     * 显示区长按响应函数
     */
    private class ShowLongClick implements View.OnLongClickListener {
        private AlertDialog dialog;
        private View view;
        private HistoryAdapter adapter;
        private ListView listView;
        private List<History> datas;

        private Button btnClear;

        public ShowLongClick() {
            init();
        }

        private void init() {
            datas = new ArrayList<>();
            view = View.inflate(getActivity(), R.layout.layout_history, null);
            listView = view.findViewById(R.id.lv_history);
            adapter = new HistoryAdapter(getActivity(), datas);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();

                    History history = datas.get(position);
                    setShow(new StringBuilder(history.getExpression()), "");
                    mTvResult.setText(history.getExpression());
                    mState = State.INPUT;   // 设置为输入模式，可继续编辑表达式
                }
            });

            btnClear = view.findViewById(R.id.btn_history_clear);
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(datas.size() <= 0) {
                        return ;
                    }

                    List<String> ids = new ArrayList<String>();
                    for(History history : datas) {
                        ids.add(history.getId());
                    }
                    dbManager.deleteHistory(ids);       // 清楚历史记录

                    datas.clear();
                    adapter.notifyDataSetChanged();
                }
            });

            dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .create();
        }

        @Override
        public boolean onLongClick(View v) {
            dialog.show();
            setDialogDatas(dbManager.queryAllHistory());
            return true;
        }

        /**
         * 更新历史记录
         * @param histories
         */
        public void setDialogDatas(List<History> histories) {
            datas.clear();
            for(History history : histories)
                datas.add(history);
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
