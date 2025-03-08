dependencies {
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("com.baomidou:dynamic-datasource-spring-boot-starter:3.5.0")
    implementation("com.baomidou:mybatis-plus-boot-starter")
    implementation("com.github.yulichang:mybatis-plus-join-boot-starter")
    implementation("cn.dev33:sa-token-redis-jackson")
    implementation("cn.dev33:sa-token-spring-boot-starter")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.hdu.hdufpga.HduDataTransformApplication")
}