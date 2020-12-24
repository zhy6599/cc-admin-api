package cc.admin.modules.bi.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Order {
    @NotBlank(message = "Invalid order column")
    private String column;

    private String direction = "asc";

    public Order(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    public Order() {
    }
}
