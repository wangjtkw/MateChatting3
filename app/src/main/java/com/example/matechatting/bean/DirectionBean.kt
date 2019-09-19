package com.example.matechatting.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class BigDirectionBean(
    @SerializedName("direction_name")
    var directionName: String,
    @PrimaryKey
    var id: Int,
    var isSelect: Boolean = false,
    @Ignore
    var isSelectCurrent: Boolean = false

)

@Entity(tableName = "direction")
data class DirectionBean(
    @PrimaryKey
    val id: Int,
    @SerializedName("direction_name")
    var directionName: String = "",
    @ColumnInfo(name = "parent_id")
    var parentId: Int = -1,
    var bigDirectionId: Int = 0,
    @ColumnInfo(name = "is_small")
    var isSmall: Boolean = false,
    @ColumnInfo(name = "is_select")
    var isSelect: Boolean = false

)

data class SaveDirectionBean(
    var bigDirectionId: Int = 0,
    var normalDirectionList: List<NormalDirection>? = null
) {
    data class NormalDirection(
        var direction: Direction? = null,
        var smallDirection: List<Direction>? = null
    )
}

data class SmallDirectionBean(
    @SerializedName("children")
    var children: List<ChildrenNormal>, //中标签集合，当没有中标签时，为小标签
    @SerializedName("direction")
    var direction: BigDirection
) {
    //大标签id
    data class BigDirection(
        @SerializedName("id")
        var id: Int
    )

    data class ChildrenNormal(
        @SerializedName("children")
        var children: List<Children>,//当该集合为空时，该集合父集合为小标签
        @SerializedName("direction")
        var direction: Direction//小标签或中标签的name 和id
    ) {

        data class Children(
            @SerializedName("children")
            var children: List<Any>,
            @SerializedName("direction")
            var direction: Direction
        )
    }

}

data class Direction(
    @SerializedName("direction_name")
    var directionName: String = "",
    @SerializedName("id")
    var id: Int = 0,
    var isSelect: Boolean = false
)