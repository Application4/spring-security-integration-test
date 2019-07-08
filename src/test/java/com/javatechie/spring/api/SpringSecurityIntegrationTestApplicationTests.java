package com.javatechie.spring.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.spring.api.entity.Person;

import antlr.collections.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringSecurityIntegrationTestApplicationTests {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper mapper;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@WithMockUser("/user")
	@Test
	public void savePerson() throws Exception {
		MvcResult result = mvc
				.perform(post("/savePerson").content(mapper.writeValueAsString(new Person(47, "santosh", "CIVIL")))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertEquals(200, result.getResponse().getStatus());
	}

	@WithMockUser("/user")
	@Test
	public void getPersons() throws Exception {
		MvcResult result = mvc.perform(get("/getAllPersons").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertEquals(200, result.getResponse().getStatus());
	}

	@Autowired
	private TestRestTemplate template;

	@Test
	public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
		ResponseEntity<?> result = template.withBasicAuth("javatechie", "pwd").getForEntity("/getAllPersons",
				ArrayList.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

}
