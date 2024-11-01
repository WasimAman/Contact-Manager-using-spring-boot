package com.contactmanager.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactmanager.entities.Contact;
@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Page<Contact> findByUserId(int userId,Pageable pageable);
}
