package com.example.multitenant.dtos.channels;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;
import com.example.multitenant.models.Channel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AtLeastOneNotNull(fields = {"name", "order"})
public class ChannelUpdateDTO {
    @Size(min = 2, message = "name can not be less than {min} characters")
    @Size(max = 32, message = "name can not be more than {max} characters")
    private String name;
    
    @Min(value = 1, message = "order can not be less than {value}")
    private Integer displayOrder;
    
    public Channel toModel() {
        var channel = new Channel();
        channel.setName(this.getName());
        channel.setDisplayOrder(this.getDisplayOrder());
        
        return channel;
    }
}
