package com.app.degitalbanking.web;


import com.app.degitalbanking.dtos.CustomerDTO;
import com.app.degitalbanking.entities.Customer;
import com.app.degitalbanking.exception.CustomerNotFoundException;
import com.app.degitalbanking.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping(path = "/customer")
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers()
    {
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword)
    {
        return bankAccountService.searchCutomers("%"+keyword+"%");
    }
    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customer_id) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customer_id);
    }

    @PostMapping("/customers")
    public CustomerDTO createCustomer(@RequestBody CustomerDTO request) {
        return bankAccountService.saveCustomer(request);
    }
    @PutMapping("/customers/{id}")
    public CustomerDTO updateCustomer(@PathVariable(name = "id") Long customerId,@RequestBody CustomerDTO request) {
        request.setId(customerId);
        return bankAccountService.updateCustomer(request);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Long customer_id) {

        bankAccountService.deleteCustomer(customer_id);
    }

}
