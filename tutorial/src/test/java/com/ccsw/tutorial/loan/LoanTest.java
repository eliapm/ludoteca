package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private static final int TOTAL_LOANS = 6;

    @Test
    public void findAllShouldReturnAllLoans() {
        List<Loan> list = new ArrayList<>();
        list.add(mock(Loan.class));

        when(loanRepository.findAll()).thenReturn(list);

        List<Loan> loans = loanService.findAll();

        assertNotNull(loans);
        assertEquals(1, loans.size());
    }

    public static final Long LOAN_ID = 1L;
    public static final Long LOAN_ID_NOT_EXISTS = 0L;

    @Test
    public void getExistsLoanIdShouldReturnLoan() {
        Loan loan = mock(Loan.class);

        when(loan.getId()).thenReturn(LOAN_ID);
        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        Loan loanResponse = loanService.get(LOAN_ID);

        assertNotNull(loanResponse);
        assertEquals(LOAN_ID, loanResponse.getId());
    }

    @Test
    public void getNotExistsLoanIdShouldReturnNull() {
        when(loanRepository.findById(LOAN_ID_NOT_EXISTS)).thenReturn(Optional.empty());
        Loan loan = loanService.get(LOAN_ID_NOT_EXISTS);

        assertNull(loan);
    }

    public static final LocalDate START_DATE = LocalDate.parse("2025-07-07");
    public static final LocalDate BEFORE_END_DATE = LocalDate.parse("2025-07-03");
    public static final LocalDate EXCEEDED_END_DATE = LocalDate.parse("2025-07-27");

    @Test
    public void saveLoanEndDateDayBeforeStartDateShouldReturnException() {
        LoanDto loan = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        loan.setGame(game);
        loan.setClient(client);
        loan.setStartDate(START_DATE);
        loan.setEndDate(BEFORE_END_DATE);

        Exception exception = assertThrows(Exception.class, () -> {
            loanService.save(loan);
        });

        assertEquals("La fecha de devolución no puede ser anterior a la fecha de inicio", exception.getMessage());
    }

    @Test
    public void saveLoanDatePeriodIsBiggerThanFourteenDaysShouldReturnException() {
        LoanDto loan = new LoanDto();

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        loan.setGame(game);
        loan.setClient(client);
        loan.setStartDate(START_DATE);
        loan.setEndDate(EXCEEDED_END_DATE);

        Exception exception = assertThrows(Exception.class, () -> {
            loanService.save(loan);
        });

        assertEquals("El período de préstamo no puede ser mayor a catorce días", exception.getMessage());
    }

    @Test
    public void deleteExistLoanIdShouldDeleteLoan() throws Exception {
        Loan loan = mock(Loan.class);

        when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(loan));

        loanService.delete(LOAN_ID);

        verify(loanRepository).deleteById(LOAN_ID);

    }

}
