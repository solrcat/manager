package cn.solarcat.service.Impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;

import cn.solarcat.aop.Log;
import cn.solarcat.common.pojo.ACTION;
import cn.solarcat.common.pojo.LEVEL;
import cn.solarcat.common.util.SolarCatResult;
import cn.solarcat.mapper.TbItemDescMapper;
import cn.solarcat.pojo.TbItemDesc;
import cn.solarcat.service.ItemDescService;

@Service
@Component
public class ItemDescServiceImpl implements ItemDescService {
	@Autowired
	private TbItemDescMapper itemDescMapper;

	@Override
	@Log(action = ACTION.SELECT, level = LEVEL.SERVICE)
	public SolarCatResult getItemDesc(Long itemId) {
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		return SolarCatResult.ok(tbItemDesc);
	}

	@Override
	@Log(action = ACTION.UPDATE, level = LEVEL.SERVICE)
	public SolarCatResult updateItemDesc(TbItemDesc tbItemDesc) {
		tbItemDesc.setUpdated(new Date());
		itemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
		return SolarCatResult.ok();
	}

}
