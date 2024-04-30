package com.harsh.chatm.Interfaces

import android.view.View
import com.harsh.chatm.DataClasses.User

interface OnMenuDotClick {
    fun onMenuDotClick(user: User, holder: View)
}