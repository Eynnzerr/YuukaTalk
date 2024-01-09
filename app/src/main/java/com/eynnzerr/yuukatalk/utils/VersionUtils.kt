package com.eynnzerr.yuukatalk.utils

import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import java.util.regex.Pattern

class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
) {
    private fun toNumber() = major * 10000 + minor * 100 + patch * 1

    operator fun compareTo(other: Version): Int =
        this.toNumber().compareTo(other.toNumber())
}

object VersionUtils {

    private val pattern = Pattern.compile("""v?(\d+)\.(\d+)\.(\d+)(-(\w+)\.(\d+))?""")

    fun convertTagToVersion(tag: String): Version {
        val matcher = pattern.matcher(tag)
        return if (matcher.find()) {
            val major = matcher.group(1)?.toInt() ?: 0
            val minor = matcher.group(2)?.toInt() ?: 0
            val patch = matcher.group(3)?.toInt() ?: 0
            Version(major, minor, patch)
        } else Version(0, 0, 0)
    }

    fun getLocalVersion(): String {
        val context = YuukaTalkApplication.context
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
}