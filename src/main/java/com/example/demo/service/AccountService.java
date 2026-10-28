package com.example.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok gives us log.info(), log.error() etc

public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request){
        if(accountRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email Already registered: "+ request.getEmail());
        }

        if(accountRepository.existsByphone(request.getPhone())){
            throw new RuntimeException("Phone number  already exists: "+ request.getPhone());
        }

        Account account =  new Account();
        account.setName(request.getName());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setBalance(request.getInitialBalance());
        account.setActive(true);

        Account saved = accountRepository.save(account);
        log.info("Account  created :id={}, name ={}", saved.getId(),saved.getName());

        return new AccountResponse(saved);
    }
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id){
        Account account = accountRepository.findById(id)
        .orElseThrow(()-> new AccountNotFoundException(
            "Account not found with id:"+ id));
            return new AccountResponse(account);
    }
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts(){
        return accountRepository.findAll().stream().map(AccountResponse::new).toList();
    }

}
