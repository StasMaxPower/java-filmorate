package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class UserControllerTests {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockMvc mockMvc;

	User user = new User(1,"123@mail.ru", "Vasya", "Vasiliy", LocalDate.of(1997,01,01) );
	User userWithoutName = new User(2,"123@mail.ru", "Vasya", "", LocalDate.of(1997,01,01) );
	User userWithoutLogin = new User(3,"123@mail.ru", "", "Vasiliy", LocalDate.of(1997,01,01) );
	User userWithInvalidBirthday = new User(4,"123@mail.ru", "", "Vasiliy", LocalDate.of(2022,06,01) );
	User userWithInvalidEmail = new User(5,"123mail.ru", "", "Vasiliy", LocalDate.of(2022,06,01) );


	@Test
	void createValidUserResponseShouldBeOk() throws Exception {
		this.mockMvc.perform(post("/users")
						.content(mapper.writeValueAsString(user))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk());
	}

	@Test
	void createValidUserWithoutNameShouldBeNameAsLogin() throws Exception {
		this.mockMvc.perform(post("/users")
						.content(mapper.writeValueAsString(userWithoutName))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
		 				.andExpect(jsonPath("$.name").value("Vasya"));
	}

	@Test
	void createUserWithoutLogin() throws Exception {
		this.mockMvc.perform(post("/users")
						.content(mapper.writeValueAsString(userWithoutLogin))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest());
	}

	@Test
	void createUserWithInvalidBirthday() throws Exception {
		this.mockMvc.perform(post("/users")
						.content(mapper.writeValueAsString(userWithInvalidBirthday))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest());
	}

	@Test
	void createUserWithInvalidEmail() throws Exception {
		this.mockMvc.perform(post("/users")
						.content(mapper.writeValueAsString(userWithInvalidEmail))
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isBadRequest());
	}
}
