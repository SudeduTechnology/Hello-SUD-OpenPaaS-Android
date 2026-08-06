package global.sud.op.hello.service.req;

public class ValidatePaymentReq {
    public String app_id;
    public String user_id;
    public String sign_data; // 支付原串（JSON字符串）
    public String signature; // 签名
}
