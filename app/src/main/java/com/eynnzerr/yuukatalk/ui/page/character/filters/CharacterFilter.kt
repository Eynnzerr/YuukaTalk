package com.eynnzerr.yuukatalk.ui.page.character.filters

import com.eynnzerr.yuukatalk.data.model.Character

interface CharacterFilter {
    fun filter(characters: List<Character>): List<Character>
}