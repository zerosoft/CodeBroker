package com.codebroker.api.manager;

import com.codebroker.api.IArea;

import java.util.Collection;

/**
 * 区域管理器
 *
 * @author xl
 */
public interface IAreaManager {

    public IArea createArea(int loaclAreaId) throws Exception;

    public IArea getAreaById(String areaId) throws Exception;

    public Collection<IArea> getAllArea() throws Exception;

    public void removeArea(int loaclAreaId);
}
