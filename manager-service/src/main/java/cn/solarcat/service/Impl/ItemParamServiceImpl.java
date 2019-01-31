package cn.solarcat.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.solarcat.aop.Log;
import cn.solarcat.common.pojo.ACTION;
import cn.solarcat.common.pojo.EasyUIDataGridResult;
import cn.solarcat.common.pojo.LEVEL;
import cn.solarcat.mapper.TbItemParamMapper;
import cn.solarcat.pojo.TbItemParam;
import cn.solarcat.pojo.TbItemParamExample;
import cn.solarcat.service.ItemParamService;

@Service
@Component
public class ItemParamServiceImpl implements ItemParamService {
	@Autowired
	private TbItemParamMapper itemParamMapper;

	@Override
	@Log(action = ACTION.SELECT, level = LEVEL.SERVICE)
	public EasyUIDataGridResult getItemParamList(int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbItemParamExample example = new TbItemParamExample();
		List<TbItemParam> list = itemParamMapper.selectByExample(example);
		// 创建一个返回值对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		// 取分页结果
		PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
		// 取总记录数
		long total = pageInfo.getTotal();
		result.setTotal((int) total);
		return result;
	}

}
