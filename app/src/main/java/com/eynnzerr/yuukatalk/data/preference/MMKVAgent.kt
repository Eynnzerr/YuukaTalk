package com.eynnzerr.yuukatalk.data.preference

import com.tencent.mmkv.MMKV

object MMKVAgent {
    private val instance = MMKV.defaultMMKV()

    // int
    fun encodeInt(key: String, value: Int) = instance.encode(key, value)
    fun decodeInt(key: String, defaultValue: Int) = instance.decodeInt(key, defaultValue)

    // boolean
    fun encodeBoolean(key: String, value: Boolean) = instance.encode(key, value)
    fun decodeBoolean(key: String) = instance.decodeBool(key, false)

    // String
    fun encodeString(key: String, value: String) = instance.encode(key, value)
    fun decodeString(key: String) = instance.decodeString(key) ?: ""

    fun containsKey(key: String) = instance.containsKey(key)
}