package com.sw.mobsale.online.util;

import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class UpdateData {
    Context context;
    SQLiteManager manager;
    ArrayList<String> list ;


    public UpdateData(Context context){
        this.context =context;
        manager = SQLiteManager.getInstance(context);
        list = new ArrayList<String>();
    }

    /**
     * 客户表
     */
    public void getCustomer(List<Map<String, Object>> dataLists) {
        String sql = "delete from m_pos_customer";
        list.add(sql);
        try {
            for (int i = 0; i < dataLists.size(); i++) {
                String customerCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
                String customerName = dataLists.get(i).get("customerName").toString();
                String address = dataLists.get(i).get("address").toString();
                String distance = dataLists.get(i).get("distance").toString();
                String date = dataLists.get(i).get("date").toString();
                sql = "insert into m_pos_customer(orgCode,orgDesc,customerCode,customerName,customerType,contacts,phoneNum,faxNum,address,remark,isEnabled)" +
                        "values('"+ customerCode +"','orgDesc','" + customerCode + "','" + customerName + "','零售'," +
                        " 'contact','phoneNum','"+ date +"','" + address + "','" + distance  + "','N')";
                list.add(sql);
            }
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订单销售
     *
     * @param orderNo
     * @return
     */
    public Table getDetailSale(String orderNo) {
        Table table = new Table();
        try {
            String sql = "select a.customerCode, a.customerName,a.itemCode,a.itemName,a.itemSpec,a.itemPrice,a.qty,a.itemAmt,b.onHandQty - a.qty   resQty,a.unitName" +
                    " from m_pos_order a,m_pos_stock_detail b where a.itemCode = b.itemCode and a.orderNo =?";
            table = manager.queryData2Table(sql, new String[]{orderNo});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 订单数量，金额
     *
     * @param orderNo
     * @return
     */
    public Table getDetailSaleTotal(String orderNo) {
        Table table = new Table();
        try {
            String sql = "select sum(qty) orderQty,sum(itemAmt) orderAmt" +
                    " from app_pos_order  where orderNo =?";
            table = manager.queryData2Table(sql, new String[]{orderNo});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 生成订单销售临时表
     *
     * @param orderNo
     */
    public void getSaleTemp(String orderNo) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat s = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new Date());
            String time = s.format(new Date());
            String sql = "insert into app_pos_sale_temp (orgCode,orgDesc,customerCode,customerName,customerAddress,customerType,transNo,transItemNo,itemCode,itemName,itemSpec,itemWeight," +
                     "saleUnitCode,saleUnitName,itemPrice,saleQty,saleAmt,discount,discountAmt,assistantQty, assistantUnitCode,assistantUnitName,userCode,userDesc,functionCode,transDate, transTime,posMachineCode," +
                    "transType,isSent,sentDate, sentTime,isSquad,squadDate,squadTime,balanceCode,entryDate,entryTime) select b.orgCode, b.orgDesc, a.customerCode, a.customerName,a.receiveInfo,'订单销售',a.orderNo,a.seqNo,a.itemCode,a.itemName,a.itemSpec," +
                    "'1',b.unitCode,b.unitName,a.itemPrice,a.qty,a.itemAmt,'0','0',a.assistantQty,a.assistantUnitCode,a.assistantUnitName,'11','li','0',a.orderDate,'" + time + "',b.wareHouseName ,'1','Y'," +
                    "'" + date + "','" + time + "','Y','" + date + "','" + time + "','1','" + date + "','" + date + "' from m_pos_order a where a.itemCode = b.itemCode and a.orderNo = '" + orderNo + "'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 零售销售
     */
    public Table getRetailSale() {
        Table table = new Table();
        try {
            String sql = "select itemCode,itemName,itemSpec,itemPrice," +
                    "onHandQty - lotalLocationQty qty," +
                    "unitName " +
                    " from m_pos_stock_detail order by itemCode asc ";
            table = manager.queryData2Table(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    /**
     * 零售销售
     * 零售意外重新进入时先查看销售临时表
     */
    public Table putTempSale() {
        Table table = new Table();
        try {
            String sql = "select a.itemCode,a.itemName,a.itemSpec,a.itemPrice,a.saleQty,b.onHandQty - a.saleQty number,b.lotalLocationQty,a.saleUnitName " +
                    " from m_pos_sale_temp a, m_pos_stock_detail b  where a.itemCode = b.itemCode order by a.itemCode asc";
            table = manager.queryData2Table(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 生成零售临时表
     *
     * @param number
     * @param index
     */
    public void getSell(String number, String store,String address, String itemCode,int index,String date,String time) {
        try {
            String sql = "insert or replace into m_pos_sale_temp (orgCode,orgDesc,customerCode,customerName,customerAddress,customerType,transNo,transItemNo,itemCode,itemName,itemSpec,itemWeight," +
                    "saleUnitCode,saleUnitName,itemPrice,saleQty,saleAmt,discount,discountAmt,assistantQty, assistantUnitCode,assistantUnitName,userCode,userDesc,functionCode,transDate, transTime,posMachineCode," +
                    "transType,isSent,sentDate, sentTime,isSquad,squadDate,squadTime,balanceCode,entryDate,entryTime) select a.orgCode,a.orgDesc,'0000','" + store + "','" + address + "','零售'," +
                    "'transNo', '" + index + "',a.itemCode,a.itemName,a.itemSpec,'1',a.unitCode,a.unitName,a.itemPrice,'" + number + "',a.itemPrice * '" + number + "' ,'0','0',a.assistantQty,a.assistantUnitCode,a.assistantUnitName,'b.entryUserCode','b.entryUserDesc','0'," +
                    "'" + date + "','" + time + "',b.wareHouseName,'1','Y','" + date + "','" + time + "','Y','" + date + "','" + time + "','1','" + date + "','" + date + "' from  m_pos_stock_detail a, m_pos_car_load b where a.itemCode = '" + itemCode + "'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 零售
     * @param store
     * @param address
     * @param number
     */
    public void updateRetail(String store,String address,String number){
        try {
            String sql = "update m_pos_sale_temp set customerName='" + store + "',customerAddress='" + address + "',transNo='" + number + "'where customerName='零售'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 生成零售临时表
     *删除选中改为未选的数据
     * @param itemName
     */
    public void getSellTemp(String itemName,String number) {
        try {
            String sql ;
            sql = "delete from app_pos_sale_temp where itemName='" + itemName + "'";
            list.add(sql);
            sql = "update app_pos_retail_all set qty = '"+ number +"'where itemName='"+itemName+"'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 零售数量，金额
     *
     * @param transNo
     * @return
     */
    public Cursor reSaleTotal(String transNo) {
        Cursor cursor = null;
        try {
            String sql = "select sum(qty)  qty,sum(itemAmt)  amt" +
                    " from app_pos_sale_temp  where transNo ='" + transNo + "' and isConfirmOrder='N'";
            cursor = manager.queryData2Cursor(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    /**
     * 销售表数量，金额
     *
     * @param store, address
     * @return
     */
    public Cursor getSaleTotal(String store, String number) {
        Cursor cursor = null;
        try {
            String sql = "select sum(saleQty) as qty,sum(saleAmt) as amt" +
                    " from m_pos_sale_t  where customerName ='" + store + "' and transNo='" + number + "'";
            cursor = manager.queryData2Cursor(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    public Cursor getTotalByName(String itemName){
        Cursor cursor = null;
        try {
            String sql = "select sum(saleQty) as qty,sum(saleAmt) as amt" +
                    " from m_pos_sale_t  where itemName ='" + itemName + "'";
            cursor = manager.queryData2Cursor(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }
    /**
     * 更新零售临时表
     *
     * @param orderNo
     * @param store
     * @param address
     */
    public void getUpdateSale(String orderNo, String store, String address) {
        try {
            String sql = "update m_pos_sale_temp set transNo='" + orderNo + "'" +
                    " where customerName='" + store + "'and customerAddress='" + address + "'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新订单临时表
     * @param orderNo
     * @param orderTime
     * @param store
     */
    public void getUpdateDetailSale(String orderNo, String orderTime, String store) {
        try {
            String sql = "update m_pos_sale_temp set transTime='" + orderTime + "' where customerName='" + store + "' and transNo='" + orderNo + "'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成销售表，更新库存表和订单表,删除销售临时表
     * <p/>
     * insert or replace into m_pos_sale_temp
     * (sequence,orgCode,orgDesc,customerCode,customerName,customerType,transNo,transItemNo ,itemCode ,itemName ,itemWeight ,
     * saleUnitCode ,saleUnitName ,itemPrice ,saleQty ,saleAmt ,discount ,discountAmt ,assistantQty , assistantUnitCode ,assistantUnitName ,userCode ,userDesc ,
     * functionCode ,transDate , transTime ,posMachineCode ,transType  ,isSent ,sentDate  , sentTime  ,isSquad ,squadDate,squadTime  ,balanceCode ,entryDate,entryTime)
     * select
     * b.sequence,a.orgCode,a.orgDesc, b.customerCode,b.customerName,'dd',
     * b.transNo,b.transItemNo,a.itemCode,a.itemName,'1',a.unitCode,a.unitName,a.itemPrice,b.saleQty,a.itemPrice * '20' ,'0001','200',a.assistantQty,a.assistantUnitCode,a.assistUnitName,'01','li','0',
     * 'transDate','transTime','100','1','Y','2017/01/20','09:16:20','Y','2017/01/20','16:20:01','100','2017/01/20','20:12:30' from  m_pos_stock_detail a ,m_pos_sale_temp b
     * where a.itemCode = b.itemCode ;
     *
     * @param orderNo
     */
    public void getSaleTable(String orderNo) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            // 1.从销售临时表取销售数据
            String sql = "insert into m_pos_sale_t select * from m_pos_sale_temp";
            list.add(sql);
            //2.更新库存表
            sql = "insert or replace into m_pos_stock_detail (orgCode,orgDesc,wareHouseCode,wareHouseName,itemCode,itemName,itemSpec,itemPrice," +
                    " itemTypeCode,unitCode,unitName,onHandQty,onHandAmt,lotalLocationQty,assistantQty,assistantUnitCode,assistantUnitName,lastInDate,lastOutDate) " +
                    " select b.orgCode,b.orgDesc,b.wareHouseCode,b.wareHouseName, b.itemCode,b.itemName,b.itemSpec,b.itemPrice," +
                    " b.itemTypeCode,b.unitCode,b.unitName,b.onHandQty - a.saleQty,b.onHandAmt -b.itemPrice * a.saleQty,  b.lotalLocationQty - a.saleQty,b.assistantQty  - a.assistantQty," +
                    " b.assistantUnitCode,b.assistantUnitName,b.lastInDate,b.lastOutDate  from m_pos_sale_temp a left outer join m_pos_stock_detail b  " +
                    "ON   b.itemCode = a.itemCode ";
            list.add(sql);
            //3.更新订单表
            sql = "update m_pos_order set isFinish='Y' where orderNo= '" + orderNo + "'";
            list.add(sql);
            //4.删除销售临时表
            sql = "delete from m_pos_sale_temp";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 零售订单表
     */
    public void getSaleTable() {
        try {
            // 1.从销售临时表取销售数据
            String sql = "insert into m_pos_sale_t select * from m_pos_sale_temp";
            list.add(sql);
            //2.更新库存表
            sql = "insert or replace into m_pos_stock_detail (orgCode,orgDesc,wareHouseCode,wareHouseName,itemCode,itemName,itemSpec,itemPrice," +
                    " itemTypeCode,unitCode,unitName,onHandQty,onHandAmt,lotalLocationQty,assistantQty,assistantUnitCode,assistantUnitName,lastInDate,lastOutDate) " +
                    " select b.orgCode,b.orgDesc,b.wareHouseCode,b.wareHouseName, b.itemCode,b.itemName,b.itemSpec,b.itemPrice," +
                    " b.itemTypeCode,b.unitCode,b.unitName,b.onHandQty - a.saleQty,b.onHandAmt -b.itemPrice * a.saleQty,  b.lotalLocationQty,b.assistantQty  - a.assistantQty," +
                    " b.assistantUnitCode,b.assistantUnitName,b.lastInDate,b.lastOutDate from m_pos_sale_temp a left outer join m_pos_stock_detail b  " +
                    "ON   b.itemCode = a.itemCode  ";
            list.add(sql);
            // 3.删除销售临时表
            sql = "delete from m_pos_sale_temp";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一日销售数据
     * @param date
     */
    public void deleteSale( String date){
        try {
                String sql = "delete  from m_pos_sale_t where transDate='"+ date + "'";
                list.add(sql);
                manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除单个客户数据
     * @param store
     * @param transNo
     */
    public void deleteSale( String store,String transNo){
        try {
            String sql = "delete  from m_pos_sale_t where transDate ='"+ store + "'and transNo ='" + transNo + "'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步更新库存表
     *
     */
    public void getStock(){
        try {
            ArrayList<String> list1 = new ArrayList<String>();
            //1.从m_pos_car_load_temp去装车数据到m_pos_car_load
            String sql1 = "insert into m_pos_car_load select a.sequence, a.orgCode,a.orgDesc,a.billNo,a.seqNo,a.workDate,a.itemCode,a.itemName,a.itemSpec,a.unitCode,a.unitName,a.itemPrice,a.qty ,a.wareHouseCode," +
                    " a.wareHouseName ,a.customerCode,a.customerName,a.carNumber,a.assistantQty ,a.assistantUnitCode ,a.assistantUnitName,a.entryUserCode,a.entryUserDesc," +
                    " a.entryDate,a.entryTime,a.remark,a.posMachineCode,a.isFlag from m_pos_car_load_temp a";
            list1.add(sql1);
            //2.更新库存表
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String  date = sdf.format(new Date());
            sql1 = "insert or replace into m_pos_stock_detail (orgCode,orgDesc,wareHouseCode,wareHouseName,itemCode,itemName,itemSpec,itemPrice," +
                    " itemTypeCode,unitCode,unitName,onHandQty,onHandAmt,lotalLocationQty,assistantQty,assistantUnitCode,assistantUnitName,lastInDate,lastOutDate) " +
                    " select b.orgCode,b.orgDesc,b.wareHouseCode,b.wareHouseName, b.itemCode,b.itemName,b.itemSpec,b.itemPrice," +
                    " '01',b.unitCode,b.unitName,b.qty + ifNull(a.onHandQty,'0'),b.itemPrice * b.qty + ifNull(a.onHandAmt,'0'),ifNull(a.lotalLocationQty,'0'),b.assistantQty + ifNull(a.assistantQty,'0')," +
                    " b.assistantUnitCode,b.assistantUnitName,b.workDate,b.workDate from m_pos_car_load b  left outer join m_pos_stock_detail a  " +
                    "ON   b.itemCode = a.itemCode  where b.isFlag = 'N'";  //  b.workDate = '"+ date +"' and
            list1.add(sql1);
            //更新装车表，标记已更新库存
            sql1 = "update m_pos_car_load set isFlag = 'Y'";  // where workDate = '"+ date +"'
            list1.add(sql1);
            //更新占用量
            sql1 = "insert or replace into m_pos_stock_detail (orgCode,orgDesc,wareHouseCode,wareHouseName,itemCode,itemName,itemSpec,itemPrice," +
                    " itemTypeCode,unitCode,unitName,onHandQty,onHandAmt,lotalLocationQty,assistantQty,assistantUnitCode,assistantUnitName,lastInDate,lastOutDate) " +
                    " select a.orgCode,a.orgDesc,a.wareHouseCode,a.wareHouseName, a.itemCode,a.itemName,a.itemSpec,a.itemPrice," +
                    " b.itemTypeCode,a.unitCode,a.unitName,a.onHandQty,a.onHandAmt,b.qty + ifNull(a.lotalLocationQty,'0') ,a.assistantQty ," +
                    " a.assistantUnitCode,a.assistantUnitName,a.lastInDate,a.lastOutDate   from m_pos_stock_detail a ," +
                    "(select   itemCode,itemTypeCode,sum(qty) qty from m_pos_order  " + //where preConsignDate = 'date'
                    " group by  itemCode) b where a.itemCode = b.itemCode ";

            list1.add(sql1);

            //删除装车临时表
            sql1 = "delete from m_pos_car_load_temp";
            list1.add(sql1);
            manager.executeBatch(list1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成零售表数据
     *
     * @param orderNo
     * @param position
     * @param date
     * @param number
     * @param itemCode
     * @param name
     * @param info
     * @param unitName
     * @param itemPrice
     */
    public void getCarMarks(String orderNo,int position,String date,String number,String itemCode,String name,String info,String unitName,String itemPrice){
        try {
            ArrayList<String> list = new ArrayList<String>();
            String  sql = "insert or replace into m_pos_car_load_temp(orgCode,orgDesc,billNo,seqNo,workDate,itemCode,itemName,itemSpec,unitCode,unitName,itemPrice,qty ,wareHouseCode," +
                    "wareHouseName ,customerCode,customerName,carNumber,assistantQty ,assistantUnitCode ,assistantUnitName,entryUserCode,entryUserDesc," +
                    "entryDate,entryTime,remark,posMachineCode) select '012345','双汇肉食厂','" + orderNo + "','" + position + "','" + date + "','" + itemCode + "' ,'" + name + "','" + info + "','001100','"+ unitName +"'," +
                    "'" + itemPrice +"',ifNull(qty,'0') + '" + number + "','9900','双汇分厂','0011','零售','京B5894',1,'4465','斤','01','王师傅','2017/01/09','10:30','按期送达，完好无损','0101'" +
                    " from m_pos_car_load_temp where seqNo = '" + position + "'";
            list.add(sql);
            manager.executeBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 生成装车零售表
     * insert into app_pos_retail_all select a.orgCode,a.orgDesc,a.itemCode,a.itemName,a.itemUrl,a.itemSpec,a.itemTypeCode,a.itemTypeName,a.itemWeight,a.unitCode,a.unitName,a.itemPrice,a.qty ," +
     " a.itemAmt,a.functionCode,a.entryUserCode,a.entryUserDesc," +
     " a.entryDate,a.entryTime,a.remark from app_pos_retail_temp a
     */
    public void getRetailSaleData(String code){
        try {
            ArrayList<String> list1 = new ArrayList<String>();
            String sql1="";
            //1.从m_pos_retail_temp去装车数据到m_pos_retail_all
            sql1 = "update app_pos_retail_temp set isFinish = 'Y' where transNo='"+code+"'";
            list1.add(sql1);
            sql1 = "insert or replace into app_pos_retail_all(orgCode,orgDesc,itemCode,itemName,itemUrl,itemSpec,itemTypeCode,itemTypeName,itemWeight,unitCode," +
                            "unitName,itemPrice,qty ,itemAmt,functionCode,entryUserCode,entryUserDesc,entryDate,entryTime,remark) select " +
                            " a.orgCode,a.orgDesc,a.itemCode,a.itemName,a.itemUrl,a.itemSpec,a.itemTypeCode,a.itemTypeName,a.itemWeight,a.unitCode,a.unitName,a.itemPrice,a.qty + ifNull(b.qty,'0') ," +
                    " a.itemAmt + ifNull(b.itemAmt,'0'),a.functionCode,a.entryUserCode,a.entryUserDesc,a.entryDate,a.entryTime,a.remark " +
                    " from app_pos_retail_temp a left outer join app_pos_retail_all b  ON   a.itemCode = b.itemCode";
            list1.add(sql1);
            manager.executeBatch(list1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 更新库存  库存为    订单信息 + 零售信息 + 结存单
     */
    public void UpdateStock(){
        try {
        //订单数据
            ArrayList<String> list1 = new ArrayList<String>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String  date = sdf.format(new Date());
            String sql = "insert or replace into m_pos_stock_detail (orgCode,orgDesc,wareHouseCode,wareHouseName,itemCode,itemName,itemSpec,itemPrice," +
                " itemTypeCode,unitCode,unitName,onHandQty,onHandAmt,lotalLocationQty,assistantQty,assistantUnitCode,assistantUnitName,lastInDate,lastOutDate) " +
                " select b.orgCode,b.orgDesc,'9900','双汇分厂', b.itemCode,b.itemName,b.itemSpec,b.itemPrice," +
                " b.itemTypeCode,b.unitCode,b.unitName,sum(b.qty) ,b.itemPrice *sum(b.qty),sum(b.qty),b.assistantQty," +
                "  b.assistantUnitCode,b.assistantUnitName ,'"+ date + "','"+ date + "' from m_pos_order b left outer join m_pos_stock_detail a  " +
                "  ON b.itemCode = a.itemCode group by b.itemCode";
                //where isLoad = 'N'
            list1.add(sql);
            //sql = "update m_pos_order set isLoad = 'Y' from m_pos_order";
            // list1.add(sql);
            // 零售数据
            sql = "insert or replace into m_pos_stock_detail (orgCode,orgDesc,wareHouseCode,wareHouseName,itemCode,itemName,itemSpec,itemPrice," +
                " itemTypeCode,unitCode,unitName,onHandQty,onHandAmt,lotalLocationQty,assistantQty,assistantUnitCode,assistantUnitName,lastInDate,lastOutDate) " +
                " select b.orgCode,b.orgDesc,b.wareHouseCode,b.wareHouseName, b.itemCode,b.itemName,b.itemSpec,b.itemPrice," +
                " ifNull(a.itemTypeCode,'0'),b.unitCode,b.unitName,b.qty + ifNull(a.onHandQty,'0'),b.qty *b.itemPrice + ifNull(a.onHandAmt,'0'),ifNull(a.lotalLocationQty,'0') ,b.assistantQty ," +
                " b.assistantUnitCode,b.assistantUnitName,'"+ date + "','"+ date + "'  from  m_pos_car_load b left outer join m_pos_stock_detail a " +
                //where preConsignDate = 'date'
                " on a.itemCode = b.itemCode ";
             list1.add(sql);
            //结存单  交班后 库存剩余量
            manager.executeBatch(list1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
