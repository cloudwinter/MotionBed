package com.sn.blackdianqi.util.permission;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.permission.PermissionGroups;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.sn.blackdianqi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/05/30
 *    desc   : 权限转换器（根据权限获取对应的名称和说明）
 */
public final class PermissionConverter {

    /** 权限名称映射（为了适配多语种，这里存储的是 StringId，而不是 String） */
    private static final Map<String, Integer> PERMISSION_NAME_MAP = new HashMap<>();

    /** 权限描述映射（为了适配多语种，这里存储的是 StringId，而不是 String） */
    private static final Map<Integer, Integer> PERMISSION_DESCRIPTION_MAP = new HashMap<>();

    static {
        PERMISSION_NAME_MAP.put(PermissionGroups.STORAGE, R.string.common_permission_storage);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_storage, R.string.common_permission_storage_description);

        PERMISSION_NAME_MAP.put(PermissionGroups.NEARBY_DEVICES, R.string.common_permission_nearby_devices);
        // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
            // 需要填充文案：蓝牙权限描述 + WIFI 权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_nearby_devices_description);
        } else {
            // 需要填充文案：蓝牙权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_nearby_devices, R.string.common_permission_nearby_devices_description);
        }

        PERMISSION_NAME_MAP.put(PermissionGroups.LOCATION, R.string.common_permission_location);
        // 注意：在 Android 12 的时候，蓝牙相关的权限已经归到附近设备的权限组了，但是在 Android 12 之前，蓝牙相关的权限归属定位权限组
        // 注意：在 Android 13 的时候，WIFI 相关的权限已经归到附近设备的权限组了，但是在 Android 13 之前，WIFI 相关的权限归属定位权限组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)  {
            // 需要填充文案：前台定位权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_location_description);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)  {
            // 需要填充文案：前台定位权限描述 + WIFI 权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_location_description);
        } else {
            // 需要填充文案：前台定位权限描述 + 蓝牙权限描述 + WIFI 权限描述
            PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location, R.string.common_permission_location_description);
        }

        // 后台定位权限虽然属于定位权限组，但是只要是属于后台权限，都有独属于自己的一套规则
        PERMISSION_NAME_MAP.put(PermissionNames.ACCESS_BACKGROUND_LOCATION, R.string.common_permission_location_background);
        PERMISSION_DESCRIPTION_MAP.put(R.string.common_permission_location_background, R.string.common_permission_location_background_description);
    }

    /**
     * 通过权限获得名称
     */
    @NonNull
    public static String getNickNamesByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> permissionNameList = getNickNameListByPermissions(context, permissions, true);

        StringBuilder builder = new StringBuilder();
        for (String permissionName : permissionNameList) {
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (builder.length() == 0) {
                builder.append(permissionName);
            } else {
                builder.append(context.getString(R.string.common_permission_comma))
                    .append(permissionName);
            }
        }
        if (builder.length() == 0) {
            // 如果没有获得到任何信息，则返回一个默认的文本
            return context.getString(R.string.common_permission_unknown);
        }
        return builder.toString();
    }

    @NonNull
    public static List<String> getNickNameListByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions, boolean filterHighVersionPermissions) {
        List<String> permissionNickNameList = new ArrayList<>();
        for (IPermission permission : permissions) {
            // 如果当前设置了过滤高版本权限，并且这个权限是高版本系统才出现的权限，则不继续往下执行
            // 避免出现在低版本上面执行拒绝权限后，连带高版本的名称也一起显示出来，但是在低版本上面是没有这个权限的
            if (filterHighVersionPermissions && permission.getFromAndroidVersion(context) > Build.VERSION.SDK_INT) {
                continue;
            }
            String permissionName = getNickNameByPermission(context, permission);
            if (TextUtils.isEmpty(permissionName)) {
                continue;
            }
            if (permissionNickNameList.contains(permissionName)) {
                continue;
            }
            permissionNickNameList.add(permissionName);
        }
        return permissionNickNameList;
    }

    public static String getNickNameByPermission(@NonNull Context context, @NonNull IPermission permission) {
        Integer permissionNameStringId = getPermissionNickNameStringId(context, permission);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        return context.getString(permissionNameStringId);
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionsByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> descriptionList = getDescriptionListByPermissions(context, permissions);

        StringBuilder builder = new StringBuilder();
        for (String description : descriptionList) {
            if (TextUtils.isEmpty(description)) {
                continue;
            }
            if (builder.length() == 0) {
                builder.append(description);
            } else {
                builder.append("\n")
                    .append(description);
            }
        }
        return builder.toString();
    }

    @NonNull
    public static List<String> getDescriptionListByPermissions(@NonNull Context context, @NonNull List<IPermission> permissions) {
        List<String> descriptionList = new ArrayList<>();
        for (IPermission permission : permissions) {
            String permissionDescription = getDescriptionByPermission(context, permission);
            if (TextUtils.isEmpty(permissionDescription)) {
                continue;
            }
            if (descriptionList.contains(permissionDescription)) {
                continue;
            }
            descriptionList.add(permissionDescription);
        }
        return descriptionList;
    }

    /**
     * 通过权限获得描述
     */
    @NonNull
    public static String getDescriptionByPermission(@NonNull Context context, @NonNull IPermission permission) {
        Integer permissionNameStringId = getPermissionNickNameStringId(context, permission);
        if (permissionNameStringId == null || permissionNameStringId == 0) {
            return "";
        }
        String permissionNickName = context.getString(permissionNameStringId);
        Integer permissionDescriptionStringId = getPermissionDescriptionStringId(permissionNameStringId);
        String permissionDescription;
        if (permissionDescriptionStringId == null || permissionDescriptionStringId == 0) {
            permissionDescription = "";
        } else {
            permissionDescription = context.getString(permissionDescriptionStringId);
        }
        return permissionNickName + context.getString(R.string.common_permission_colon) + permissionDescription;
    }

    /**
     * 获取这个权限对应的别名 StringId
     */
    @Nullable
    public static Integer getPermissionNickNameStringId(@NonNull Context context, @NonNull IPermission permission) {
        String permissionName = permission.getPermissionName();
        String permissionGroup = permission.getPermissionGroup(context);
        Integer permissionNameStringId = PERMISSION_NAME_MAP.get(permissionName);
        if (permissionNameStringId != null && permissionNameStringId > 0) {
            return permissionNameStringId;
        }
        Integer permissionGroupStringId = PERMISSION_NAME_MAP.get(permissionGroup);
        if (permissionGroupStringId != null && permissionGroupStringId > 0) {
            return permissionGroupStringId;
        }
        return permissionNameStringId;
    }

    /**
     * 获取这个权限对应的描述 StringId
     */
    @Nullable
    public static Integer getPermissionDescriptionStringId(@IdRes int permissionNickNameStringId) {
        return PERMISSION_DESCRIPTION_MAP.get(permissionNickNameStringId);
    }
}