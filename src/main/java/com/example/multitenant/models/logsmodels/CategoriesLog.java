package com.example.multitenant.models.logsmodels;

import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.Category;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.LogEventType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@DiscriminatorValue("CATEGORY")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories_logs")
public class CategoriesLog extends BaseOrganizationsLogs {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String getMessage() {
        var userName = this.getUser() == null ? "NOT FOUND" : this.getUser().getFirstName();
        var categoryName = this.getCategory() == null ? "NOT FOUND" : this.getCategory().getName();

        var message = "";
        if(this.getEventType().equals(LogEventType.ORG_CATEGORY_CREATED)) {
            message = String.format("user '%s' has created a new category with name '%s'", userName, categoryName);

        } else if(this.getEventType().equals(LogEventType.ORG_CATEGORY_UPDATED)) {
            message = String.format("user '%s' has updated a category with name '%s'", userName, categoryName);

        } else if(this.getEventType().equals(LogEventType.ORG_CATEGORY_DELETED)) {
            message = String.format("user '%s' has deleted a category with name '%s'", userName, categoryName);
            
        } else {
            throw new UnknownException("invalid event type");
        }

        return message;
    }
}
