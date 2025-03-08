dependencies {
    implementation("com.github.yulichang:mybatis-plus-join-boot-starter")
    implementation("com.baomidou:mybatis-plus-boot-starter")
    implementation(project(":hdu-common"))
    implementation("com.mysql:mysql-connector-j")
    implementation("io.netty:netty-all")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("cn.dev33:sa-token-redis-jackson")
    implementation("cn.dev33:sa-token-spring-boot-starter")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2021.0.5.0")
    implementation(project(":hdu-account-api"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.apache.dubbo:dubbo-spring-boot-starter")
    implementation("com.alibaba.nacos:nacos-client")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.hdu.hdufpga.FPGAApplication")
}