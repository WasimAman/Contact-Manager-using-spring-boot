package com.contactmanager.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.contactmanager.entities.Contact;
import com.contactmanager.repositories.ContactRepository;

/**
 * ContactService
 */
@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    public Contact saveContact(Contact contact){
        return contactRepository.save(contact);
    }

    public Page<Contact> getContacts(int userId,Pageable pageable){
        return contactRepository.findByUserId(userId,pageable);
    }

    public Contact getContactById(int contactId){
        return contactRepository.findById(contactId).get();
    }

    public void deleteById(int id){
        contactRepository.deleteById(id);
    }

    public void  updateContact(Contact contact){
        contactRepository.save(contact);
    }
}