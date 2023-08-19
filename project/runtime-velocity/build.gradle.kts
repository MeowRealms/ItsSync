val taboolib_version: String by project

plugins {
    id("io.izzel.taboolib") version "1.56"
}

dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    // 引入 服务端核心

}


// =============================
//       下面的东西不用动
// =============================
taboolib {
    description {
        name(rootProject.name)
    }
    // 不要在这里加模块
    install("common", "platform-velocity")
    options("skip-minimize", "keep-kotlin-module", "skip-taboolib-relocate")
    classifier = null
    version = taboolib_version
}