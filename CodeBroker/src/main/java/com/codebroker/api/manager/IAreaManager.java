package com.codebroker.api.manager;

import java.util.Collection;

import com.codebroker.api.IArea;

/**
 * 区域管理器
 * 
 * @author xl
 *
 */
public interface IAreaManager {

	public IArea createArea(int loaclAreaId) throws Exception;

	public IArea getAreaById(String areaId) throws Exception;
	
	public Collection<IArea> getAllArea() throws Exception;

	public void removeArea(int loaclAreaId);
}
