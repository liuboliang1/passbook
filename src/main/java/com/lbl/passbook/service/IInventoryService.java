package com.lbl.passbook.service;

import com.lbl.passbook.vo.Response;

public interface IInventoryService {
    Response getInventoryInfo(Long userId) throws Exception;
}
