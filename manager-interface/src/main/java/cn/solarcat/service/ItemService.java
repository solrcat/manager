package cn.solarcat.service;

import cn.solarcat.common.pojo.EasyUIDataGridResult;
import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.pojo.TbItem;
import cn.solarcat.pojo.TbItemDesc;

public interface ItemService {
	TbItem getTbItemById(long itemId);

	EasyUIDataGridResult getItemList(int page, int rows);

	SolarCatResult addItem(TbItem tbItem, String desc);

	SolarCatResult deleteItem(Long itemId);

	TbItem getItem(Long itemId);

	SolarCatResult updateItemAndDesc(TbItem item, TbItemDesc itemDesc);

	SolarCatResult updateInstock(Long itemId);

	SolarCatResult updateReshelf(Long itemId);

	TbItemDesc getTbItemDescById(long itemId);
}
