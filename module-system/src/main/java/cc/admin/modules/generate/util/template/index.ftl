<template>
    <q-page class="${pageClass}">
        ${viewCatalog!''}
        <div class="col bg-white shadow-2 q-pa-md q-ma-sm">
        <#if generate.isSimpleQuery == "0">
            <#include "include/queryForm.ftl" />
        </#if>
            <q-table flat color="primary" class="cross_table" separator="cell"
                     :columns="columns" :data="list" row-key="id"
                     :pagination.sync="pagination"
                     :visible-columns="group"
                     @request="query"
                     :rows-per-page-options="[10,20,50,100]"
                     :loading="loading"
                     selection="multiple"
                     :selected.sync="selected">

                <template v-slot:top="table">
                    <div class="row no-wrap full-width">
<#if generate.isSimpleQuery == "1">
    <#include "include/simpleQuery.ftl" />
</#if>
                        <q-space />
    <#include "include/tableOptBtn.ftl" />
                    </div>
                </template>
<#list fmColumnList as fm>
    <#if fm.sysDicCode == "" && fm.dicTable == "">
    <#else >
                <template #body-cell-${fm.code}="props">
                    <q-td
                            key="${fm.code}"
                            :props="props"
                    >
                        {{getDictLabel(${fm.optionsName},props.row.${fm.code}) }}
                    </q-td>
                </template>
    </#if>
</#list>
                <template #body-cell-opt="props">
                    <q-td :props="props" :auto-width="true">
                        <q-btn flat round dense color="primary" icon="edit"
                               @click="edit(props.row)"> <q-tooltip>编辑</q-tooltip></q-btn>
                        <btn-del label="${geForm.moduleName}" @confirm="del(props.row)"/>
                    </q-td>
                </template>
            </q-table>
        </div>
<#include "include/editDialog.ftl" />
        <confirm ref="confirmDialog" :msg="confirmMsg" @confirm="deleteBatch()" />
    </q-page>
</template>

<script>
import { IndexMixin } from 'boot/mixins';
import { getDictLabel } from 'boot/dictionary';
import confirm from 'components/confirm';
<#if fmRuleList?? && (fmRuleList?size > 0) >
import { <#list fmRuleList as fmRule>${fmRule},</#list> } from 'boot/inputTest';
</#if>
<#if catalogInput??>
import catalogselect from 'components/catalogselect';
import viewcatalog from 'components/viewcatalog';
</#if>

export default {
    name: '${geForm.className}',
    mixins: [IndexMixin],
    components: {
        confirm,
<#if catalogInput??>
    viewcatalog,
    catalogselect,
</#if>
    },
    data() {
        return {
            columns: [
                {
                    name: 'index',
                    align: 'center',
                    label: '序号',
                    field: 'index',
                },
              <#list fmColumnList as fm>
                  <#if fm.disList == '1'>
                      <#if fm.code == "catalogId" && catalogInput??>
                {
                    name: '${fm.code}_dictText', align: 'left', label: '${fm.name}', field: '${fm.code}_dictText',
                },
                      <#else >
                {
                    name: '${fm.code}', align: 'left', label: '${fm.name}', field: '${fm.code}',
                },
                      </#if>
                  </#if>
              </#list>
                {
                    name: 'opt', align: 'center', label: '操作', field: 'id',
                },
            ],
    <#list fmColumnList as fm>
        <#if fm.sysDicCode != "" || fm.dicTable != "">
            ${fm.optionsContent},
        </#if>
    </#list>
            showQuery: true,
            headers: [{ name: 'Authorization', value: localStorage.Authorization }],
            uploadUrl: `${r"$"}{process.env.SERVER_URL}${r"$"}{process.env.BASE_URL}/sys/common/upload`,
            imgUrl: `${r"$"}{process.env.SERVER_URL}${r"$"}{process.env.BASE_URL}/sys/common/static`,
            url: {
            list: '/${requestMapping}/list',
                    add: '/${requestMapping}/add',
                    edit: '/${requestMapping}/edit',
                    delete: '/${requestMapping}/delete',
                    deleteBatch: '/${requestMapping}/deleteBatch',
                    exportXlsUrl: '/${requestMapping}/exportXls',
                    importExcelUrl: '/${requestMapping}/importExcel',
            },
        };
    },
    methods: {
    <#list fmRuleList as fmRule>
        ${fmRule},
    </#list>
<#if catalogInput??>
    addBefore() {
        this.form.catalogId = this.catalog;
        return true;
    },
    selectCatalog(n) {
        this.catalog = n;
        this.query();
    },
</#if>
    getDictLabel,
    initDict() {
            <#list fmColumnList as fm>
                <#if fm.sysDicCode != "" && fm.dynamicDic == "1">
      this.${r"$"}axios.get('/sys/dictItem/selectItemsByDictCode?dictCode=${fm.sysDicCode}').then((r) => {
          this.${fm.optionsName} = r;
      });
                <#elseif fm.dicTable != "" && fm.dynamicDic == "1">
      this.${r"$"}axios.get('/sys/dictItem/selectItemsByDefId?defId=${fm.optionsName}').then((r) => {
          this.${fm.optionsName} = r;
      });
                </#if>
            </#list>
    },
},
mounted() {
    this.initDict();
},
};
</script>

<style lang="stylus">

</style>
