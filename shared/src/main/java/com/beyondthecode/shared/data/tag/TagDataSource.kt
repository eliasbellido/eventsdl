package com.beyondthecode.shared.data.tag

import com.beyondthecode.model.Tag

interface TagDataSource{
    fun getTags(): List<Tag>
}