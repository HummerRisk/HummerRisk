<template>
  <el-dropdown size="medium" @command="handleCommand" class="align-right">
    <span class="dropdown-link">
        {{ currentUser.name }}<i class="el-icon-caret-bottom el-icon--right"/>
    </span>
    <template v-slot:dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="personal">{{ $t('commons.personal_information') }} <i class="el-icon-user-solid"/></el-dropdown-item>
        <el-dropdown-item command="about">{{ $t('commons.about_us') }} <i class="el-icon-info"/></el-dropdown-item>
        <el-dropdown-item command="ApiHelp">{{ $t('commons.api_help_documentation') }} <i class="el-icon-question"/></el-dropdown-item>
        <el-dropdown-item command="custodian">{{ 'Custodian' }} <i class="el-icon-s-help"/></el-dropdown-item>
        <el-dropdown-item command="nuclei">{{ 'Nuclei' }} <i class="el-icon-s-help"/></el-dropdown-item>
        <el-dropdown-item command="prowler">{{ 'Prowler' }} <i class="el-icon-s-help"/></el-dropdown-item>
        <el-dropdown-item command="logout">{{ $t('commons.exit_system') }} <i class="el-icon-warning"/></el-dropdown-item>
      </el-dropdown-menu>
    </template>

    <about-us ref="aboutUs"/>
  </el-dropdown>
</template>

<script>
import {getCurrentUser} from "../../../../common/js/utils";
import AboutUs from "./AboutUs";
import axios from "axios";
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
            axios.get("/signout").then(response => {
              if (response.data.success) {
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
            window.location.href = "https://docs.hummerrisk.com";
            break;
          case "ApiHelp":
            window.open('/doc.html', "_blank");
            break;
          case "custodian":
            window.open('https://docs.hummerrisk.com/question/rule', "_blank");
            break;
          case "nuclei":
            window.open('https://docs.hummerrisk.com/question/nuclei', "_blank");
            break;
          case "prowler":
            window.open('https://docs.hummerrisk.com/question/prowler', "_blank");
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


