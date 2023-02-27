package com.sw.mobsale.online.util;

import com.sw.mobsale.online.R;

/**
 * 常量
 */
public class Constant {
    /**
     * indent submit address
     */
    public static final int ADDRESS_SUBMIT = 10;
    /**
     * 省
     */
    public static final int ADDRESS_P = 11;
    /**
     * 市
     */
    public static final int ADDRESS_CITY = 12;
    /**
     * 区
     */
    public static final int ADDRESS_COUNTRY = 13;
    /**
     *store query
     */
    public static final int STORE_QUERY = 14;
    /**
     *conn blue
     */
    public static final int BLUE_CON = 15;
    /**
     * 完成支付
     */
    public static final int FINISH_PAY = 21;
    /**
     * 打印
     */
    public static final int DATA_PRINT = 22;
    /**
     * data查询
     */
    public static final int DATA_QUERY_ALL =23;
    /**
     * data 订单查询
     */
    public static final int DATA_QUERY_ORDER =24;

    /**
     * data 零售查询
     */
    public static final int DATA_QUERY_RETAIL =25;
    /**
     * 司机
     */
    public static final int WORKER_NO = 26;
    /**
     * 确定司机
     */
    public static final int WORKER_NO_SUBMIT = 27;

    /**
     * data 订单查询
     */
    public static final int DATA_QUERY_ORDER_TYPE =28;
    /**
     * start-activity worker car
     */
    public static final int WORKER_CAR =29;
    /**
     * 切换用户退出
     */
    public static final int USER_BACK = 31;
    /**
     * 切换用户登录
     */
    public static final int USER_LOGIN = 32;
    /**
     * 车辆
     */
    public static final int CAR_NO = 33;
    /**
     * 选择商铺
     */
    public static final int CHOOSE_STORE = 34;
    /**
     * 再次进入mainfragment
     */
    public static final int MAIN_DETAIL =35;
    /**
     * 确定车辆
     */
    public static final int SUBMIT_CAR =36;
    /**
     *登录
     */
    public static final int VERIFY_CODE = 37;
    /**
     *登录
     */
    public static final int FORCE_DOWN = 38;
    /**
     * 选择司机
     */
    public static final int NO_WORKER =39;
    /**
     * 选择车辆
     */
    public static final int NO_CAR =40;
    /**
     * 订单头信息
     */
    public static final int LOAD_ORDER = 41;
    /**
     * 订单明细
     */
    public static final int ORDER_DETAIL = 42;
    /**
     * 路线信息
     */
    public static final int ROUTE_LINE = 43;
    /**
     * 零售信息
     */
    public static final int LOAD_SALE = 44;
    /**
     * 路线信息
     */
    public static final int SAOMA_ORDER = 45;
    /**
     * 装车零售汇总
     */
    public static final int LOAD_SALE_ALL = 46;
    /**
     * 零售详情
     */
    public static final int SALE_SHOP_CAR = 48;
    /**
     * 加入后台零售
     */
    public static final int ADD_SALE_SHOP_CAR = 49;
    /**
     *login->fpwd
     */
    public static final int INTENT_FPWD = 50;
    /**
     * load->loadOrder
     */
    public static final int LOAD_LOADORDER = 51;
    /**
     * loadSale -> right
     */
    public static final int SALE_CHOOSE = 53;
    /**
     * loadOrder -> right
     */
    public static final int ORDER_CHOOSE = 54;
    /**
     * sell->right
     */
    public static final int SELL_RIGHT = 55;
    /**
     * sell->test
     */
    public static final int SELL_TEST = 56;
    /**
     * pay TYPE
     */
    public static final int PAY_TYPE = 61;
    /**
     * pay URL
     */
    public static final int PAY_TYPE_URL = 62;
    /**
     * pay URL  商户账期
     */
    public static final int PAY_DEBT_URL = 63;
    /**
     * pay URL  商户账期  记账
     */
    public static final int PAY_DEBT_BOOK_URL = 64;


    /**
     * 交班 A - > 头  D ->model  E-> 确认交班 R -> 剩余商品合计（铺货+订单） S->零售商品  T->零售产品汇总
     */
    public static final int CONNECT_A = 71;
    public static final int CONNECT_D = 72;
    public static final int CONNECT_E = 73;
    public static final int CONNECT_R = 74;
    public static final int CONNECT_T = 75;

    /**
     * 零售S
     *  status='A',表示增加到销售购物车。。status='D'销售零售单购物车产品列表 。
     *      ,status='T' 实时库存 res 零售库存汇总查询  satus ="S" 确认订单  ，返回单号
     *      status = "U" 零售数量更新 sell  status = "K" 零售删除单品 sell
     *      STATUS = "G" 零售单挂单res   STATUS =C 挂单明细
      */
    public static final int SALE_RETAIL_A = 81;
    public static final int SALE_RETAIL_D = 82;
    public static final int SALE_RETAIL_U = 83;
    public static final int SALE_RETAIL_K = 84;
    public static final int SALE_RETAIL_T = 85;
    public static final int SALE_RETAIL_S = 86;
    public static final int SALE_RETAIL_G = 87;
    public static final int SALE_RETAIL_B = 88;
    public static final int SALE_RETAIL_C = 89;

