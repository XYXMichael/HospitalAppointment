package com.xyx.travelingshare.entity.node_section

import com.chad.library.adapter.base.entity.node.BaseExpandNode
import com.chad.library.adapter.base.entity.node.BaseNode

class RootNode(var childNodes:List<BaseNode>, var title:String):BaseExpandNode(){
    init{
        this.title = title
    }

    override val childNode: MutableList<BaseNode>?
        get() = childNodes as MutableList<BaseNode>
}