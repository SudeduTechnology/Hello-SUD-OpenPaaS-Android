package global.sud.op.hello.service.req;

public class MockPaymentReq {
    public String app_id;
    public String user_id;
    public String sud_trade_no; // SUD业务订单号
    public String action; // 模拟支付方式，成功：SUCCESS，失败：FAILED
}
