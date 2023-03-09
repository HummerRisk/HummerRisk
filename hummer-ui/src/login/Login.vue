<template>
  <div class="login-background">
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
      <div class="container" v-loading="result.loading" v-if="ready">
        <el-row type="flex">

          <el-col :span="24">
            <el-form :model="form" :rules="rules" ref="form">
              <div class="title">
                <img src="../assets/img/logo/logo-dark.png" style="width: 224px" alt="">
              </div>
              <div class="border"></div>
              <div class="welcome">
                {{$t('commons.welcome')}}
              </div>
              <div class="form">
                <el-form-item v-slot:default>
                  <el-radio-group v-model="form.authenticate">
                  </el-radio-group>
                </el-form-item>
                <el-form-item prop="username">
                  <el-input v-model="form.username" :placeholder="$t('commons.login_username')" autofocus
                            autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="password">
                  <el-input v-model="form.password" :placeholder="$t('commons.password')" show-password autocomplete="off"
                            maxlength="20" show-word-limit/>
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
import { saveLocalStorage } from '@/common/js/utils';
import { DEFAULT_LANGUAGE } from "@/common/js/constants";
import { signinUrl, isLoginUrl, ssoSigninUrl, languageUrl } from "@/api/auth/auth";
import { setToken } from '@/common/js/auth';

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
        if (!response.data.success) {
          this.ready = true;
        } else {
          let user = response.data.data;
          setToken(response.data.token);
          saveLocalStorage(response.data);
          this.getLanguage(user.language);
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
                this.loginUrl = ssoSigninUrl;
                this.doLogin();
            }
          } else {
            return false;
          }
        });
      },
      doLogin() {
        this.result = this.$post(this.loginUrl, this.form, response => {
          saveLocalStorage(response);
          sessionStorage.setItem('loginSuccess', 'true');
          setToken(response.data.token);
          this.getLanguage(response.data.language);
        });
      },
      getLanguage(language) {
        if (!language) {
          this.$get(languageUrl, response => {
            language = response.data;
            localStorage.setItem(DEFAULT_LANGUAGE, language);
            window.location.href = "/";
          })
        } else {
          window.location.href = "/";
        }
      },
    }
  }
</script>

<style scoped>
  .container {
    width: 30%;
    min-width: 600px;
    max-width: 740px;
    height: 560px;
    /*background-color: #fff;*/
    margin: auto;
    position: absolute;
    top: 17%;
    left: 10%;
    right: 10%;
    box-shadow: #dddddd 0 0 10px;
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
    border-radius: 0;
    border-color: #935e3a;
    background-color: #935e3a;
  }

  .btn > .submit:hover {
    border-color: #df913c;
    background-color: #df913c;
  }

  .btn > .submit:active {
    border-color: #df913c;
    background-color: #df913c;
  }

  .msg {
    margin-top: 10px;
    padding: 0 40px;
    color: red;
    text-align: center;
  }

</style>

<style>
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

  #particles-js {
    width: 100%;
    /*height: calc(100% - 100px);*/
    position: absolute;
  }

  .login-background {
    /*background: linear-gradient(-180deg, #df913c 0%, #ffffff 100%);*/
    background-image: url(../assets/background.png);
    background-size: contain;
    background-color: #334071;
    width: 100%;
    height: 100%; /**宽高100%是为了图片铺满屏幕 */
    min-height: 760px;
    z-index: -1;
    position: absolute;
    display: flex;
    justify-content: center;
    align-items: center;
  }

</style>

