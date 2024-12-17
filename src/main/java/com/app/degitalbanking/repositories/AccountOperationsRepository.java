package com.app.degitalbanking.repositories;

import com.app.degitalbanking.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationsRepository extends JpaRepository<AccountOperation,Long> {
}
