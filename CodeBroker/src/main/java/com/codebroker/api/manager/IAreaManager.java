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

	public IArea createGrid(int loaclGridId) throws Exception;

	public Collection<IArea> getAllGrid() throws Exception;

	public void removeGrid(int loaclGridId);
}
