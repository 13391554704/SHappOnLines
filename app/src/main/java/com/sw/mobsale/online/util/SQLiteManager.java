package com.sw.mobsale.online.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class SQLiteManager {
    private DBHelper dbHelper;
    public static SQLiteManager instance = null;
    private SQLiteDatabase sqliteDatabase;

    /**
     * 构造函数
     * @param context   上下文对象
     */
    private SQLiteManager(Context context) {
        dbHelper = new DBHelper(context);
        sqliteDatabase = dbHelper.getReadableDatabase();
    }

    /***
     * 获取本类对象实例
     * @param context   上下文对象
     * @return
     */
    public static final SQLiteManager getInstance(Context context) {
        if (instance == null)
            instance = new SQLiteManager(context);
        return instance;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if(sqliteDatabase.isOpen()) sqliteDatabase.close();
        if(dbHelper != null) dbHelper.close();
        if(instance != null) instance = null;
    }

    /**
     * 插入数据
     * @param sql       执行更新操作的sql语句
     * @param bindArgs      sql语句中的参数,参数的顺序对应占位符顺序
     * @return  result      返回新添记录的行号，与主键id无关
     */
    public Long insertDataBySql(String sql, String[] bindArgs) throws Exception{
        long result = 0;
        if(sqliteDatabase.isOpen()){
            SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
            if(bindArgs != null){
                int size = bindArgs.length;
                for(int i = 0; i < size; i++){
                    //将参数和占位符绑定，对应
                    statement.bindString(i+1, bindArgs[i]);
                }
                result = statement.executeInsert();
                statement.close();
            }
        }else{
            Log.i("info", "数据库已关闭");
        }
        return result;
    }

    /**
     * 插入数据
     * @param table         表名
     * @param values        要插入的数据
     * @return  result      返回新添记录的行号，与主键id无关
     */
    public Long insertData(String table, ContentValues values){
        long result = 0;
        if(sqliteDatabase.isOpen()){
            result = sqliteDatabase.insert(table, null, values);
        }
        return result;
    }

    /**
     * 更新数据
     * @param sql       执行更新操作的sql语句
     * @param bindArgs  sql语句中的参数,参数的顺序对应占位符顺序
     */
    public void updateDataBySql(String sql, String[] bindArgs) throws Exception{
        if(sqliteDatabase.isOpen()){
            SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
            if(bindArgs != null){
                int size = bindArgs.length;
                for(int i = 0; i < size; i++){
                    statement.bindString(i+1, bindArgs[i]);
                }
                statement.execute();
                statement.close();
            }
        }else{
            Log.i("info", "数据库已关闭");
        }
    }

    /**
     * 执行批量更新
     * @param ar        ar存放的是执行更新操作的sql语句
     * @throws Exception
     */
    public synchronized void executeBatch(ArrayList ar) throws Exception {
        if(sqliteDatabase.isOpen()){
            // 开始时间
            long time1 = System.currentTimeMillis();
            // 开始事务
            sqliteDatabase.beginTransaction();
            try {
                // 数据循环处理
                for (int i = 0; i < ar.size(); i++) {
                    sqliteDatabase.execSQL((String) ar.get(i));
                    Log.d("TAG", "addBatch: " + ar.get(i));
                }
                //设置事务处理成功，不设置会自动回滚不提交
                sqliteDatabase.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                //结束事务
                sqliteDatabase.endTransaction();
            }
            // 结束时间
            long time2 = System.currentTimeMillis();
            Log.i("info", "消耗时间：" + (time2 - time1));
        } else {
            Log.i("info", "数据库已关闭");
        }
    }
    /**
     * 更新数据
     * @param table         表名
     * @param values        表示更新的数据
     * @param whereClause   表示SQL语句中条件部分的语句
     * @param whereArgs     表示占位符的值
     * @return
     */
    public int updataData(String table, ContentValues values, String whereClause, String[] whereArgs){
        int result = 0;
        if(sqliteDatabase.isOpen()){
            result = sqliteDatabase.update(table, values, whereClause, whereArgs);
        }
        return result;
    }

    /**
     * 删除数据
     * @param sql       执行更新操作的sql语句
     * @param bindArgs  sql语句中的参数,参数的顺序对应占位符顺序
     */
    public void deleteDataBySql(String sql, String[] bindArgs) throws Exception{
        if(sqliteDatabase.isOpen()){
            SQLiteStatement statement = sqliteDatabase.compileStatement(sql);
            if(bindArgs != null){
                int size = bindArgs.length;
                for(int i = 0; i < size; i++){
                    statement.bindString(i+1, bindArgs[i]);
                }
                Method[] mm = statement.getClass().getDeclaredMethods();
                for (Method method : mm) {
                    Log.i("info", method.getName());
                    /**
                     *  反射查看是否能获取executeUpdateDelete方法
                     *  查看源码可知 executeUpdateDelete是public的方法，但是好像被隐藏了所以不能被调用，
                     *      利用反射貌似只能在root以后的机器上才能调用，小米是可以，其他机器却不行，所以还是不能用。
                     */
                }
                statement.execute();
                statement.close();
            }
        }else{
            Log.i("info", "数据库已关闭");
        }
    }

    /**
     * 删除数据
     * @param table         表名
     * @param whereClause   表示SQL语句中条件部分的语句
     * @param whereArgs     表示占位符的值
     * @return
     */
    public int deleteData(String table, String whereClause, String[] whereArgs){
        int result = 0;
        if(sqliteDatabase.isOpen()){
            result = sqliteDatabase.delete(table, whereClause, whereArgs);
        }
        return result;
    }

    /**
     * 查询数据
     * @param sql         执行查询操作的sql语句
     * @param selectionArgs     查询条件
     * @return                  返回查询的游标，可对数据自行操作，需要自己关闭游标
     */
    public Cursor queryData2Cursor(String sql, String[] selectionArgs) throws Exception{
        if(sqliteDatabase.isOpen()){
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
        return null;
    }

    /**
     * 查询数据
     * @param sql               执行查询操作的sql语句
     * @param selectionArgs     查询条件
     * @param object                Object的对象
     * @return List<Object>       返回查询结果
     */
    public List<Object> queryData2Object(String sql, String[] selectionArgs, Object object) throws Exception{
        List<Object> mList = new ArrayList<Object>();
        if(sqliteDatabase.isOpen()){
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
            Field[] f;
            if(cursor != null && cursor.getCount() > 0) {
                while(cursor.moveToNext()){
                    f = object.getClass().getDeclaredFields();
                    for(int i = 0; i < f.length; i++) {
                        //为JavaBean 设值
                        invokeSet(object, f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));
                    }
                    mList.add(object);
                }
            }
            cursor.close();
        }else{
            Log.i("info", "数据库已关闭");
        }
        return mList;
    }

    /**
     * 查询数据
     * @param sql                           执行查询操作的sql语句
     * @param selectionArgs                 查询条件
     * @param object                            Object的对象
     * @return  List<Map<String, Object>>   返回查询结果
     * @throws Exception
     */
    public List<Map<String, Object>> queryData2Map(String sql, String[] selectionArgs, Object object)throws Exception{
        List<Map<String, Object>> mList = new ArrayList<Map<String,Object>>();
        if(sqliteDatabase.isOpen()){
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
            Field[] f;
            Map<String, Object> map;
            if(cursor != null && cursor.getCount() > 0) {
                while(cursor.moveToNext()){
                    map = new HashMap<String, Object>();
                    //获得某个类的所有申明的字段，即包括public、private和proteced，
                    //但是不包括父类的申明字段。
                    f = object.getClass().getDeclaredFields();
                    for(int i = 0; i < f.length; i++) {
                        map.put(f[i].getName(), cursor.getString(cursor.getColumnIndex(f[i].getName())));
                    }
                    mList.add(map);
                }
            }
            cursor.close();
        }else{
            Log.i("info", "数据库已关闭");
        }
        return mList;
    }
    /**
     * 查询数据
     * @param sql                           执行查询操作的sql语句
     * @param selectionArgs                 查询条件
     * @return  table                       返回查询结果
     * @throws Exception
     */
    public Table queryData2Table(String sql, String[] selectionArgs)throws Exception{
        Table table = new Table();
        if(sqliteDatabase.isOpen()){
            Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
            if(cursor != null && cursor.getCount() > 0) {
                // 返回列的总行数
                // int columnCount = cursor.getColumnCount();
                // 返回一个字符串数组的列名
                String[]  col = cursor.getColumnNames();
                //@构造table的表头信息，避免因为查询结果唯恐导致的表头为空
                for (int i = 0; i < col.length; i++) {
                    table.addColumns(col[i]);
                }
                // @添加表的每一行的信息
                while(cursor.moveToNext()){
                    Vector row = new Vector();
                    for(int i = 0; i < col.length; i++) {
                        row.add(cursor.getString(cursor.getColumnIndex(col[i])));
                    }
                    table.addRow(row);
                }
            }
            cursor.close();
        }else{
            Log.i("info", "数据库已关闭");
        }
        return table;
    }


    /**
     * java反射bean的set方法
     * @param objectClass
     * @param fieldName
     * @return
     */
    public static Method getSetMethod(Class objectClass, String fieldName) {
        try {
            Class[] parameterTypes = new Class[1];
            Field field = objectClass.getDeclaredField(fieldName);
            parameterTypes[0] = field.getType();
            StringBuffer sb = new StringBuffer();
            sb.append("set");
            sb.append(fieldName.substring(0, 1).toUpperCase());
            sb.append(fieldName.substring(1));
            Method method = objectClass.getMethod(sb.toString(), parameterTypes);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行set方法
     * @param object    执行对象
     * @param fieldName 属性
     * @param value     值
     */
    public static void invokeSet(Object object, String fieldName, Object value) {
        Method method = getSetMethod(object.getClass(), fieldName);
        try {
            method.invoke(object, new Object[] { value });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
