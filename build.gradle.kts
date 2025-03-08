import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	id("java")
	id("org.springframework.boot") version "2.6.13"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
}
extra["springBootVersion"] = "2.6.13"
extra["saTokenVersion"] = "1.37.0"
allprojects {
	group = "com.hdu"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
	apply {
		plugin("java")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
	}

	java {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	tasks.withType<Test> {
		useJUnitPlatform()
		testLogging {
			events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
		}
	}

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")
	}

	dependencyManagement {
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}")
			mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
		}

		dependencies {
			dependency("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0")
			dependency("com.mysql:mysql-connector-j:8.0.32")
			dependency("org.apache.dubbo:dubbo-spring-boot-starter:3.0.9")
			dependency("com.alibaba.nacos:nacos-client:2.0.4")
			dependency("cn.hutool:hutool-all:5.8.26")
			dependency("com.baomidou:mybatis-plus-boot-starter:3.5.3.1")
			dependency("com.github.yulichang:mybatis-plus-join-boot-starter:1.4.10")
			dependency("com.alibaba:easyexcel:3.3.3")
			dependency("com.alibaba:fastjson:2.0.9")
			dependency("io.seata:seata-spring-boot-starter:1.6.1")
			dependency("org.apache.rocketmq:rocketmq-spring-boot-starter:2.2.2")
			dependency("org.springframework.boot:spring-boot-starter-data-redis:2.5.3")
			dependency("io.netty:netty-all:4.1.69.Final")
			dependency("org.redisson:redisson-spring-boot-starter:3.16.1")
			dependency("com.fasterxml.jackson.core:jackson-annotations:2.18.3")
			dependency("cn.dev33:sa-token-spring-boot-starter:${property("saTokenVersion")}")
			dependency("cn.dev33:sa-token-redis-jackson:${property("saTokenVersion")}")
		}
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}
}
