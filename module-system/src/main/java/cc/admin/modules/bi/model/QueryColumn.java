package cc.admin.modules.bi.model;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import lombok.Data;

@Data
public class QueryColumn {
    private String name;
    private String type;

    public QueryColumn(String name, String type) {
        this.name = name;
        this.type = type.toLowerCase();
    }

    public void setType(String type) {
        this.type = type == null ? Constants.EMPTY : type;
    }
}
