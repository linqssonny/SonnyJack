package com.sonnyjack.library.bean.http

open class BaseDownloadInfo {

    companion object {
        var SUCCESS_CODE = 200

        /************ 错误码 **************/
        var ERROR_CODE_DEFAULT = -1//默认失败码
        var ERROR_CODE_URL_NULL = -2//下载链接为空
    }

    var downloadUrl: String? = null
    var message: String? = null//error message
    var code: Int = SUCCESS_CODE//错误码
    var saveFilePath: String? = null//文件的保存地址

    fun isSuccess(): Boolean {
        return code == SUCCESS_CODE
    }
}