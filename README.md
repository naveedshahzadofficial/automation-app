# automation-app

adb shell  pm grant com.naveedshahzad.automation android.permission.WRITE_SECURE_SETTINGS

adb shell settings put global airplane_mode_on
adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state 
