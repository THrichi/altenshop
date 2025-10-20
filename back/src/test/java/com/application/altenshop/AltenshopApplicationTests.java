package com.application.altenshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class AltenshopApplicationTest {

	@Test
	void contextLoads() {
		// Vérifie que le contexte Spring se charge sans erreur
	}

	@Test
	void mainMethodRunsWithoutExceptions() {
		// Vérifie que la méthode main s'exécute sans lever d'exception
		assertDoesNotThrow(() -> AltenshopApplication.main(new String[]{}));
	}
}
