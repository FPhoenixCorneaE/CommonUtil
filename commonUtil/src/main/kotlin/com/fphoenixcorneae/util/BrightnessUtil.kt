package com.fphoenixcorneae.util

import android.Manifest
import android.annotation.SuppressLint
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.view.Window
import androidx.annotation.IntRange
import androidx.fragment.app.FragmentActivity
import com.fphoenixcorneae.ext.appContext
import com.fphoenixcorneae.permission.request

/**
 * 屏幕亮度工具类
 */
class BrightnessUtil private constructor() {

    companion object {
        /**
         * Return whether automatic brightness mode is enabled.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        val isAutoBrightnessEnabled: Boolean
            get() = try {
                val mode = Settings.System.getInt(
                    appContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
                )
                mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                false
            }

        /**
         * Enable or disable automatic brightness mode.
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         * 并且需要打开允许修改系统设置
         *
         * @param enabled True to enabled, false otherwise.
         * @return `true`: success<br></br>`false`: fail
         */
        @SuppressLint("NewApi")
        fun setAutoBrightnessEnabled(enabled: Boolean): Boolean {
            return when {
                Settings.System.canWrite(appContext) -> {
                    Settings.System.putInt(
                        appContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        when {
                            enabled -> Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                            else -> Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                        }
                    )
                }
                else -> {
                    false
                }
            }
        }

        /**
         * 获取屏幕亮度
         *
         * @return 屏幕亮度 0-255
         */
        val brightness: Int
            get() = try {
                Settings.System.getInt(
                    appContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS
                )
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                0
            }

        /**
         * 设置屏幕亮度
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         * 并且需要打开允许修改系统设置
         *
         * @param brightness 亮度值
         */
        @SuppressLint("NewApi")
        fun setBrightness(@IntRange(from = 0, to = 255) brightness: Int) {
            when {
                Settings.System.canWrite(appContext) -> {
                    if (!isAutoBrightnessEnabled) {
                        setAutoBrightnessEnabled(true)
                    }
                    val resolver = appContext.contentResolver
                    Settings.System.putInt(
                        resolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        brightness
                    )
                    resolver.notifyChange(
                        Settings.System.getUriFor("screen_brightness"),
                        null
                    )
                }
                else -> {
                    // 申请"android.permission.WRITE_SETTINGS"权限
                    (ActivityUtil.topActivity as FragmentActivity).request(Manifest.permission.WRITE_SETTINGS) {
                        onGranted {
                            setAutoBrightnessEnabled(true)
                        }
                        onDenied {
                            if (!Settings.System.canWrite(appContext)) {
                                IntentUtil.openApplicationManageWriteSettings()
                            }
                        }
                        onShowRationale {
                            if (!Settings.System.canWrite(appContext)) {
                                IntentUtil.openApplicationManageWriteSettings()
                            }
                        }
                        onNeverAskAgain {
                            if (!Settings.System.canWrite(appContext)) {
                                IntentUtil.openApplicationManageWriteSettings()
                            }
                        }
                    }
                }
            }
        }

        /**
         * 设置窗口亮度
         *
         * @param window     窗口
         * @param brightness 亮度值
         */
        fun setWindowBrightness(
            window: Window,
            @IntRange(from = 0, to = 255) brightness: Int
        ) {
            val lp = window.attributes
            lp.screenBrightness = brightness / 255f
            window.attributes = lp
        }

        /**
         * 获取窗口亮度
         *
         * @param window 窗口
         * @return 屏幕亮度 0-255
         */
        fun getWindowBrightness(window: Window): Int {
            val lp = window.attributes
            val brightness = lp.screenBrightness
            return when {
                brightness < 0 -> brightness.toInt()
                else -> (brightness * 255).toInt()
            }
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}