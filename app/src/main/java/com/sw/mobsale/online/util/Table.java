package com.sw.mobsale.online.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * 表：关系型数据结构
 */
public class Table implements java.io.Serializable {
    //保存列名
    private Hashtable columns = new Hashtable();
    //保存主键
    private Vector primaryKeys = new Vector();
    //保存所有行
    private Vector rows = new Vector();
    //总行数
    private int totalRowCount = -1;
    //表名
    private String name;

    public Table() {
    }
    //根据行列集取得表
    public Table(Vector rows, Hashtable columns) {
        this.rows = rows;
        this.columns = columns;
    }
    //增加一列小写的列名,列编号创建的一个Object是value
    public void addColumns(String name) {
        columns.put(name.toLowerCase(), new Integer(columns.size()));
    }

    /**
     * 增加列
     *
     * @param name
     * @param updateRows:是否为所有行的该列插入一个空值
     * 为什么要加两个空值null???
     */
    public void addColumns(String name, boolean updateRows) {
        this.addColumns(name);
        if (updateRows) {
            for (int i = 0; i < rows.size(); i++) {
                Vector row = (Vector) rows.elementAt(i);
                row.addElement(null);
            }
        }
    }

    /**
     * 删除列 （未加列是否存在的判断）
     *
     * @param name
     */
    public void removeColumn(String name) {
        int colIndex = ((Integer) columns.get(name.toLowerCase())).intValue();//列索引
        columns.remove(name.toLowerCase());//删除列名

        Iterator itr = columns.keySet().iterator();
        while (itr.hasNext()) {
            String colName = String.valueOf(itr.next());
            int location = Integer.parseInt(String
                    .valueOf(columns.get(colName)));

            if (location > colIndex) {//后面的列索引减1
                location -= 1;
                columns.put(colName, new Integer(location));
            }
        }
        //删除这一列在每一行中对应的列值
        for (int i = 0; i < rows.size(); i++) {
            Vector row = (Vector) rows.elementAt(i);
            row.removeElementAt(colIndex);
        }
    }

    /**
     * 增加一行
     *
     * @param row
     */
    public void addRow(Vector row) {
        rows.addElement(row);
    }

    public void addRow(int index,Vector row) {
        rows.add(index,row);
    }

    public void addRow(int index) {
        Vector row = new Vector();
        for (int i = 0; i < this.getColumnCount(); i++) {
            row.addElement(null);
        }
        rows.add(index, row);
    }

    /**
     * 创建新空行
     *
     */
    public void createRow() {
        Vector row = new Vector();
        for (int i = 0; i < this.getColumnCount(); i++) {
            row.addElement(null);
        }
        this.rows.addElement(row);
    }
    /**
     * @author Bread
     * @date (2005-4-7 10:27:03)
     * @param
     * @return
     * Function :创建指定行数空行
     */
    public void createRow(int rowCount) {
        for(int j = 0 ; j < rowCount ; j++){
            Vector row = new Vector();
            for (int i = 0; i < this.getColumnCount(); i++) {
                row.addElement(null);
            }
            this.rows.addElement(row);
        }
    }

    //设置某一行的内容
    public void setRow(int index, Vector row) {
        rows.set(index, row);
    }
    //取得指定行
    public Vector getRow(int row) {
        return (Vector) rows.elementAt(row);
    }

    /**
     * 设置单元格值
     *
     * @param row
     * @param col
     * @param obj
     */
    public void setCellValue(int row, int col, Object obj) {
        ((Vector) this.rows.elementAt(row)).setElementAt(obj, col);
    }

    /**
     * 设置单元格值
     *
     * @param row
     * @param colName
     * @param obj
     */
    public void setCellValue(int row, String colName, Object obj) {
        Object colObj = columns.get(colName.toLowerCase());
        ((Vector) this.rows.elementAt(row)).setElementAt(obj,
                ((Integer) colObj).intValue());
    }

    public String getCellValue(int rowNo, String colName) {
        return getCellValue(rowNo, colName, false);
    }

