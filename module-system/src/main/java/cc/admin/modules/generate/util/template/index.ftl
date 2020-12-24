<template>
    <q-page class="${pageClass}">
        ${viewCatalog!''}
        <div class="col column view_card shadow-2 q-pa-md q-ma-sm">
        <#if generate.isSimpleQuery == "0">
            <div class="row items-center justify-start q-mb-md">
                    <#list fmColumnList as fm>
                        <#if fm.isQuery =='1'>
                            <div class="row items-center q-mb-md col-3">
                                <span class="q-ml-md">${fm.name}：</span>
                            <#if fm.dataType =='date'>
                                <q-field outlined dense label="${fm.name}" v-model="searchForm.${fm.code}" class="col">
                                    <template v-slot:control>
                                        {{searchForm.${fm.code}}}
                                    </template>
                                    <template v-slot:append>
                                        <q-btn flat dense round color="primary" icon="today">
                                            <q-popup-proxy>
                                                <q-date v-model="searchForm.${fm.code}" mask="YYYY-MM-DD"/>
                                            </q-popup-proxy>
                                        </q-btn>
                                    </template>
                                </q-field>
                            <#elseif fm.dataType =='datetime'>
                                      <q-field outlined dense v-model="searchForm.${fm.code}" class="col">
                                          <template v-slot:control>
                                              {{searchForm.${fm.code}}}
                                          </template>
                                          <template v-slot:append>
                                              <q-btn flat dense round color="primary" icon="schedule">
                                                  <q-popup-proxy>
                                                      <div class="row">
                                                          <q-date flat square v-model="searchForm.${fm.code}" mask="YYYY-MM-DD HH:mm:ss"/>
                                                          <q-time flat square v-model="searchForm.${fm.code}" mask="YYYY-MM-DD HH:mm:ss"/>
                                                      </div>
                                                  </q-popup-proxy>
                                              </q-btn>
                                          </template>
                                      </q-field>
                            <#else>
                                <#if fm.sysDicCode == "" && fm.dicTable == "">
                                      <q-input outlined dense v-model="searchForm.${fm.code}" type="${fm.cmpType}"  class="col" />
                                <#else >
                                      <q-select outlined dense emit-value v-model="searchForm.${fm.code}" map-options :options="${fm.optionsName}"  class="col"/>
                                </#if>
                            </#if>
                            </div>
                        </#if>
                    </#list>
                <div class="row items-center q-mb-md col-3 q-ml-md">
                    <q-btn
                            color="primary"
                            label="搜索"
                            icon="search"
                            class="on-left"
                            @click="query()"
                            :loading="loading"
                            unelevated
                    />
                    <q-btn label="重置" icon="search_off" color="primary" outline @click="searchReset" />
                </div>
            </div>
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
            <#if generate.isSimpleQuery == "1">
                <template #top-left>
                    <div class="row no-wrap">
                        <div class="row items-center">
                            <q-input

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
                    </div>
                </template>
            </#if>
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
                        <q-btn-dropdown outline color="primary" label="自选列" icon="view_list">
                            <q-list>
                                <q-item tag="label" v-for="item in columns" :key="item.name">
                                    <q-item-section avatar>
                                        <q-checkbox v-model="group" :val="item.name" />
                                    </q-item-section>
                                    <q-item-section>
                                        <q-item-label>{{item.label}}</q-item-label>
                                    </q-item-section>
                                </q-item>
                            </q-list>
                        </q-btn-dropdown>
                        <q-btn
                                :disable="selected.length === 0"
                                outline
                                color="primary"
                                label="批量删除"
                                @click="showConfirm()"
                                icon="mdi-delete-variant"
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
        </div>
        <q-dialog maximized flat persistent ref="dialog" position="right">
            <q-form @submit="submit" class="dialog_card column">
                <h5 class="view_title justify-between q-px-md">
                    {{editType}}${geForm.moduleName}
                    <q-btn dense outline round icon="clear" size="sm" v-close-popup/>
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
                    <q-btn outline color="primary" label="取消" v-close-popup/>
                    <q-btn unelevated color="primary" class="on-right" label="提交" type="submit"/>
                </div>
            </q-form>
        </q-dialog>
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
