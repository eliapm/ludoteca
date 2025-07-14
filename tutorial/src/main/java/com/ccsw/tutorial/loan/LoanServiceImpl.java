package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.exceptions.IllegalDateRangeException;
import com.ccsw.tutorial.loan.exceptions.IllegalReservationException;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    GameService gameService;

    @Autowired
    ClientService clientService;

    @Override
    public Loan get(Long id) {
        return this.loanRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {
        Pageable pageable = PageRequest.of(dto.getPageable().getPageNumber(), dto.getPageable().getPageSize());

        LoanSpecification gameSpec = new LoanSpecification(new SearchCriteria("game.id", ":", dto.getIdGame()));
        LoanSpecification clientSpec = new LoanSpecification(new SearchCriteria("client.id", ":", dto.getIdClient()));
        LoanSpecification dateSpec = new LoanSpecification(new SearchCriteria("date", "date", dto.getDate()));

        Specification<Loan> spec = Specification.where(gameSpec).and(clientSpec).and(dateSpec);

        return loanRepository.findAll(spec, pageable);
    }

    @Override
    public List<Loan> findAll() {
        return (List<Loan>) this.loanRepository.findAll();
    }

    @Override
    public void save(LoanDto dto) throws IllegalDateRangeException, IllegalReservationException {
        Loan loan = new Loan();
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalDateRangeException("La fecha de devolución no puede ser anterior a la fecha de inicio");
        } else if (ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) > 14) {
            throw new IllegalDateRangeException("El período de préstamo no puede ser mayor a catorce días");
        }

        BeanUtils.copyProperties(dto, loan, "id", "game", "client");

        LoanSearchDto gameSearchDto = new LoanSearchDto();
        gameSearchDto.setIdGame(dto.getGame().getId());
        Page<Loan> existingLoans = null;

        PageableRequest pageable = new PageableRequest();
        pageable.setPageNumber(0);
        pageable.setPageSize(10);
        gameSearchDto.setPageable(pageable);

        for (LocalDate date = dto.getStartDate(); !date.isAfter(dto.getEndDate()); date = date.plusDays(1L)) {
            gameSearchDto.setDate(date);
            existingLoans = this.findPage(gameSearchDto);

            if (!existingLoans.isEmpty()) {
                throw new IllegalReservationException("El juego ya está reservado para este día");
            }
        }

        LoanSearchDto clientSearchDto = new LoanSearchDto();
        clientSearchDto.setIdClient(dto.getClient().getId());
        clientSearchDto.setPageable(pageable);

        for (LocalDate date = dto.getStartDate(); !date.isAfter(dto.getEndDate()); date = date.plusDays(1L)) {
            clientSearchDto.setDate(date);
            existingLoans = this.findPage(clientSearchDto);
            if (existingLoans.getTotalElements() >= 2) {
                throw new IllegalReservationException("El cliente ya tiene dos juegos reservados para ese día");
            }
        }

        Client client = new Client();
        client.setId(dto.getClient().getId());
        loan.setClient(client);
        Game game = new Game();
        game.setId(dto.getGame().getId());
        loan.setGame(game);
        this.loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) throws Exception {
        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }

        this.loanRepository.deleteById(id);
    }
}
