package com.sk.springrestdocs.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.springrestdocs.model.Customer;
import com.sk.springrestdocs.model.CustomerRequest;
import com.sk.springrestdocs.repository.CustomerRepository;
import com.sk.springrestdocs.service.CustomerService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@Testcontainers
public class CustomerIntegrationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0").withExposedPorts(27017);

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {

        mongoDBContainer.start();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        customerRepository.deleteAll();
    }

    @Test
    public void testGetCustomers() throws Exception {
        customerService.addCustomer(new CustomerRequest("John", "Doe", "john.doe@gmail.com", "1234567890"));
        customerService.addCustomer(new CustomerRequest("Jane", "Doe", "jane.doe@gmail.com", "1234567891"));
        mockMvc.perform(get("/customers").header("X-AUTH-TOKEN", "1234"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("jane.doe@gmail.com"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"))
                .andExpect(jsonPath("$[1].phone").value("1234567891"))
                .andDo(document("getCustomers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                .description("The customers resource")
                                        .responseFields(getResponseFields())
                                        .responseSchema(Schema.schema("Customers.Customer"))
                                        .requestHeaders(headerWithName("X-AUTH-TOKEN").description("The authentication token"))
                                .summary("The customers resource")

                                .build()),
                        responseFields(getResponseFields()),
                        requestHeaders(headerWithName("X-AUTH-TOKEN").description("The authentication token"))
                ));
    }


    @Test
    public void testDeleteCustomers() throws Exception {
        customerService.addCustomer(new CustomerRequest("John", "Doe", "john.doe@gmail.com", "1234567890"));
        Optional<Customer> customer = customerRepository.findAll().stream().findFirst();
        String customerId = customer.get().customerId();
        mockMvc.perform(delete("/customers/{customerId}", customerId).header("X-AUTH-TOKEN", "1234"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent())
                .andDo(document("deleteCustomers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("The customers delete resource")
                                        .requestHeaders(headerWithName("X-AUTH-TOKEN").description("The authentication token"))
                                        .summary("The customers delete resource")
                                        .pathParameters(parameterWithName("customerId").description("The id of the customer"))
                                        .build()),
                       pathParameters(parameterWithName("customerId").description("The id of the customer")),
                        requestHeaders(headerWithName("X-AUTH-TOKEN").description("The authentication token"))
                ));
    }

    @Test
    public void testPostCustomers() throws Exception {

        val customerRequest = new CustomerRequest("John", "Doe", "john.doe@gmail.com", "1234567890");
        val objectMapper = new ObjectMapper();
        val customerRequestJson = objectMapper.writeValueAsString(customerRequest);
        mockMvc.perform(post("/customers").header("X-AUTH-TOKEN", "1234").content(customerRequestJson).contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@gmail.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andDo(document("postCustomers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("The customer post resource")
                                        .responseFields(getRequestFieldsForPost())
                                        .responseFields(getResponseFieldsForPost())
                                        .responseSchema(Schema.schema("Customer"))
                                        .requestSchema(Schema.schema("CustomerRequest"))
                                        .requestHeaders(headerWithName("X-AUTH-TOKEN").description("The authentication token"))
                                        .summary("The customers post resource")
                                        .build()),
                        responseFields(getResponseFieldsForPost()),
                        requestFields(getRequestFieldsForPost()),
                        requestHeaders(headerWithName("X-AUTH-TOKEN").description("The authentication token"))
                ));
    }

    private FieldDescriptor[] getResponseFieldsForPost() {
        return new FieldDescriptor[] {
                fieldWithPath("firstName").description("The first name of the customer"),
                fieldWithPath("lastName").description("The last name of the customer"),
                fieldWithPath("email").description("The email of the customer"),
                fieldWithPath("phone").description("The phone of the customer"),
                fieldWithPath("customerId").description("The id of the customer")
        };
    }

    private FieldDescriptor[] getRequestFieldsForPost() {
        return new FieldDescriptor[] {
                fieldWithPath("firstName").description("The first name of the customer"),
                fieldWithPath("lastName").description("The last name of the customer"),
                fieldWithPath("email").description("The email of the customer"),
                fieldWithPath("phone").description("The phone of the customer")
        };
    }



    private FieldDescriptor[] getResponseFields() {
        return new FieldDescriptor[] {
                fieldWithPath("[].firstName").description("The first name of the customer"),
                fieldWithPath("[].lastName").description("The last name of the customer"),
                fieldWithPath("[].email").description("The email of the customer"),
                fieldWithPath("[].phone").description("The phone of the customer"),
                fieldWithPath("[].customerId").description("The id of the customer")
        };
    }

}


