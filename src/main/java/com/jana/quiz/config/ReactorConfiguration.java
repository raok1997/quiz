package com.jana.quiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import reactor.core.publisher.Hooks;

@Configuration
@Profile("!" + QuizConstants.SPRING_PROFILE_PRODUCTION)
public class ReactorConfiguration {

    public ReactorConfiguration() {
        Hooks.onOperatorDebug();
    }
}
