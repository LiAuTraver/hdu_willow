//package com.hdu.neoauth
//
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.boot.runApplication
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.web.SecurityFilterChain
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController
//
//@SpringBootApplication
//class HduOAuthApplication
//
//fun main(args: Array<String>) {
//	runApplication<HduOAuthApplication>(*args)
//}
//
//@RestController
//class HomeController {
//	@GetMapping("/")
//	fun home(): String = "Hello, World!"
//
//	@GetMapping("/secured")
//	fun secured(): String = "Hello, Secured!"
//
//	@GetMapping("/oauth2/authorization/github")
//	fun oauth2(): String = "Success!"
//}
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfig {
//	@Bean
//	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//		http
//			.authorizeHttpRequests { it.anyRequest().authenticated() }
//			.oauth2Login()
//			.and()
//			.formLogin()
//
//		return http.build()
//	}
//}
