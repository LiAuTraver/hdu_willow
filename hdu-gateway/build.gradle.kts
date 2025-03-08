dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery:2021.0.5.0")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.hdu.hdufpga.HduGatewayApplication")
}