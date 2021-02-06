            <div class="row items-center justify-start q-mb-md">
                    <#list fmColumnList as fm>
                        <#if fm.isQuery =='1'>
                <q-item class="col-xl-2 col-md-3 col-sm-6 col-xs-12">
                    <q-item-section class="col-3 text-right gt-sm">
                        <q-item-label>${fm.name}：</q-item-label>
                    </q-item-section>
                    <q-item-section class="col">
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
                    </q-item-section>
                </q-item>
                        </#if>
                    </#list>
                <q-item class="col-xl-2 col-md-3 col-sm-6 col-xs-12 q-pr-sm">
                    <q-item-label class="col-12 text-right row no-wrap justify-center">
                        <q-btn
                                unelevated
                                no-wrap
                                label="查询"
                                color="primary"
                                class="q-mr-sm no-border-radius"
                                :loading="loading"
                                @click="query()"
                        >
                            <template v-slot:loading>
                                <q-spinner-ios class="on-center" />
                            </template>
                        </q-btn>
                        <q-btn
                                outline
                                no-wrap
                                unelevated
                                label="重置"
                                class="q-mr-sm no-border-radius"
                                color="secondary"
                                @click="searchReset"
                        />
                        <q-btn-dropdown
                                v-model="showQuery"
                                persistent
                                dense
                                flat
                                color="primary"
                                :label="tableLabel"
                                @before-show="show"
                                @before-hide="hide"
                        ></q-btn-dropdown>
                    </q-item-label>
                </q-item>
            </div>
