package com.harsh.streamingapp.utils

object Constants {
    // Firebase
    const val KEY_NAME: String = "name" // for name
    const val KEY_EMAIL: String = "email" // for email
    const val KEY_PROFILE: String = "profile" // for profile image

    const val REMOTE_MSG_TYPE: String = "type"
    const val REMOTE_MSG_INVITATION: String = "invitation"
    const val REMOTE_MSG_MEETING_TYPE: String = "meetingType" // call type
    const val REMOTE_MSG_INVITER_TOKEN: String = "inviterToken" // invitation sent by

    const val REMOTE_MSG_INVITATION_RESPONSE: String = "invitationResponse"

    // these are call responses
    const val REMOTE_MSG_INVITATION_ACCEPTED: String = "accepted"
    const val REMOTE_MSG_INVITATION_REJECTED: String = "rejected"
    const val REMOTE_MSG_INVITATION_CANCELLED: String = "cancelled"

    // this is call ID
    const val REMOTE_MSG_MEETING_ROOM: String = "meetingRoom"

    // fcm scope url
    const val FCM_SCOPE: String = "https://www.googleapis.com/auth/firebase.messaging"
}