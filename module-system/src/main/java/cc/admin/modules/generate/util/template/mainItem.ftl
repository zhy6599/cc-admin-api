<template>
    <div class="col column view_card shadow-2 q-pl-md">
        <q-table
                flat
                color="primary"
                class="cross_table"
                separator="cell"
                :columns="columns"
                :data="list"
                row-key="id"
                :loading="loading"
                :rows-per-page-options="[10, 20, 50, 100]"
                :pagination.sync="pagination"
                @request="query"
        >
            <template #top-left>
                <div class="row no-wrap">
                    <q-input
                            clearable
                            outlined
                            dense
                            placeholder="请输入关键字搜索"
                            class="on-left"
                            @input="query"
                            debounce="500"
                            v-model="key"
                    >
                        <template #append>
                            <q-btn flat round icon="search" color="primary" @click="query" :loading="loading">
                                <q-tooltip>搜索</q-tooltip>
                            </q-btn>
                        </template>
                    </q-input>
                </div>
            </template>
            <template #top-right="table">
                <q-btn-group outline>
                    <q-btn outline icon="add" color="primary" label="新建${geForm.moduleName}" @click="add" />
                    <q-btn
                            outline
                            color="primary"
                            label="切换全屏"
                            @click="table.toggleFullscreen"
                            :icon="table.inFullscreen ? 'fullscreen_exit' : 'fullscreen'"
                    />
                </q-btn-group>
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
        <q-dialog maximized flat persistent ref="dialog" position="right">
            <q-form @submit="submit" class="dialog_card column">
                <h5 class="view_title justify-between q-px-md">
                    {{editType}}${geForm.moduleName}
                    <q-btn dense outline round icon="clear" size="sm" v-close-popup />
                </h5>
                <q-scroll-area class="col">
                    <div class="row q-col-gutter-x-md dialog_form q-pa-md">
                        <#list fmColumnList as fm>
                          <#if fm.disForm == '1'>
                          <div class="col-${generate.formType}">
                              <h5><#if fm.mastInput =='1'||fm.mastInput =='2'><q-icon name="star" color="red"/></#if> ${fm.name}：</h5>
                              <#if fm.dataType =='date'>
                                  <q-field outlined dense v-model="form.${fm.code}" <#if fm.isReadonly =='1'>readonly</#if> ${fm.rule}>
                                      <template v-slot:control>
                                          {{form.${fm.code}}}
                                      </template>
                                      <template v-slot:append>
                                          <q-btn flat dense round color="primary" icon="today">
                                              <q-popup-proxy>
                                                  <q-date v-model="form.${fm.code}" mask="YYYY-MM-DD"/>
                                              </q-popup-proxy>
                                          </q-btn>
                                      </template>
                                  </q-field>
                              <#elseif fm.dataType =='datetime'>
                                  <q-field outlined dense v-model="form.${fm.code}" <#if fm.isReadonly =='1'>readonly</#if>  ${fm.rule}>
                                      <template v-slot:control>
                                          {{form.${fm.code}}}
                                      </template>
                                      <template v-slot:append>
                                          <q-btn flat dense round color="primary" icon="schedule">
                                              <q-popup-proxy>
                                                  <div class="row">
                                                      <q-date flat square v-model="form.${fm.code}" mask="YYYY-MM-DD HH:mm:ss"/>
                                                      <q-time flat square v-model="form.${fm.code}" mask="YYYY-MM-DD HH:mm:ss"/>
                                                  </div>
                                              </q-popup-proxy>
                                          </q-btn>
                                      </template>
                                  </q-field>
                              <#else>
                                  <#if fm.code == "catalogId" && catalogInput??>
                                      ${catalogInput}
                                  <#elseif fm.sysDicCode == "" && fm.dicTable == "">
                                  <q-input outlined dense v-model="form.${fm.code}" <#if fm.isReadonly =='1'>readonly</#if> type="${fm.cmpType}"   ${fm.rule}/>
                                  <#else >
                                  <q-select outlined dense emit-value v-model="form.${fm.code}" map-options :options="${fm.optionsName}" />
                                  </#if>
                              </#if>
                          </div>
                          </#if>
                        </#list>
                    </div>
                </q-scroll-area>
                <div class="row justify-end q-pa-md">
                    <q-btn outline color="primary" label="取消" v-close-popup />
                    <q-btn unelevated color="primary" class="on-right" label="提交" type="submit" />
                </div>
            </q-form>
        </q-dialog>
    </div>
</template>

<script>
import { IndexMixin } from 'boot/mixins';
import { getDictLabel } from 'boot/dictionary';
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
    },
    props: {
        selectedArray: Array,
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
        queryParam() {
            const sel = this.selectedArray[0];
            return {
                ${slaveColumnCode}: sel.${mainColumnCode},
            };
        },
        beforeQuery() {
            return this.selectedArray.length === 1;
        },
        addBefore() {
            this.form.${slaveColumnCode} = this.selectedArray[0].id;
            return true;
        },
    },
    mounted() {
        this.initDict();
    },
    watch: {
        selectedArray: {
            handler() {
                if (this.selectedArray.length === 1) {
                    this.key = '';
                    this.query();
                } else {
                    this.list = [];
                }
            },
            deep: true,
        },
    },
};
</script>

<style lang="stylus"></style>
