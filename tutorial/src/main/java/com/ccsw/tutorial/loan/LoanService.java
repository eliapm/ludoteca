package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.exceptions.IllegalDateRangeException;
import com.ccsw.tutorial.loan.exceptions.IllegalReservationException;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanService {
    Loan get(Long id);

    Page<Loan> findPage(LoanSearchDto dto);

    List<Loan> findAll();

    void save(LoanDto dto) throws IllegalReservationException, IllegalDateRangeException;

    void delete(Long id) throws Exception;

}
