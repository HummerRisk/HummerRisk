<p align="center">
    <a href="https://hummerrisk.com">
        <img src="./frontend/src/assets/img/logo/logo-dark.png" alt="HummerRisk" width="300"/>
    </a>
</p>
<h3 align="center">
    开源的云原生安全合规检测平台 
    <a href="https://github.com/hummerrisk/hummerrisk/blob/master/README_EN.md" style="color: #df913c;">[英文版]</a>
</h3>

<p align="center">
    <a href="https://www.codacy.com/gh/hummerrisk/hummerrisk/dashboardutm_source=github.com&amp;utm_medium=referral&amp;utm_content=hummerrisk/hummerrisk&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/3331d2c045ae4d0ba1fd8fdd623186e7" alt="A"/></a>
    <a href="https://www.gnu.org/licenses/old-licenses/gpl-3.0"><img src="https://img.shields.io/github/license/hummerrisk/hummerrisk?color=%231890FF&style=flat-square" alt="License: GPL v3"></a>
    <a href="https://github.com/hummerrisk/hummerrisk/releases/latest"><img src="https://img.shields.io/github/v/release/hummerrisk/hummerrisk" alt=""></a>
    <a href="https://github.com/hummerrisk/hummerrisk"><img src="https://img.shields.io/github/stars/hummerrisk/hummerrisk?color=%231890FF&style=flat-square" alt=""></a>
    <a href="https://github.com/hummerrisk/hummerrisk/releases"><img src="https://img.shields.io/github/downloads/hummerrisk/hummerrisk/total" alt=""></a>
</p>
<hr/>

## 什么是HummerRisk

HummerRisk 是开源的安全合规检测平台，全面支持云原生，实现对主流公(私)有云资源的安全合规扫描、网络漏洞扫描、虚拟机安全扫描、软件依赖扫描、容器镜像扫描，支持 Cloud Custodian、Prowler、Nuclei、 Xray 和 Dependency Check 等多种扫描引擎，全面满足日常的安全合规检测需要。

**支持功能**


**技术优势**
开源开放：HummerRisk 遵循 GPL v3 开源协议
极致体验：使用 SpringBoot/Vue 进行开发，界面美观、用户体验好。
云原生支持：
灵活扩展：

## 快速开始

仅需两步快速安装 HummerRisk：

1.  准备一台不小于 4 G 内存的 64 位 Linux 主机；
2.  以 root 用户执行如下命令一键安装 HummerRisk。

```sh
curl -sSL https://github.com/hummerrisk/hummerrisk/releases/latest/download/quick_start.sh | sh
```

## 帮助文档

> [帮助文档](https://docs.hummerrisk.com/)

## 微信群

<img src="https://hummerrisk-1312321453.cos.ap-beijing.myqcloud.com/contact_me_qr.png" width="156" height="156" alt="">

## 安全说明

如果您在使用过程中发现任何安全问题，请通过以下方式直接联系我们：

- 邮箱：support@hummercloud.com

## 技术栈

- [Vue](https://vuejs.org/)：使用了 Vue.js 作为前端技术栈
- [Spring Boot](https://www.tutorialspoint.com/spring_boot/spring_boot_introduction.htm)：使用了 Spring Boot 作为后端技术栈
- [MySQL](https://www.mysql.com/)：使用了 MySQL 作为数据库引擎
- [Cloud Custodian](https://cloudcustodian.io/)：使用了 Cloud Custodian 作为云平台扫描引擎
- [Prowler](https://prowler.pro/)：使用了 Prowler 作为 AWS 扫描引擎
- [Nuclei](https://nuclei.projectdiscovery.io/)：使用了 Nuclei 作为漏洞扫描引擎
- [Xray](https://xray.cool/)：使用了 Xray 作为漏洞检测引擎
- [Dependency Check](https://jeremylong.github.io/DependencyCheck/)：使用了 Dependency Check 作为软件依赖检查引擎
- [Element](https://element.eleme.cn/#/)：使用 Element 提供的优秀组件库

## License & Copyright

Copyright (c) 2022 瀚马科技 HummerCloud, All rights reserved.

Licensed under The GNU General Public License version 3 (GPLv3) (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

https://www.gnu.org/licenses/gpl-3.0.html

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