    /**
     * 在线处理订单,A= 在线订单查询   O= 下发订单，G =扫码下发订单  D= 订单明细，  S = 配送完毕订单 ，B未配送商户
     * U = 未配送订单， K = 删除订单，C = 查询所有下发订单+生成的零售单 E =未配送商品合计  I -路线店铺
     */
    public static final int ORDER_STATUS_G = 90;
    public static final int ORDER_STATUS_A = 91;
    public static final int ORDER_STATUS_O = 92;
    public static final int ORDER_STATUS_K = 93;
    public static final int ORDER_STATUS_Y = 94;
    public static final int ORDER_STATUS_U = 95;
    public static final int ORDER_STATUS_S = 96;
    public static final int ORDER_STATUS_B = 97;
    public static final int ORDER_STATUS_E = 98;
    public static final int ORDER_STATUS_F = 99;
    public static final int ORDER_STATUS_I = 100;

    /**
     * listview图标
     */
    public static final int[]image1 = new int[]{R.drawable.item_order_bg1, R.drawable.item_order_bg2, R.drawable.item_order_bg3, R.drawable.item_order_bg4, R.drawable.item_order_bg5,};

    /**
     * listview图标
     */
    public static final int[]image = new int[]{R.drawable.item_sell_bg_1, R.drawable.item_sell_bg_2, R.drawable.item_sell_bg_3, R.drawable.item_sell_bg_4, R.drawable.item_sell_bg_5,};
    /**
     * URL
     */
//    public static final String URL = "http://124.193.193.35:1011/b2b/mobsale";
//      public static final String URL = "http://124.193.193.34:8181/b2b/mobsale";
//      public static final String URL = "http://219.238.112.246:8030/b2b/mobsale";
  public static final String URL = "http://mobsale.shineway-soft.com/b2b/mobsale";
    /**
     * 登陆请求路径
     */
    public static final String LOGIN_PATH = URL +"/appCarlogin/A";
    /**
     * 验证码请求路径
     */
    public static final String VERIFY_CODE_PATH =URL+"/appCarlogin/B";
    /**
     * 退出请求路径
     */
    public static final String BACK_PATH =URL+"/appCarlogin/C";
    /**
     * 强制退出请求路径
     */
    public static final String FORCE_BACK_PATH =URL+"/appCarlogin/D";
    /**
     * 测试请求路径
     */
    public static final String TEST_SERVER_PATH =URL+"/appCarlogin/E";
    /**
     * 确定车辆请求路径
     */
    public static final String CAR_CHOOSE_PATH =URL+"/appCarNum/C";
    /**
     * MAIN请求路径
     */
    public static final String LOGIN_DETAIL_PATH =URL+"/appCarNum/B";
    /**
     * 车辆请求路径
     */
    public static final String CARNUM_PATH =URL+"/appCarNum/A";
    /**
     * 司机请求路径
     */
    public static final String WORKER_PATH =URL+"/appCarDriver/A";
    /**
     * 确定司机请求路径
     */
    public static final String WORKER_CHOOSE_PATH =URL+"/appCarDriver/C";
    /**
     *   loadsale零售明细请求路径
     */
    public static final String LOAD_SALE_PATH_A =URL+"/appCarItem/A";
    /**
     *   retail  零售明细请求路径
     */
    public static final String RETAIL_SALE_PATH_S =URL+"/appCarItem/S";
    /**
     * 后台 -> 零售明细请求路径  加入 retail_temp  a ->loadsale  d ->筛选 loadsale  l-> right分类
     */
    public static final String LOAD_SALE_PATH_RETAIL =URL+"/appCarRetail/A";
    /**
     * 零售销售-> 零售明细请求路径 retail/sell    status ->a 添加   ->d 查询  ->u 更新  ->k 删除 m->Address c->挂单明细
     */
    public static final String SALE_PATH_RETAIL =URL+"/appCarRetail/S";
    /**
     * 路线请求路径
     */
    public static final String LINE_ROUTE_PATH =URL+"/appCarlines/A";
    /**
     * 路线商铺请求路径
     */
    public static final String LINE_ROUTE_PATH_STORE =URL+"/appCarlines/S";
    /**
     * 订单头请求路径
     */
    public static final String LOAD_ORDER_PATH = URL+"/appCarOrder/A";
    /**
     * 订单路线请求路径
     */
    public static final String LOAD_ORDER_ROUTE_PATH_ONLINE =URL+"/appCarOrder/Q";
    /**
     * 订单明细请求路径
     */
    public static final String LOAD_ORDER_DETAIL_PATH =URL+"/appCarOrder/D";
    /**
     * 订单明细请求路径
     */
    public static final String TWO_ORDER_DETAIL_PATH =URL+"/appCarOrder/B";
    /**
     * pay请求路径
     */
    public static final String PAY_PATH =URL+"/appPayMent/A";
    /**
     * 完成支付请求路径
     */
    public static final String PAY_PATH_ORDER =URL+"/appCarSale/";
    /**
     * 账期完成支付请求路径
     */
    public static final String PAY_PATH_ORDER_DEBT =URL+"/appCarSale/Z";
    /**
     * data请求路径
     */
    public static final String DATA_PRINT_PATH_ALL =URL+"/appCarSale/S";
    /**
     * data请求路径
     */
    public static final String CONNECT_PATH =URL+"/appHandOver/Q";




}
