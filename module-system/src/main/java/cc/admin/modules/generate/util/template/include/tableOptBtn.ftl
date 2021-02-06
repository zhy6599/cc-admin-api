                    <q-btn-group outline>
                        <q-btn outline icon="add" no-wrap color="primary" label="新建" @click="add" />
                        <q-btn
                                outline
                                color="primary"
                                label="切换全屏" no-wrap v-if="$q.screen.gt.md"
                                @click="table.toggleFullscreen"
                                :icon="table.inFullscreen ? 'fullscreen_exit' : 'fullscreen'"
                        />
                        <q-btn-dropdown outline color="primary" label="自选列" icon="view_list" no-wrap v-if="$q.screen.gt.md">
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
                        <q-btn outline no-wrap v-if="$q.screen.gt.sm"
                               label="导入" icon="mdi-cloud-upload-outline"
                               :loading="importing"
                               color="primary" @click="importExcel">
                            <q-uploader auto-upload ref="excelUploader" :max-files="1" class="hidden"
                                        :url="importExcelUrlFull" field-name="file"
                                        :headers="[{name: 'authorization', value: $store.state.User.authorization}]"
                                        @uploaded="importedExcel"/>
                        </q-btn>
                        <q-btn outline no-wrap v-if="$q.screen.gt.sm"
                               :loading="exporting"
                               label="导出" icon="mdi-cloud-download-outline" color="primary"
                               @click="exportExcel('${geForm.moduleName}')"/>
                        <q-btn
                                :disable="selected.length === 0"
                                outline no-wrap v-if="$q.screen.gt.md"
                                color="primary"
                                label="批量删除"
                                @click="showConfirm()"
                                icon="mdi-delete-variant"
                        />
                    </q-btn-group>
