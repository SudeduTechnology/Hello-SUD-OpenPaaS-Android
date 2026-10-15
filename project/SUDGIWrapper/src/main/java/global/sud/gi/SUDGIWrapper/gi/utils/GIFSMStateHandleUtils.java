/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package global.sud.gi.SUDGIWrapper.gi.utils;

import global.sud.gi.SUDGIWrapper.gi.state.GIStateResponse;
import global.sud.gi.core.ISUDFSMStateHandle;

public class GIFSMStateHandleUtils {

    /**
     * 回调游戏，成功
     *
     * @param handle
     */
    public static void handleSuccess(ISUDFSMStateHandle handle) {
        GIStateResponse response = new GIStateResponse();
        response.ret_code = GIStateResponse.SUCCESS;
        response.ret_msg = "success";
        handle.success(GIJsonUtils.toJson(response));
    }

}
