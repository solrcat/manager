package cn.solarcat.service.Impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;

import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.mapper.TbItemParamItemMapper;
import cn.solarcat.pojo.TbItemParamItem;
import cn.solarcat.pojo.TbItemParamItemExample;
import cn.solarcat.pojo.TbItemParamItemExample.Criteria;
import cn.solarcat.service.ItemParamItemService;

@Service
@Component
public class ItemParamItemServiceImpl implements ItemParamItemService {
	@Autowired
	private TbItemParamItemMapper itemParamItemService;

	@Override
	public SolarCatResult getItemParamItem(long itemId) {
		TbItemParamItemExample example = new TbItemParamItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list = itemParamItemService.selectByExample(example);
		if (list != null && list.size() > 0) {
			return SolarCatResult.ok(list.get(0));
		}
		return null;
	}

	@Override
	public SolarCatResult updateItemParamItem(TbItemParamItem itemParamItem) {
		itemParamItem.setUpdated(new Date());
		itemParamItemService.updateByPrimaryKey(itemParamItem);
		return null;
	}

}
