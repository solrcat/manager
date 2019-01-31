package cn.solarcat.service;

import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.pojo.TbItemDesc;

public interface ItemDescService {
	SolarCatResult getItemDesc(Long itemId);
	SolarCatResult updateItemDesc(TbItemDesc tbItemDesc);
}
