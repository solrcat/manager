package cn.solarcat.service;

import java.util.List;

import cn.solarcat.common.pojo.EasyUITreeNode;

public interface ItemCatService {
	List<EasyUITreeNode> getItemCatList(long parentId);
}
