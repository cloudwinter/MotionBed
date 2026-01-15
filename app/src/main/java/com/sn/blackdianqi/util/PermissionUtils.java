package com.sn.blackdianqi.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.sn.blackdianqi.util.permission.PermissionDescription;
import com.sn.blackdianqi.util.permission.PermissionInterceptor;

import java.util.List;


/**
 * 获取权限工具类
 *
 * @author CJZ
 * @Time 2018/11/23
 */
public class PermissionUtils {

    /**
     * 权限组申请
     */
    public static void requestPermission( Context context,  PermissionCallBack callBack) {
        XXPermissions.with(context)
                .permission(PermissionLists.getBluetoothConnectPermission())
                .permission(PermissionLists.getBluetoothScanPermission())
                .permission(PermissionLists.getAccessFineLocationPermission())
                // 设置权限请求拦截器（局部设置）
                .interceptor(new PermissionInterceptor())
                .description(new PermissionDescription())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                        boolean allGranted = deniedList.isEmpty();
                        if (!allGranted) {
                            callBack.onFailure();
                        } else {
                            callBack.onSuccess();
                        }
                    }
                });
    }

    /**
     * 回调
     */
    public interface PermissionCallBack {
        void onSuccess();

        void onFailure();
    }

}
