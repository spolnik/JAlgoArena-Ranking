package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableEurekaClient
@EnableKafka
@EnableCaching(proxyTargetClass=true)
open class JAlgoArenaRankingApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaRankingApp::class.java, *args)
}