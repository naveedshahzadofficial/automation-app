# automation-app

adb shell pm grant com.naveedshahzad.automation android.permission.WRITE_SECURE_SETTINGS

adb shell settings put global airplane_mode_on 1
adb shell am broadcast -a android.intent.action.ACTION_AIRPLANE_MODE_CHANGED --ez state 1

