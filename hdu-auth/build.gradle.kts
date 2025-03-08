dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("cn.dev33:sa-token-spring-boot-starter")
    implementation(project(":hdu-common"))
    implementation(project(":hdu-account-api"))
    implementation("org.apache.dubbo:dubbo-spring-boot-starter")
    implementation("com.alibaba.nacos:nacos-client")
    implementation("cn.dev33:sa-token-redis-jackson")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.hdu.hdufpga.HduAuthApplication")
}