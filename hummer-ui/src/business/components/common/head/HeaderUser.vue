<template>
  <el-dropdown size="medium" @command="handleCommand" class="align-right">
    <span class="dropdown-link">
        {{ currentUser.name }}<i class="el-icon-caret-bottom el-icon--right"/>
    </span>
    <template v-slot:dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="personal"><i class="el-icon-user-solid"/> {{ $t('commons.personal_information') }}</el-dropdown-item>
        <el-dropdown-item command="about"><i class="el-icon-info"/> {{ $t('commons.about_us') }}</el-dropdown-item>
        <el-dropdown-item command="apiHelp"><i class="el-icon-question"/> {{ $t('commons.api_help_documentation') }}</el-dropdown-item>
        <el-dropdown-item command="logout"><i class="el-icon-warning"/> {{ $t('commons.exit_system') }}</el-dropdown-item>
      </el-dropdown-menu>
    </template>

    <about-us ref="aboutUs"/>
  </el-dropdown>
</template>

<script>
import {getCurrentUser} from "../../../../common/js/utils";
import AboutUs from "./AboutUs";
import axios from "axios";
import {removeToken} from "@/common/js/auth";
import {signoutUrl} from "@/api/auth/auth";
/* eslint-disable */
  export default {
    name: "User",
    components: {AboutUs},
    computed: {
      currentUser: () => {
        return getCurrentUser();
      }
    },
    methods: {
      handleCommand(command) {
        switch (command) {
          case "personal":
            this.$router.push('/setting/personsetting').catch(error => error);
            break;
          case "logout":
            axios.get(signoutUrl).then(response => {
              if (response.data.success) {
                removeToken();
                localStorage.clear();
                window.location.href = "/login";
              }
            }).catch(error => {
              localStorage.clear();
              window.location.href = "/login";
            });
            break;
          case "about":
            this.$refs.aboutUs.open();
            break;
          case "help":
            window.location.href = "https://hummerrisk.com";
            break;
          case "apiHelp":
            let api = process.env.VUE_APP_BASE_API
            window.open(api+'/doc.html', "_blank");
            break;
          default:
            break;
        }
      }
    }
  }
</script>

<style scoped>
  .dropdown-link {
    cursor: pointer;
    font-size: 12px;
    color: rgb(245, 245, 245);
    line-height: 40px;
  }

  .align-right {
    float: right;
    margin: 0 20px;
  }

</style>


