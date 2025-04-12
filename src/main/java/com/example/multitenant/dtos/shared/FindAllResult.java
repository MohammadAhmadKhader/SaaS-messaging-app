package com.example.multitenant.dtos.shared;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindAllResult<TModel> implements IFindAllResult<TModel> {
    List<TModel> list;
    Long count;
    Integer page;
    Integer size;
}
