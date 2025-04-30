package com.example.multitenant.dtos.shared;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.example.multitenant.dtos.apiresponse.ApiResponses;

import lombok.*;

@Getter
@Setter
public class CursorPage<TModel, ID extends Serializable> {
    private List<TModel> data;
    private ID nextCursor;
    private boolean hasNext;

    public static <TModel, ID extends Serializable> CursorPage<TModel, ID> of(List<TModel> data, ID nextCursor, boolean hasNext) {
        return new CursorPage<TModel,ID>(data, nextCursor, hasNext);
    };

    private CursorPage(List<TModel> data, ID nextCursor, boolean hasNext) {
        setData(data);
        setNextCursor(nextCursor);
        setHasNext(hasNext);
    }

    public Object toApiResponse(String modelKey, Function<List<TModel>, List<?>> listModifier) {
        return ApiResponses.CursorResponse(modelKey, listModifier == null ? this.getData() : listModifier.apply(this.getData()), this.isHasNext(), this.getNextCursor());
    }
}
