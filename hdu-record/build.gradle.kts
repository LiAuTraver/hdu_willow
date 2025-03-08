dependencies {
    implementation(project(":hdu-common"))
    implementation(project(":hdu-interrupt-api"))
    implementation(project(":hdu-record-api"))
    
    implementation("com.baomidou:mybatis-plus-boot-starter")
    implementation("cn.hutool:hutool-all")
    implementation("com.alibaba:easyexcel")
    implementation("com.alibaba:fastjson")
    implementation("io.seata:seata-spring-boot-starter")
    
    implementation("org.apache.dubbo:dubbo-spring-boot-starter")
    implementation("com.alibaba.nacos:nacos-client")
    implementation("org.apache.rocketmq:rocketmq-spring-boot-starter")
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2021.0.5.0")
    implementation("com.mysql:mysql-connector-j")
    
    implementation("cn.dev33:sa-token-redis-jackson")
    implementation("cn.dev33:sa-token-spring-boot-starter")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.hdu.hdufpga.HduRecordApplication")
}