<#list fmColumnList as fm>
    <#if fm.dicTable != "" && fm.dynamicDic == "1">
  // 以下内容复制到 SysDictItemController 的 afterPropertiesSet 中
  dictTableMap.put("${fm.optionsName}", new DictTable("${fm.dicTable}", "${fm.dicCode}", "${fm.dicText}"));
    </#if>
</#list>
