package com.fmning.authentication.repository;

import com.fmning.authentication.domain.ClientDetail;
import org.springframework.data.repository.CrudRepository;

public interface ClientDetailRepo extends CrudRepository<ClientDetail, String> {
}
