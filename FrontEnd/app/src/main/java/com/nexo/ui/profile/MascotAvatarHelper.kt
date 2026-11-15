package com.nexo.ui.profile

import com.nexo.R
import com.nexo.ui.theme.AppStrings

object MascotAvatarHelper {
    fun getOptions(): List<MascotAvatarOption> = listOf(
        MascotAvatarOption("default", R.drawable.mascote_default, AppStrings.cdMascotDefault),
        MascotAvatarOption("focado", R.drawable.mascote_focado, AppStrings.cdMascotStudying),
        MascotAvatarOption("calma", R.drawable.mascote_calma, AppStrings.cdMascotCalm),
        MascotAvatarOption("campeao", R.drawable.mascote_campeao, AppStrings.cdMascotChampion),
        MascotAvatarOption("apoio", R.drawable.mascote_apoio, AppStrings.cdMascotSupport)
    )

    fun getResIdById(avatarId: String?): Int {
        return getOptions().find { it.id == avatarId }?.resId ?: R.drawable.mascote_default
    }

    fun getIdByResId(resId: Int): String {
        return getOptions().find { it.resId == resId }?.id ?: "default"
    }
}