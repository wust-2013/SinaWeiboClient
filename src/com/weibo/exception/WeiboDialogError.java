
package com.weibo.exception;

public class WeiboDialogError extends Throwable {
    private int mErrorCode;

    private String mFailingUrl;

    public WeiboDialogError(String message, int errorCode, String failingUrl) {
        super(message);
        mErrorCode = errorCode;
        mFailingUrl = failingUrl;
    }
    /**
     * 获取错误代码
     * @return int
     */
    int getErrorCode() {
        return mErrorCode;
    }
    /**
     * 获取请求失败的url
     * @return String
     */
    String getFailingUrl() {
        return mFailingUrl;
    }

}
