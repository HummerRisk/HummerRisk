<template>
  <div class="login-background" v-loading="result.loading">
    <vue-particles
      id="particles-js"
      class=""
      color="#409EFF"
      :particleOpacity="0.7"
      :particlesNumber="60"
      shapeType="circle"
      :particleSize="6"
      linesColor="#409EFF"
      :linesWidth="1"
      :lineLinked="true"
      :lineOpacity="0.4"
      :linesDistance="150"
      :moveSpeed="3"
      :hoverEffect="true"
      hoverMode="grab"
      :clickEffect="true"
      clickMode="push">
    </vue-particles>
    <div style="width: 100%;height: 100%;">
      <div class="container" v-if="ready">
        <el-row type="flex">

          <el-col :span="15" class="image">
          </el-col>

          <el-col :span="9">
            <el-form :model="form" :rules="rules" ref="form">
              <div class="title">
                <img src="../assets/img/logo/logo-dark.png" style="width: 300px" alt="">
              </div>
              <div class="border"></div>
              <div class="welcome">
                {{$t('commons.welcome')}}
              </div>
              <div class="form">
                <el-form-item prop="username">
                  <el-input v-model="form.username" :placeholder="$t('commons.login_username')" autofocus
                            autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="password">
                  <el-input v-model="form.password" :placeholder="$t('commons.password')" show-password autocomplete="off"
                            maxlength="150" show-word-limit/>
                </el-form-item>
              </div>
              <div class="btn">
                <el-button type="primary" class="submit" @click="submit('form')">
                  {{$t('commons.login')}}
                </el-button>
              </div>
              <div class="msg">
                {{msg}}
              </div>
            </el-form>
          </el-col>

        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import {saveLocalStorage} from '@/common/js/utils';
import {DEFAULT_LANGUAGE} from "@/common/js/constants";
import {isLoginUrl, languageUrl, signinUrl} from "@/api/auth/auth";
import {setToken} from '@/common/js/auth';

/* eslint-disable */
  export default {
    name: "Login",
    data() {
      return {
        result: {},
        form: {
          username: '',
          password: '',
          authenticate: 'LOCAL'
        },
        rules: {
          username: [
            {required: true, message: this.$t('commons.input_login_username'), trigger: 'blur'},
          ],
          password: [
            {required: true, message: this.$t('commons.input_password'), trigger: 'blur'},
            {min: 1, max: 150, message: this.$t('commons.input_limit', [1, 150]), trigger: 'blur'}
          ]
        },
        msg: '',
        ready: false,
        loginUrl: signinUrl,
      }
    },
    beforeCreate() {
      this.$get(isLoginUrl).then(response => {
        if (response.data.success) {
          let user = response.data.data;
          setToken(response.data.token);
          let responseUser = {
            data : response.data.data
          };
          saveLocalStorage(responseUser);
          this.getLanguage(user.language);
        } else {
          this.ready = true;
        }
      });
    },
    created: function () {
      // 主页添加键盘事件,注意,不能直接在焦点事件上添加回车
      document.addEventListener("keydown", this.watchEnter);
    },
    destroyed() {
      //移除监听回车按键
      document.removeEventListener("keydown", this.watchEnter);
    },
    methods: {
      //监听回车按钮事件
      watchEnter(e) {
        let keyNum = e.which; //获取被按下的键值
        //判断如果用户按下了回车键（keycody=13）
        if (keyNum === 13) {
          // 按下回车按钮要做的事
          this.submit('form');
        }
      },
      submit(form) {
        this.$refs[form].validate((valid) => {
          if (valid) {
            switch (this.form.authenticate) {
              case "LOCAL":
                this.loginUrl = signinUrl;
                this.doLogin();
                break;
              default:
                this.loginUrl = signinUrl;
                this.doLogin();
            }
          } else {
            return false;
          }
        });
      },
      doLogin() {
        this.result = this.$post(this.loginUrl, this.form, response => {
          let responseUser = {
            data : response.data.user
          };
          saveLocalStorage(responseUser);
          sessionStorage.setItem('loginSuccess', 'true');
          setToken(response.data.token);
          this.getLanguage(response.data.language, response.data.token);
        });
      },
      getLanguage(language, token) {
        if (!language) {
          this.$get(languageUrl, response => {
            language = response.data;
            localStorage.setItem(DEFAULT_LANGUAGE, language);
            if(!!token) window.location.href = "/";
          });
        } else {
          if(!!token) window.location.href = "/";
        }
      },
    }
  }
</script>

<style scoped>
.container {
  height: calc(100% - 280px);
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  padding: 0 8%;
}

.container >>> .el-row {
  height: 100%;
}

.title {
  margin-top: 50px;
  font-size: 32px;
  letter-spacing: 0;
  text-align: center;
}

.border {
  height: 2px;
  margin: 20px auto 20px;
  position: relative;
  width: 80px;
  background: #df913c;
}

.welcome {
  margin-top: 50px;
  font-size: 14px;
  color: #999999;
  letter-spacing: 0;
  line-height: 18px;
  text-align: center;
}

.form {
  margin-top: 30px;
  padding: 0 40px;
}

.btn {
  margin-top: 40px;
  padding: 0 40px;
}

.btn > .submit {
  width: 100%;
  border-radius: 5px;
  border-color: #df913c;
  background-color: #df913c;
}

.btn > .submit:hover {
  border-color: #FFA500;
  background-color: #FFA500;
}

.btn > .submit:active {
  border-color: #FFA500;
  background-color: #FFA500;
}

.msg {
  margin-top: 10px;
  padding: 0 40px;
  color: red;
  text-align: center;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, "Neue Haas Grotesk Text Pro", "Arial Nova", "Segoe UI", "Helvetica Neue", ".PingFang SC", "PingFang SC", "Source Han Sans SC", "Noto Sans CJK SC", "Source Han Sans CN", "Noto Sans SC", "Source Han Sans TC", "Noto Sans CJK TC", "Hiragino Sans GB", sans-serif;
  font-size: 14px;
  background-color: #f5f5f5;
  line-height: 26px;
  color: #2B415C;
  -webkit-font-smoothing: antialiased;
  margin: 0;
}

.form .el-input > .el-input__inner {
  border-radius: 0;
}

.image {
  background-image: url(../assets/img/login/login-left.png);
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

#particles-js {
  width: 100%;
  position: absolute;
}

.login-background {
  /*background: linear-gradient(-180deg, #df913c 0%, #ffffff 100%);*/
  background-image: url(../assets/background.png);
  background-size: contain;
  background-color: #142e48;
  width: 100%;
  height: 100%; /**宽高100%是为了图片铺满屏幕 */
  min-height: 760px;
  z-index: -1;
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  background-repeat: no-repeat;
}

</style>

