package com.example.multitenant.dtos.shared;

import java.util.List;

public interface IFindAllResult<TModel> {
    List<TModel> getList();
    Long getCount();
    Integer getSize();
    Integer getPage();
}
