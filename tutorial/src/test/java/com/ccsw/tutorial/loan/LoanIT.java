package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {
    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    private static final LocalDate START_DATE = LocalDate.parse("2025-10-20");
    private static final LocalDate END_DATE = LocalDate.parse("2025-10-25");

    private static final LocalDate NEW_START_DATE = LocalDate.parse("2024-07-20");
    private static final LocalDate NEW_END_DATE = LocalDate.parse("2024-07-25");

    private static final Long DELETE_LOAN_ID = 2L;

    private static final int TOTAL_LOANS = 6;
    private static final int PAGE_SIZE = 5;

    private static final String GAME_ID_PARAM = "idGame";
    private static final String CLIENT_ID_PARAM = "idClient";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    @Test
    public void findFirstPageWithFiveNoFilterSizeShouldReturn() {
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());

    }

    @Test
    public void findSecondPageWithFiveSizeShouldReturnLastResult() {
        int elements_count = TOTAL_LOANS - PAGE_SIZE;

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(1, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(elements_count, response.getBody().getContent().size());
    }

    @Test
    public void findExistsGameShouldReturnFilteredLoans() {
        int filteredLoans = 2;
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setIdGame(1L);
        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(filteredLoans, response.getBody().getTotalElements());
    }

    @Test
    public void findNotExistsTitleShouldReturnEmpty() {
        int filteredLoans = 0;
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setIdGame(0L);
        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(filteredLoans, response.getBody().getTotalElements());
    }

    @Test
    public void findExistsClientShouldReturnFilteredLoans() {
        int filteredLoans = 3;
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setIdClient(1L);
        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(filteredLoans, response.getBody().getTotalElements());
    }

    @Test
    public void findNotExistsClientShouldReturnEmpty() {
        int filteredLoans = 0;
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setIdClient(0L);
        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(filteredLoans, response.getBody().getTotalElements());
    }

    @Test
    public void findExistsDateShouldReturnFilteredLoans() {
        int filteredLoans = 2;
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setDate(LocalDate.parse("2025-07-21"));
        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(filteredLoans, response.getBody().getTotalElements());
    }

    @Test
    public void findNotExistsDateShouldReturnEmpty() {
        int filteredLoans = 0;
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));
        searchDto.setDate(LocalDate.parse("2000-07-21"));
        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(filteredLoans, response.getBody().getTotalElements());
    }

    @Test
    public void saveLoanShouldCreateLoan() {
        long newLoanSize = TOTAL_LOANS + 1;

        LoanDto dto = new LoanDto();
        GameDto gameDto = new GameDto();
        gameDto.setId(6L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(2L);

        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setStartDate(NEW_START_DATE);
        dto.setEndDate(NEW_END_DATE);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();

        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setPageNumber(0);
        pageableRequest.setPageSize((int) newLoanSize);
        searchDto.setPageable(pageableRequest);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());

        LoanDto loan = response.getBody().getContent().stream().filter(item -> item.getId().equals(newLoanSize)).findFirst().orElse(null);
        assertNotNull(loan);
        assertEquals("Azul", loan.getGame().getTitle());
    }

    @Test
    public void saveLoanWhereGameIsAlreadyReservedForThatDayShouldNotSave() {
        LoanDto dto = new LoanDto();
        GameDto gameDto = new GameDto();
        gameDto.setId(1L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(2L);

        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setStartDate(START_DATE);
        dto.setEndDate(END_DATE);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setPageNumber(0);
        pageableRequest.setPageSize(TOTAL_LOANS);
        searchDto.setPageable(pageableRequest);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);
        //        assertThrows(IllegalReservationException.class, () -> {
        //            response.getStatusCode();
        //        });

        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());

    }

    @Test
    public void saveLoanWithAClientThatHasAlreadyTwoReservationsForThatDayShouldNotSave() {
        LoanDto dto = new LoanDto();
        GameDto gameDto = new GameDto();
        gameDto.setId(2L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(3L);

        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setStartDate(START_DATE);
        dto.setEndDate(END_DATE);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setPageNumber(0);
        pageableRequest.setPageSize(10);
        searchDto.setPageable(pageableRequest);

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
    }

    @Test
    public void deleteLoanShouldDelete() {
        long newLoanSize = TOTAL_LOANS - 1;
        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_LOAN_ID, HttpMethod.DELETE, null, Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);
        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }

    @Test
    public void deleteWithNotExistsIdShouldThrowException() {
        long deleteAuthorId = TOTAL_LOANS + 1;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + deleteAuthorId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
