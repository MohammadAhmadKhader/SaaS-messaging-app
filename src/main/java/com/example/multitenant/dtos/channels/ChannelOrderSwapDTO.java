package com.example.multitenant.dtos.channels;

import com.example.multitenant.common.validators.contract.AllDifferentIntegerFields;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllDifferentIntegerFields(fieldNames = {"channelId1", "channelId2"})
public class ChannelOrderSwapDTO {
    @Min(value = 1, message = "first channel can not be less than {value}")
    @NotNull(message = "first channel id is required")
    private Integer channelId1;

    @Min(value = 1, message = "second channel can not be less than {value}")
    @NotNull(message = "second channel id is required")
    private Integer channelId2;
}