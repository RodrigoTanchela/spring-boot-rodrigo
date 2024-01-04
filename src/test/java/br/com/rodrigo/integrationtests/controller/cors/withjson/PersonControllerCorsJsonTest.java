package br.com.rodrigo.integrationtests.controller.cors.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rodrigo.configs.TestConfigs;
import br.com.rodrigo.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.rodrigo.integrationtests.vo.AccountCredentialsVO;
import br.com.rodrigo.integrationtests.vo.PersonVO;
import br.com.rodrigo.integrationtests.vo.TokenVO;
import br.com.rodrigo.integrationtests.vo.wrappers.WrapperPersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerCorsJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;

	private static PersonVO person;

	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = 
				given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENTE_TYPE_JSON)	
				.body(user)
					.when()
					.post()
				.then()
				.statusCode(200)
				.extract() 
				.body()
					.as(TokenVO.class)
				.getAccessToken();
		
		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();

//		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_RODRIGO)
//				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
//				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
//				.build();

		var content = 
				given()
				.spec(specification)
				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
				.body(person)
					.when()
					.post()
				.then().statusCode(200)
				.extract()
				.body()
				.asString();

		PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
		person = createdPerson;
		
		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());

		assertTrue(createdPerson.getId() > 0);

		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Stallman", createdPerson.getLastName());
		assertEquals("New York City, New York, US", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
	}
	
	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Solo");

//		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_RODRIGO)
//				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
//				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
//				.build();

		var content = 
				given()
				.spec(specification)
				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
				.body(person)
					.when()
					.post()
				.then().statusCode(200).extract().body().asString();

		PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
		person = createdPerson;
		
		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());

		assertEquals(person.getId(), createdPerson.getId());

		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Piquet Solo", createdPerson.getLastName());
		assertEquals("New York City, New York, US", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
	}


//	@Test
//	@Order(2)
//	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
//		mockPerson();
////
////		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
////				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
////				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
////				.build();
//
//		var content = 
//				given()
//				.spec(specification)
//				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
//				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
//				.body(person).when().post()
//				.then().statusCode(403).extract().body().asString();
//
//		assertNotNull(content);
//		assertEquals("Invalid CORS request", content);
//
//	}

	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();

//		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_RODRIGO)
//				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
//				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
//				.build();

		var content = given()
				.spec(specification)
				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_RODRIGO)
					.pathParam("id", person.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();

		PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);

		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());

		assertEquals(person.getId(), createdPerson.getId());

		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Piquet Solo", createdPerson.getLastName());
		assertEquals("New York City, New York, US", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
	}
	
//	@Test
//	@Order(4)
//	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
//		mockPerson();
//
////		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
////				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
////				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
////				.build();
//
//		var content = 
//				given()
//				.spec(specification)
//				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
//				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
//				.pathParam("id", person.getId()).when().get("{id}").then().statusCode(403).extract().body().asString();
//
//
//		assertNotNull(content);
//		assertEquals("Invalid CORS request", content);
//	}
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		        given()
				.spec(specification)
				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
					.pathParam("id", person.getId())
					.when()
					.delete("{id}")
				.then()
					.statusCode(204);

//		PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
//		person = createdPerson;
//		
//		assertNotNull(createdPerson);
//		assertNotNull(createdPerson.getId());
//		assertNotNull(createdPerson.getFirstName());
//		assertNotNull(createdPerson.getLastName());
//		assertNotNull(createdPerson.getAddress());
//		assertNotNull(createdPerson.getGender());
//
//		assertEquals(person.getId(), createdPerson.getId());
//
//		assertEquals("Richard", createdPerson.getFirstName());
//		assertEquals("Piquet Solo", createdPerson.getLastName());
//		assertEquals("New York City, New York, US", createdPerson.getAddress());
//		assertEquals("Male", createdPerson.getGender());
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		mockPerson();

//		specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_RODRIGO)
//				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
//				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
//				.build();

		var content = 
				given()
				.spec(specification)
				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
					.when()
					.get()
				.then()
				.statusCode(200)
					.extract()
					.body()
					.asString();
				//.as(new TypeRef<List<PersonVO>>() {});
		
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
		
		PersonVO foundPersonOne = people.get(0);
		person = foundPersonOne;
		
		assertNotNull(foundPersonOne);
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());

		assertTrue(foundPersonOne.getId() > 0);

		assertEquals("Ayrton", foundPersonOne.getFirstName());
		assertEquals("Senna", foundPersonOne.getLastName());
		assertEquals("São Paulo", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
		
		PersonVO foundPersonFour = people.get(2);
		person = foundPersonFour;
		
		assertNotNull(foundPersonFour);
		assertNotNull(foundPersonFour.getId());
		assertNotNull(foundPersonFour.getFirstName());
		assertNotNull(foundPersonFour.getLastName());
		assertNotNull(foundPersonFour.getAddress());
		assertNotNull(foundPersonFour.getGender());

		assertTrue(foundPersonFour.getId() > 0);

		assertEquals("Indira", foundPersonFour.getFirstName());
		assertEquals("Gandhi", foundPersonFour.getLastName());
		assertEquals("Porbandar - India", foundPersonFour.getAddress());
		assertEquals("Female", foundPersonFour.getGender());
	}
	
	@Test
	@Order(5)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

		RequestSpecification specificationWithoutToken = specification = new RequestSpecBuilder()
				.setBasePath("/api/person/v1").setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL)).addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		
				given()
				.spec(specificationWithoutToken)
				.contentType(TestConfigs.CONTENTE_TYPE_JSON)
					.when()
					.get()
				.then()
				.statusCode(403);
	}
	
	private void mockPerson() {
		person.setFirstName("Richard");
		person.setLastName("Stallman");
		person.setAddress("New York City, New York, US");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
