package global.sud.op.hello.service;

import global.sud.op.hello.common.http.param.BaseResponse;
import global.sud.op.hello.common.http.param.IBaseUrl;
import global.sud.op.hello.service.req.GameLoginReq;
import global.sud.op.hello.service.req.GetUserProfileReq;
import global.sud.op.hello.service.req.MockPaymentReq;
import global.sud.op.hello.service.req.ValidatePaymentReq;
import global.sud.op.hello.service.resp.GameLoginResp;
import global.sud.op.hello.service.resp.GetUserProfileResp;
import global.sud.op.hello.service.resp.MockPaymentResp;
import global.sud.op.hello.service.resp.ValidatePaymentResp;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * 网络请求方法和地址
 */
public interface MainRequestMethod {

    /** 游戏登录URL */
    String GAME_LOGIN = "v1/app/generate/user/signature";

    /** 获取用户敏感信息 */
    String USER_PROFILE = "v1/app/get/user/profile";

    /** 模拟支付 */
    String MOCK_PAYMENT = "v1/app/pay/mock";

    /** 验证订单 */
    String VALIDATE_PAYMENT = "v1/app/pay/validate";

    /** 游戏登录 */
    @POST(GAME_LOGIN)
    Observable<BaseResponse<GameLoginResp>> gameLogin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GameLoginReq req);

    /** 获取用户敏感信息 */
    @POST(USER_PROFILE)
    Observable<BaseResponse<GetUserProfileResp>> getUserProfile(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GetUserProfileReq req);

    /** 模拟支付 */
    @POST(MOCK_PAYMENT)
    Observable<BaseResponse<MockPaymentResp>> mockPayment(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body MockPaymentReq req);

    /** 验证订单 */
    @POST(VALIDATE_PAYMENT)
    Observable<BaseResponse<ValidatePaymentResp>> validatePayment(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body ValidatePaymentReq req);

}
