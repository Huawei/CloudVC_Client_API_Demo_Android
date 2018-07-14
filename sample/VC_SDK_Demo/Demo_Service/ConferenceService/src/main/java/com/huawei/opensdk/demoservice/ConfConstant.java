package com.huawei.opensdk.demoservice;


public class ConfConstant {

    /**
     * Book conference status.
     */
    public enum BookConfStatus
    {
        IDLE(),
        INSTANT_BOOKING(),
        RESERVED_BOOKING()
    }

    /**
     * Conference convening status.
     */
    public enum ConfConveneStatus
    {
        UNKNOWN(),
        SCHEDULE(),
        CREATING(),
        GOING(),
        DESTROYED()
    }

    public enum ParticipantStatus
    {
        IN_CONF(),
        CALLING(),
        JOINING(),
        LEAVED(),
        NO_EXIST(),
        BUSY(),
        NO_ANSWER(),
        REJECT(),
        CALL_FAILED(),
        UNKNOWN()
    }

    public enum CONF_EVENT {
        BOOK_CONF_SUCCESS(),    //预约会议成功
        BOOK_CONF_FAILED(),     //预约会议失败

        JOIN_CONF_SUCCESS(),
        JOIN_CONF_FAILED(),

        ADD_YOURSELF_FAILED(),
        ADD_ATTENDEE_RESULT(),
        DEL_ATTENDEE_RESULT(),

        MUTE_ATTENDEE_RESULT(),
        UN_MUTE_ATTENDEE_RESULT(),

        REQUEST_CHAIRMAN_RESULT(),
        RELEASE_CHAIRMAN_RESULT(),

        WILL_TIMEOUT(),
        POSTPONE_CONF_RESULT(),

        SET_CONF_MODE_RESULT(),
        GET_DATA_CONF_PARAM_RESULT(),

        UPGRADE_CONF_RESULT(),

        SPEAKER_LIST_IND(),
        STATE_UPDATE(),

        JOIN_DATA_CONF_RESULT(),
        JOIN_DATA_CONF_LEAVE(),
        JOIN_DATA_CONF_TERMINATE(),

        CAMERA_STATUS_UPDATE(),

        CHAIRMAN_INFO(),//请求主席信息
        CHAIRMAN_RELEASE_IND(),//释放主席信息

        WATCH_ATTENDEE_RESULT(),
        BROADCAST_ATTENDEE_RESULT(),
        CANCEL_BROADCAST_ATTENDEE_RESULT(),

        BUTT()
    }

    /**
     * Conference media type
     */
    public enum ConfMediaType {
        VOICE_CONF(),
        VIDEO_CONF(),
        VOICE_AND_DATA_CONF(),
        VIDEO_AND_DATA_CONF()
    }

    /**
     * Conference role
     */
    public enum ConfRole {
        CHAIRMAN(),
        ATTENDEE()
    }

    /**
     * Conference right
     */
    public enum ConfRight {
        MY_CREATE(),
        MY_JOIN(),
        MY_CREATE_AND_JOIN()
    }

}
