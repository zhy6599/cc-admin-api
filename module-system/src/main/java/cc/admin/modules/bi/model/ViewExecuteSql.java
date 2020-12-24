package cc.admin.modules.bi.model;

import lombok.Data;
import cc.admin.modules.bi.core.model.SqlVariable;

import java.util.List;

@Data
public class ViewExecuteSql {
    private String sourceId;

    private String sql;

    private List<SqlVariable> variables;

    private int limit = 0;
    private int pageNo = -1;
    private int pageSize = -1;
}
