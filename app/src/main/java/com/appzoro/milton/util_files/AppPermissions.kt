package com.appzoro.milton.util_files

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

class AppPermissions(private val mActivity: Activity) {

    companion object {

        fun isGPSPermissionGrantedForLocation(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 1111
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isPermissionForLocationStorage(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if ((activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) && activity.checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                            ,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1112
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun checkForLocationStorage(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) && activity.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        == PackageManager.PERMISSION_GRANTED)
            } else {
                true
            }
        }

        fun isStoragePermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), 1113
                    )
                    false
                }
            } else {
                true
            }
        }

        fun isLocationPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 1114
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isCameraWithStoragePermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if ((activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CAMERA
                    )
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ), 1115
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun checkImagePermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_GRANTED)
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun checkVideoPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                )
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_GRANTED)
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isSendMessagePermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (activity.checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.SEND_SMS
                        ), 1116
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isImagePermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if ((activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CAMERA
                    )
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ), 1117
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isVideoPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if ((activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECORD_AUDIO
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CAMERA
                    )
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                        ), 1118
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun checkCameraStorageAudioPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                )
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                        == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_GRANTED)
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isMessagePermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if ((activity.checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECEIVE_SMS
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECEIVE_MMS
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_PHONE_STATE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CHANGE_NETWORK_STATE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECORD_AUDIO
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CAMERA
                    )
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.RECEIVE_MMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                        ), 1119
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isAllPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if ((activity.checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECEIVE_SMS
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECEIVE_MMS
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_PHONE_STATE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CHANGE_NETWORK_STATE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.RECORD_AUDIO
                    )
                            == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(
                        Manifest.permission.CAMERA
                    ) //                    == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.FLASHLIGHT)
                            == PackageManager.PERMISSION_GRANTED)
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.RECEIVE_MMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                        ), 1110
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

        fun isCallingPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                if (activity.checkSelfPermission(Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    activity.requestPermissions(
                        arrayOf(
                            Manifest.permission.CALL_PHONE
                        ), 1121
                    )
                    false
                }
            } else {
                true
            }
        }
    }

}