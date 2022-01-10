package com.avatye.cashblock.base.component.domain.entity.base

import com.avatye.cashblock.R


enum class ActivityTransitionType(val value: Pair<Int, Int>) {
    NONE(Pair(0, 0)),
    SLIDE_ENTER(Pair(R.anim.acb_transition_activity_slide_enter_enter, R.anim.acb_transition_activity_slide_enter_exit)),
    SLIDE_EXIT(Pair(R.anim.acb_transition_activity_slide_exit_enter, R.anim.acb_transition_activity_slide_exit_exit)),
    FADE_EXIT(Pair(R.anim.acb_transition_activity_fade_in, R.anim.acb_transition_activity_fade_out)),
    FADE_IN(Pair(R.anim.acb_transition_activity_fade_in, R.anim.acb_transition_activity_fade_out));
}