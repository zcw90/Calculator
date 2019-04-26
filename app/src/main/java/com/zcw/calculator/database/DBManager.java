package com.zcw.calculator.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zcw.calculator.bean.History;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private static DBManager INSTANCE;

    public static DBManager getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (DBManager.class) {
                if(INSTANCE == null)
                    INSTANCE = new DBManager(context);
            }
        }
        return INSTANCE;
    }

    private DBManager(Context context) {
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 向数据库中插入历史记录
     * @param history
     */
    public void insertHistory(History history) {
        //采用事务处理，保证数据的完整性
        db.beginTransaction();
        try {
            // 历史记录信息
            String expression = history.getExpression();
            String result = history.getResult();
            String date = history.getDate();

            //拼接、执行插入语句
            String sql = "INSERT INTO " + DatabaseHelper.TABLE_NAME + " VALUES(null,?,?,?)";
            db.execSQL(sql, new String[]{expression, result, date});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 查询所有历史记录
     * @return
     */
    public List<History> queryAllHistory() {
        List<History> histories = new ArrayList<History>();
        Cursor cursor;

        //拼接查询语句
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " order by \"date\" desc";
        cursor = db.rawQuery(sql, null);

        //利用游标遍历所有数据对象
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String expression = cursor.getString(cursor.getColumnIndex("expression"));
            String result = cursor.getString(cursor.getColumnIndex("result"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            History history = new History();
            history.setId(id);
            history.setExpression(expression);
            history.setResult(result);
            history.setDate(date);

            histories.add(history);
        }

        cursor.close();
        return histories;
    }

    /**
     * 删除历史记录
     * @param ids
     */
    public void deleteHistory(List<String> ids) {
        //拼接查询语句
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_NAME + " WHERE id IN (";
        for (int i = 0; i < ids.size() - 1; i++) {
            sql += ids.get(i);
            sql += ',';
        }
        sql += ids.get(ids.size() - 1);
        sql += ')';

        db.execSQL(sql);
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        db.close();     //释放数据库资源
    }
}
