dependencies {
    implementation("org.apache.dubbo:dubbo-spring-boot-starter")
    implementation("com.alibaba.nacos:nacos-client")
    
    implementation("com.baomidou:mybatis-plus-boot-starter")
    implementation("cn.hutool:hutool-all")
    implementation("com.github.yulichang:mybatis-plus-join-boot-starter")
    
    implementation(project(":hdu-common"))
    implementation(project(":hdu-account-api"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.seata:seata-spring-boot-starter")
    implementation("com.mysql:mysql-connector-j")
    
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2021.0.5.0")
    
    implementation("cn.dev33:sa-token-spring-boot-starter")
    implementation("cn.dev33:sa-token-redis-jackson")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.hdu.hdufpga.HduAccountApplication")
}