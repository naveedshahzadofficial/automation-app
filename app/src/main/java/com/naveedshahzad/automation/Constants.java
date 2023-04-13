package com.naveedshahzad.automation;

final class Constants
{
    static final String WEBSITE_URL = "websiteUrl";
    static final String MY_BROADCAST_TASKS = "com.naveedshahzad.tasks";

    static final String COMMAND_FLIGHT_MODE = "settings put global airplane_mode_on ";
    static final String COMMAND_FLIGHT_MODE_BROADCAST = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state ";

}
