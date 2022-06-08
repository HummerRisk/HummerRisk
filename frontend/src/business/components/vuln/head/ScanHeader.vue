<template>

  <div>
    <el-row class="table-title" type="flex" justify="space-between" align="middle">
      <slot name="title">
        {{ title }}
      </slot>
    </el-row>
    <el-row type="flex" justify="space-between" align="middle">
      <span class="operate-button">
        <el-tooltip class="item" effect="dark" :content="$t('account.save_settings_tip')" placement="top-start">
          <table-button  v-if="showSave" icon="el-icon-upload"
                         :content="saveTip" @click="save"/>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" :content="$t('account.start_batch_tip')" placement="top-start">
          <table-button  v-if="showCreate" icon="el-icon-s-promotion" type="success"
                           :content="createTip" @click="create"/>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" :content="$t('account.quick_settings_tip')" placement="top-start">
          <table-button  v-if="showSetting" icon="el-icon-s-tools" type="primary"
                           :content="settingTip" @click="setting"/>
        </el-tooltip>
        <el-tooltip class="item" effect="dark" :content="$t('account.clean_settings_tip')" placement="top-start">
          <table-button  v-if="showClean" icon="el-icon-refresh" type="danger"
                         :content="cleanTip" @click="clean"/>
        </el-tooltip>

        <slot name="button"></slot>
      </span>
      <span>
        <table-button  icon="el-icon-back"
                      type="info"
                      :content="backTip" @click="back"/>
      </span>
    </el-row>
  </div>

</template>

<script>
import TableSearchBar from '../../common/components/TableSearchBar';
import TableButton from '../../common/components/TableButton';

export default {
    name: "TableHeader",
    components: {TableSearchBar, TableButton},
    props: {
      title: {
        type: String,
        default() {
          return this.$t('commons.name');
        }
      },
      showSave: {
        type: Boolean,
        default: true
      },
      showCreate: {
        type: Boolean,
        default: true
      },
      showSetting: {
        type: Boolean,
        default: false
      },
      showClean: {
        type: Boolean,
        default: false
      },
      condition: {
        type: Object
      },
      saveTip: {
        type: String,
        default() {
          return this.$t('commons.save');
        }
      },
      createTip: {
        type: String,
        default() {
          return this.$t('commons.create');
        }
      },
      settingTip: {
        type: String,
      },
      cleanTip: {
        type: String,
      },
      backTip: {
        type: String
      },
      tip: {
        String,
        default() {
          return this.$t('commons.search_by_name');
        }
      }
    },
    methods: {
      search(value) {
        this.$emit('update:condition', this.condition);
        this.$emit('search', value);
      },
      save() {
        this.$emit('save');
      },
      create() {
        this.$emit('create');
      },
      clean() {
        this.$emit('clean');
      },
      setting() {
        this.$emit('setting')
      },
      back() {
        this.$emit('back')
      }
    },
    computed: {
    }
  }
</script>

<style>

  .table-title {
    height: 40px;
    font-weight: bold;
    font-size: 18px;
  }

</style>

<style scoped>

  .operate-button {
    margin-bottom: -5px;
  }
  .item {
    margin: 4px;
  }
</style>