    /**
     * 字符型单元格取值
     *
     * @param rowNo
     * @param colName
     * @param format 如果为true，则当值为空时返回""
     * @return
     */
    public String getCellValue(int rowNo, String colName, boolean format) {
        Object colObj = columns.get(colName.toLowerCase());
        if (null == colObj) {
            if (format) {
                return "";
            }
            return null;
        }

        return getCellValue(rowNo, ((Integer) colObj).intValue(), format);
    }

    public String getCellValue(int rowNo, int colNo) {
        return getCellValue(rowNo, colNo, false);
    }

    /**
     * 字符型单元格取值
     *
     * @param rowNo
     * @param colNo
     * @param format 如果为true，则当值为空时返回""
     * @return
     */
    public String getCellValue(int rowNo, int colNo, boolean format) {
        Vector row = (Vector) rows.elementAt(rowNo);
        Object obj = row.elementAt(colNo);
        if (null == obj) {
            if (format) {
                return "";
            }
            return null;
        }
        return obj.toString();
    }

    /**
     * Object类型单元格取值
     *
     * @param rowNo 行号
     * @param colName 列名
     * @return Object
     */
    public Object getCellObjectValue(int rowNo, String colName) {
        Object colObj = columns.get(colName.toLowerCase());
        if (null == colObj) {
            return null;
        }
        return getCellObjectValue(rowNo, ((Integer) colObj).intValue());
    }

    /**
     * Object类型单元格取值
     *
     * @param rowNo 行号
     * @param colNo 列号
     * @return
     */
    public Object getCellObjectValue(int rowNo, int colNo) {
        Vector row = (Vector) rows.elementAt(rowNo);
        return row.elementAt(colNo);
    }

    /**
     * 行数
     *
     * @return
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * 列数
     *
     * @return
     */
    public int getColumnCount() {
        return columns.size();
    }

    /**
     * 获得列名
     *
     * @param index
     * @return
     */
    public String getColumnName(int index) {
        Iterator itr = columns.keySet().iterator();

        while (itr.hasNext()) {
            String name = String.valueOf(itr.next());
            int location = Integer.parseInt(String.valueOf(columns.get(name)));

            if (location == index) {
                return name;
            }
        }

        return null;
    }

    /**
     * 增加主键
     * 先判断增加的主键是否存在
     * @param keyName
     */
    public void addPrimaryKey(String keyName) {
        if (!primaryKeys.contains(keyName.toLowerCase())) {
            primaryKeys.addElement(keyName.toLowerCase());
        }
    }

    /**
     * 增加列，并把该列设为主键
     *
     * @param columnName
     */
    public void addPrimaryKeyColumn(String columnName) {
        addPrimaryKey(columnName);
        if (!columns.containsKey(columnName)) {
            addColumns(columnName);
        }
    }

    /**
     * 获得所有主键
     *
     * @return
     */
    public Vector getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * 获得所有列
     *
     * @return
     */
    public Hashtable getColumns() {
        return columns;
    }

    /**
     * 设置所有列
     *
     * @param columns
     */
    public void setColumns(Hashtable columns) {
        this.columns = columns;
    }

    /**
     * 获得所有行
     *
     * @return
     */
    public Vector getRows() {
        return rows;
    }

    /**
     * 设置所有行
     *
     * @param rows
     */
    public void setRows(Vector rows) {
        this.rows = rows;
    }

    /**
     * 设置所有主键
     *
     * @param primaryKeys
     */
    public void setPrimaryKeys(Vector primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    /**
     * 删除行
     *
     * @param rowIndex
     */
    public void removeRow(int rowIndex) {
        rows.removeElementAt(rowIndex);
    }

    /**
     * 删除所以记录
     *
     * @return
     */
    public void removeAllRow() {
        rows.clear();
    }

    /**
     * 总行数 指该表在数据库存在的真实记录数 对于分页查询，返回值可能不等于getRowCount()
     *
     * @return
     */
    public int getTotalRowCount() {
        if (totalRowCount == -1) {
            return getRowCount();
        }
        return totalRowCount;
    }

    /**
     * 设置总行数 指该表在数据库存在的真实记录数 对于分页查询，返回值可能不等于rowCount
     *
     * @param totalRowCount
     */
    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    /**
     * 表名
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}