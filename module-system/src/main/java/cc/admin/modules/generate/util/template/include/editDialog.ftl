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
