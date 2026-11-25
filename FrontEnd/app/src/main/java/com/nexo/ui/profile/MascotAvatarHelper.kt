package com.nexo.ui.profile

import com.nexo.R
import com.nexo.ui.theme.AppStrings

object MascotAvatarHelper {
    fun getOptions(): List<MascotAvatarOption> = listOf(
        MascotAvatarOption("default", R.drawable.mascote_default, AppStrings.cdMascotDefault),
        MascotAvatarOption("focado", R.drawable.mascote_focado, AppStrings.cdMascotStudying),
        MascotAvatarOption("acolhedor", R.drawable.mascote_acolhedor, AppStrings.cdMascotWelcoming),
        MascotAvatarOption("campeao", R.drawable.mascote_campeao, AppStrings.cdMascotChampion),
        MascotAvatarOption("explorador", R.drawable.mascote_explorador, AppStrings.cdMascotExplorer),
        MascotAvatarOption("desbravador", R.drawable.mascote_desbravador, AppStrings.cdMascotTrailblazer),
        MascotAvatarOption("aventureiro", R.drawable.mascote_aventureiro, AppStrings.cdMascotAdventurer),
        MascotAvatarOption("pensativo", R.drawable.mascote_pensativo, AppStrings.cdMascotThoughtful),
        MascotAvatarOption("calma", R.drawable.mascote_calma, AppStrings.cdMascotCalm),
        MascotAvatarOption("apoio", R.drawable.mascote_apoio, AppStrings.cdMascotSupport),
        MascotAvatarOption("amizade", R.drawable.mascote_amizade, AppStrings.cdMascotFriendship),
        MascotAvatarOption("estrela", R.drawable.mascote_estrela, AppStrings.cdMascotStar)
    )

    fun getResIdById(avatarId: String?): Int {
        return getOptions().find { it.id == avatarId }?.resId ?: R.drawable.mascote_default
    }

    fun getIdByResId(resId: Int): String {
        return getOptions().find { it.resId == resId }?.id ?: "default"
    }
}