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
