package cn.solarcat.service;

import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.pojo.TbItemParamItem;

public interface ItemParamItemService {
	SolarCatResult getItemParamItem(long itemId);
	SolarCatResult updateItemParamItem(TbItemParamItem itemParamItem);
}
