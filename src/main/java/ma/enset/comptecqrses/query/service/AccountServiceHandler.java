package ma.enset.comptecqrses.query.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.comptecqrses.commonapi.enums.OperationType;
import ma.enset.comptecqrses.commonapi.events.AccountActivatedEvent;
import ma.enset.comptecqrses.commonapi.events.AccountCreatedEvent;
import ma.enset.comptecqrses.commonapi.events.AccountCreditedEvent;
import ma.enset.comptecqrses.commonapi.events.AccountDebitedEvent;
import ma.enset.comptecqrses.commonapi.queries.GetAllAccountsQuery;
import ma.enset.comptecqrses.query.controllers.GetAccountByIdQuery;
import ma.enset.comptecqrses.query.entities.Account;
import ma.enset.comptecqrses.query.entities.Operation;
import ma.enset.comptecqrses.query.repositories.AccountRepository;
import ma.enset.comptecqrses.query.repositories.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    @EventHandler
    public void on(AccountCreatedEvent event){
        log.info("********************************");
        log.info("AccountCreatedEvent received");
        Account account = new Account();
        account.setId(event.getId());
        account.setCurrency(event.getCurrency());
        account.setStatus(event.getStatus());
        account.setBalance(event.getInitialBalance());
        accountRepository.save(account);
    }

/*    @EventHandler
    public void on(AccountActivatedEvent event){
        log.info("********************************");
        log.info("AccountActivatedEvent received");
        Account account=accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);

    }*/
     @EventHandler
     public void on(AccountDebitedEvent event){
        log.info("********************************");
        log.info("AccountDebitedEvent received");
        Account account=accountRepository.findById(event.getId()).get();
         Operation operation = new Operation();
         operation.setAmount(event.getAmount());
         operation.setDate(new Date()); //à ne pas faire
         operation.setType(OperationType.DEBIT);
         operation.setAccount(account);
         operationRepository.save(operation);
        account.setBalance(account.getBalance()- event.getAmount());

        accountRepository.save(account);

}

    @EventHandler
    public void on(AccountCreditedEvent event){
        log.info("********************************");
        log.info("AccountDebitedEvent received");
        Account account=accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date()); //à ne pas faire
        operation.setType(OperationType.CREDIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()+event.getAmount());

        accountRepository.save(account);

    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
            return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountByIdQuery query){
        return accountRepository.findById(query.getId()).get();
    }
}
